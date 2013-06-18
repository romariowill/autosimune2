package autosimmune.agents.cells;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.parameter.Parameter;
import autosimmune.agents.portals.Portal;
import autosimmune.defs.CitokineNames;
import autosimmune.defs.EnvParameters;
import autosimmune.defs.TRegCellStates;
import autosimmune.defs.ThCellStates;
import autosimmune.defs.ZoneNames;
import autosimmune.env.Environment;
import autosimmune.env.Global;
import autosimmune.utils.Affinity;
import autosimmune.utils.Pattern;

public class ThCell extends Cell implements Lymphocyte {

	
	private ThCellStates state;
	
	private Pattern target;
	
	private int lifetime;
	
	private int durationCK;
	
	private boolean especific = false;
	
	private boolean calculedEspecific = false;
	
	private int proliferationCount;
	
	private int ck1MemoryThreshold;
	
	private int memoryProliferationCount;
	
	private int ck2ApoptosisLimit;
	
	private static int thCount = 0;
	private static int thActivatedCount = 0;
	
	public ThCell(Environment z, int x, int y, Pattern target) {
		super(z, x, y, new Pattern(Global.getInstance().getStringParameter(EnvParameters.TH_SELF_PATTERN)));
		this.state = ThCellStates.INACTIVE;
		this.target = target;
		initiateParameters();
	}
	
	private void initiateParameters(){
		this.lifetime = Global.getInstance().getIntegerParameter(EnvParameters.TH_LIFETIME);
		this.durationCK = Global.getInstance().getIntegerParameter(EnvParameters.TH_CK1_DURATION);
		this.proliferationCount = Global.getInstance().getIntegerParameter(EnvParameters.TH_PROLIFERATION_COUNT);
		this.ck1MemoryThreshold = Global.getInstance().getIntegerParameter(EnvParameters.TH_CK1_MEMORY_THRESHOLD);
		this.memoryProliferationCount = Global.getInstance().getIntegerParameter(EnvParameters.TH_MEMORY_PROLIFERATION_COUNT);
		this.ck2ApoptosisLimit = Global.getInstance().getIntegerParameter(EnvParameters.TH_CK2_APOPTOSIS_LIMIT);
		//this.setThCount(this.getThCount() + 1);
	}
	
	private ThCell(Environment z, int x, int y, Pattern target, ThCellStates initialState) {
		super(z, x, y, new Pattern(Global.getInstance().getStringParameter(EnvParameters.TH_SELF_PATTERN)));
		this.state = initialState;
		this.target = target;
		initiateParameters();
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
	    //System.out.println("ThCount: " + this.getThCount());
	    //System.out.println("ThActive: " + this.getThActiveCount());
		switch(state){
			
			case INACTIVE: {

				randomWalk();
				
				for(APC d : getEspecificNeighbors(APC.class)){
					Pattern antigen = d.MHCII();
					/*System.out.println("Th Encontrou com APC ");
					if(antigen!=null)
						System.out.print(" Ant: "+antigen.getEpitope()+ " - ");
					if(target!=null)
						System.out.print(" Tar: "+target.getEpitope()+ " - ");*/
					if (Affinity.match(target, antigen)){
						//System.out.print("\n\n\n\n\n\n\n-----------------------------------\n\n\n\n\n\n");
						d.contact(true);
						this.state = ThCellStates.ACTIVE;
						ThCell.setThActiveCount(thActivatedCount+1);
						//System.out.println("Th Ativados: " + ThCell.getThActiveCount());
						for(int i = 0; i < proliferationCount; i++){
							ThCell th = new ThCell(this.zone, getX()+1, getY(), this.target, this.state);
							this.zone.add(th);
							ThCell.setThActiveCount(thActivatedCount+1);
							//TODO fazer passar pela Circulation
							Portal.transportToZone(th, ZoneNames.Tissue, 150, 150);
						}
						//System.out.println("Th Ativados: " + ThCell.getThActiveCount());
						return;
					} else {
						d.contact(false);
					}
				}
				
			} break;
			
			
			case ACTIVE: {
				
				if(durationCK > 0){
					//fara as celulas B virem de encontro a ela
					releaseCitokine(CitokineNames.CK1);
				}
				
				if(getCitokineValue(CitokineNames.CK1) < ck1MemoryThreshold){
					this.state =  ThCellStates.MEMORY;
					ThCell.setThActiveCount(thActivatedCount-1);
					return;
				}
				
				/*Caso a quantidade de CK2 seja maior que o valor especificado no 
				 * parametro ck2ApoptosisLimit, a célula entra em Apoptose
				 */	
				//TODO Fazer estado de Anergia
				if(getCitokineValue(CitokineNames.CK2) > ck2ApoptosisLimit){
					this.state =  ThCellStates.APOPTOSIS;
					ThCell.setThActiveCount(thActivatedCount-1);
					return;
				}
				
				//segue as APCs no linfonodo ou no tecido
				followCitokineByGradient(true, CitokineNames.MK1);
				

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
					this.state = ThCellStates.APOPTOSIS;
					ThCell.setThActiveCount(thActivatedCount-1);
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
						this.state = ThCellStates.ACTIVE;
						ThCell.setThActiveCount(thActivatedCount+1);
						for(int i = 0; i < memoryProliferationCount; i++){
							ThCell th = new ThCell(this.zone, getX()+1, getY(), this.target, this.state);
							this.zone.add(th);
							Portal.transportToZone(th, ZoneNames.Tissue, 150, 150);
							ThCell.setThActiveCount(thActivatedCount+1);
						}
						//System.out.println("Th Ativados: " + ThCell.getThActiveCount());
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

	/*
	public boolean isActivated() {
		return (this.state.equals(ThCellStates.ACTIVE));
	}
	*/

	@Parameter(usageName="target", displayName="ThCell Target Antigen: ")
	public Pattern getTargetPattern() {
		return this.target;
	}

	@Override
	public boolean isActive() {
		return (this.state.equals(ThCellStates.ACTIVE));
	}
	

	public static int getThCount() {
		return thCount;
	}

	public static void setThCount(int thCount) {
		ThCell.thCount = thCount;
	}
	
	public static synchronized int getThActiveCount() {
		return thActivatedCount;
	}

	public static synchronized void setThActiveCount(int thCount) {
		ThCell.thActivatedCount = thCount;
	}
}
