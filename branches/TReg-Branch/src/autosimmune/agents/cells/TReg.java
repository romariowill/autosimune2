package autosimmune.agents.cells;

import repast.simphony.engine.schedule.ScheduledMethod;
//import repast.simphony.parameter.Parameter;
import autosimmune.agents.portals.Portal;
import autosimmune.defs.CitokineNames;
import autosimmune.defs.EnvParameters;
import autosimmune.defs.TRegCellStates;
import autosimmune.defs.ZoneNames;
import autosimmune.env.Environment;
import autosimmune.env.Global;
import autosimmune.utils.Affinity;
import autosimmune.utils.Pattern;

public class TReg extends Cell implements Lymphocyte{
	
    private TRegCellStates state;
	
	private Pattern target;
	
	private int lifetime;
	
	private int durationCK;
	
	private boolean especific = false;
	
	private boolean calculedEspecific = false;
	
	private int proliferationCount;
	
	private int ck1MemoryThreshold;
	
	private int memoryProliferationCount;
	
	static private int tRegCount = 0;
	
	
	//Setando o construtor da classe mae 
	public TReg(Environment z, int x, int y, Pattern target)
	{
		super(z, x, y, new Pattern(Global.getInstance().getStringParameter(EnvParameters.TREG_SELF_PATTERN)));
		state = TRegCellStates.INACTIVE;
		this.target = target;
		initiateParameters();
	}
	
	private TReg(Environment z, int x, int y, Pattern target, TRegCellStates initialState) {
		super(z, x, y, new Pattern(Global.getInstance().getStringParameter(EnvParameters.TREG_SELF_PATTERN)));
		this.state = initialState;
		this.target = target;
		initiateParameters();
	}
	
	//Inicializando os parâmetros
	private void initiateParameters(){
		this.lifetime = Global.getInstance().getIntegerParameter(EnvParameters.TREG_LIFETIME);
		//TReg lança citocinas anti inflamatórias (CK2)
		this.durationCK = Global.getInstance().getIntegerParameter(EnvParameters.TREG_CK2_DURATION);
		this.proliferationCount = Global.getInstance().getIntegerParameter(EnvParameters.TREG_PROLIFERATION_COUNT);
		this.ck1MemoryThreshold = Global.getInstance().getIntegerParameter(EnvParameters.TREG_CK1_MEMORY_THRESHOLD);
		this.memoryProliferationCount = Global.getInstance().getIntegerParameter(EnvParameters.TREG_MEMORY_PROLIFERATION_COUNT);
		this.settRegCount(this.gettRegCount() + 1);
	}
	
	
	@Override
	public boolean isActive() {
		return (this.state.equals(TRegCellStates.ACTIVE));
	}
	
	public boolean isEspecific(Pattern p){
		if(!calculedEspecific){
			if(Affinity.match(this.target, p)){
				calculedEspecific = true;
				especific = true;
				return true;
			} else {
				calculedEspecific = true;
				especific = false;
				return false;
			}
		} else {
			return especific;
		}
	}
	
	/**
	 * Método que executa o passo do linfócito a cada tick
	 */
	@ScheduledMethod(start = 40, interval = 1)
	public void step(){

		tick();
		
		switch(state){
			
			case INACTIVE: {

				randomWalk();
				
				for(APC d : getEspecificNeighbors(APC.class)){
					Pattern antigen = d.MHCII();
					System.out.println("TReg Encontrou com APC");
					if (Affinity.match(target, antigen)){
						d.contact(true);
						this.state = TRegCellStates.ACTIVE;
						System.out.println("TReg Ativado");
						for(int i = 0; i < proliferationCount; i++){
							TReg treg = new TReg(this.zone, getX()+1, getY(), this.target, this.state);
							this.zone.add(treg);
							
							//TODO fazer passar pela Circulation
							Portal.transportToZone(treg, ZoneNames.Tissue, 150, 150);
						}
						return;
					} else {
						d.contact(false);
					}
				}
				
			} break;
			
			
			case ACTIVE: {
				
				if(durationCK > 0){
					//Lança Citocinas inibitórias (anti inflamatórias)
					releaseCitokine(CitokineNames.CK2);
				}
				
				if(getCitokineValue(CitokineNames.CK1) < ck1MemoryThreshold){
					this.state =  TRegCellStates.MEMORY;
					return;
				}
				
				//segue as APCs no linfonodo ou no tecido
				followCitokineByGradient(true, CitokineNames.MK1);
				

				/*Duvida: Ao encontrar uma APC,o TReg libera IL-2 como o Thelper?*/
				
				for(APC d : getEspecificNeighbors(APC.class)){
					Pattern antigen = d.MHCII();

					if (Affinity.match(target, antigen)){
						d.contact(true);
						//reinicia liberacao de CK1
						durationCK = Global.getInstance().getIntegerParameter(EnvParameters.TH_CK1_DURATION);
						return;
					} else {
						d.contact(false);
					}
				}
				
				if(lifetime <= 0){
					this.state = TRegCellStates.APOPTOSIS;
				} else {
					lifetime--;
				}
				
			} break;
			
			case MEMORY: {
				
				randomWalk();
				
				for(APC d : getEspecificNeighbors(APC.class)){
					Pattern antigen = d.MHCII();

					if (Affinity.match(target, antigen)){
						d.contact(true);
						this.state = TRegCellStates.ACTIVE;
						for(int i = 0; i < memoryProliferationCount; i++){
							TReg treg = new TReg(this.zone, getX()+1, getY(), this.target, this.state);
							this.zone.add(treg);
							Portal.transportToZone(treg, ZoneNames.Tissue, 150, 150);
						}
						return;
					} else {
						d.contact(false);
					}
				}
			} break;
			
			case APOPTOSIS: {
				apoptosis();
			} break;
		}
	}
	

	public static int gettRegCount() {
		return tRegCount;
	}

	public static void settRegCount(int tReg) {
		TReg.tRegCount = tReg;
	}
	
}
