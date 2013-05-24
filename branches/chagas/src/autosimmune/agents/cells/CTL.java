package autosimmune.agents.cells;

import repast.simphony.engine.schedule.ScheduledMethod;
import autosimmune.agents.portals.Portal;
import autosimmune.defs.CitokineNames;
import autosimmune.defs.EnvParameters;
import autosimmune.defs.SELF;
import autosimmune.defs.TCLStates;
import autosimmune.defs.ZoneNames;
import autosimmune.env.Environment;
import autosimmune.env.Global;
import autosimmune.utils.Affinity;
import autosimmune.utils.Pattern;

/**
 * Agente CTL, representa o linfocito T Citolitico (CD8+)
 * @author Maverick
 *
 */
public class CTL extends Cell implements Lymphocyte {
	
	/** Estado da celula */
	private TCLStates state;
	
	/** Padrão que a celula ira reconhecer e atacar */
	private Pattern target;
	
	/** Tempo de vida da celula no tecido */
	private int lifeTissue;
	
	/** Tempo de vida da celula no linfonodo */
	private int lifeLymphnode;
	
	/** Quantidade maxima de liberacao de CK1 no linfonodo */
	private int durationCK1Lymphnode;
	
	/** Quantidade de CK1 liberada no linfonodo */
	private int releasedCK1Lymphnode;
	
	/** Quantidade maxima de liberacao de CKI no tecido */
	private int durationCK1Tissue;
	
	/** Quantidade de CK1 liberada no tecido */
	private int releasedCK1Tissue;
	
	private boolean especific = false;
	
	private boolean autoimmune = false;
	
	private boolean calculedAutoimmune = false;
	
	private boolean calculedEspecific = false;
	
	private int autoimmuneCellKill = 0;
	
	private float ck1MemoryThreshold;
	
	private float pk1MemoryThreshold;
	
	private int proliferationCount;

	/**
	 * Construtor publico
	 * @param z Environment Ambiente em que a celula esta
	 * @param x int Posicao inicial x
	 * @param y int Posicao inicial y
	 * @param target Pattern Alvo da celula
	 */
	public CTL(Environment z, int x, int y, Pattern target) {
		super(z, x, y, new Pattern(Global.getInstance().getStringParameter(EnvParameters.CTL_SELF_PATTERN)));		
		this.target = target;
		this.state = TCLStates.INACTIVE;
		initiateParameters();
	}
	
	private void initiateParameters(){
		/* int lifeTissue, int lifeLymphnode, int durationCK1Lymphnode, int durationCK1Tissue, int memoryThreshold */
		this.lifeLymphnode = Global.getInstance().getIntegerParameter(EnvParameters.CTL_LYMPHNODE_LIFETIME);
		this.lifeTissue = Global.getInstance().getIntegerParameter(EnvParameters.CTL_TISSUE_LIFETIME);
		this.durationCK1Lymphnode = Global.getInstance().getIntegerParameter(EnvParameters.CTL_LYMPHNODE_CK1_DURATION);
		this.durationCK1Tissue = Global.getInstance().getIntegerParameter(EnvParameters.CTL_TISSUE_CK1_DURATION);
		this.pk1MemoryThreshold = Global.getInstance().getFloatParameter(EnvParameters.CTL_PK1_MEMORY_THRESHOLD);
		this.ck1MemoryThreshold = Global.getInstance().getFloatParameter(EnvParameters.CTL_CK1_MEMORY_THRESHOLD);
		this.proliferationCount = Global.getInstance().getIntegerParameter(EnvParameters.CTL_PROLIFERATION_COUNT);
	}
	
	/*
	@Parameter(usageName="target", displayName="CTL Target Antigen: ")
	public String getTarget(){
		return this.target.getEpitope();
	}
	*/
	
	private CTL(Environment z, int x, int y, Pattern target, TCLStates state) {
		super(z, x, y, new Pattern(Global.getInstance().getStringParameter(EnvParameters.CTL_SELF_PATTERN)));
		this.target = target;
		this.state = state;
		initiateParameters();
	}


	@ScheduledMethod(start = 60, interval = 1)
	public void step() {
		
		tick();
		
		switch(state){
			
			case INACTIVE: {
				
				randomWalk();
				
				for(APC d : getEspecificNeighbors(APC.class)){
					//FIXME o CTL examina o MHCI
					Pattern antigen = d.MHCII();

					if (Affinity.match(target, antigen)){
						d.contact(true);
						this.state = TCLStates.ACTIVE;
						for(int i = 0; i<proliferationCount; i++){
							CTL ctl = new CTL(this.zone, getX(), getY()+1, this.target, this.state);
							//TODO fazer passar pela Circulation
							Portal.transportToZone(ctl, ZoneNames.Tissue, 150, 150);
						}
						return;
					} else {
						d.contact(false);
					}
				}
				
			} break;
		

			case ACTIVE: {
				
				//tarefas a serem executadas se estiver no linfonodo
				if(this.zone.getEnvName().equals(ZoneNames.Lymphnode)){
					
					lifeLymphnode--;
					if(lifeLymphnode <= 0){
						this.state = TCLStates.APOPTOSIS;
					}
					
					if( durationCK1Lymphnode - releasedCK1Lymphnode > 0) {
						releaseCitokine(CitokineNames.CK1);
						releasedCK1Lymphnode++;
					}
					
					if (getCitokineValue(CitokineNames.CK1) <= ck1MemoryThreshold){
						for(APC d : getEspecificNeighbors(APC.class)){
							//FIXME o CTL examina o MHCI
							Pattern antigen = d.MHCII();

							if (Affinity.match(target, antigen)){
								d.contact(true);
								this.state = TCLStates.MEMORY;
								return;
							}
						}
					}
				} else if (this.zone.getEnvName().equals(ZoneNames.Tissue)){ //se estiver no tecido
					
					lifeTissue--;
					if(lifeTissue <= 0){
						this.state = TCLStates.APOPTOSIS;
					}
					
					if( durationCK1Tissue - releasedCK1Tissue > 0) {
						releaseCitokine(CitokineNames.CK1);
						releasedCK1Tissue++;
					}
					
					followCitokineByGradient(true, CitokineNames.PK1);
					
					for(PC pc: getEspecificNeighbors(PC.class)){
						if(!pc.isDead()){
							Pattern antigenos = pc.MHCI();
							if(Affinity.match(this.target, antigenos)){
								if(!pc.isInfected()){
									autoimmuneCellKill++;
								}
								pc.apoptosis();
								releasedCK1Tissue = 0;
								lifeTissue++;
							}
						}							
					}
					
					if(getCitokineValue(CitokineNames.CK1) <= ck1MemoryThreshold && getCitokineValue(CitokineNames.MK1) <= pk1MemoryThreshold){
						this.state = TCLStates.MEMORY;
					}
					
				} else { //circulacao
					randomWalk();
				}
			} break;
			
			
			case MEMORY: {
				if(this.zone.getEnvName().equals(ZoneNames.Lymphnode)){
					followCitokineByGradient(true, CitokineNames.MK1);
						for(APC d : getEspecificNeighbors(APC.class)){
							//FIXME o CTL examina o MHCI
							Pattern antigen = d.MHCII();
							if (Affinity.match(target, antigen)){
								d.contact(true);
								this.state = TCLStates.ACTIVE;
								return;
							}
						}
				} else if(this.zone.getEnvName().equals(ZoneNames.Tissue)){
					followCitokineByGradient(true, CitokineNames.PK1);
					for(PC pc: getEspecificNeighbors(PC.class)){
						if (pc.isStressed()){
							Pattern antigenos = pc.MHCI();
							if(Affinity.match(this.target, antigenos)){
								pc.apoptosis();
								releasedCK1Tissue = 0;
								lifeTissue++;
								this.state = TCLStates.ACTIVE;
							}
						}
					}
				} else { //circulacao
					randomWalk();
				}
			}

			
			case APOPTOSIS: {
				apoptosis();
			} break;
			
			
		}
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
	
	public boolean isAutoimunne(){
		if(!calculedAutoimmune){
			for(SELF self: SELF.values()){
				if(Affinity.match(self.getPattern(), target)){
					autoimmune = true;
					calculedAutoimmune = true;
					return true;
				}
			}
			autoimmune = false;
			calculedAutoimmune = false;
			return false;
		} else {
			return autoimmune;
		}
	}

	@Override
	public boolean isActive() {
		return (this.state.equals(TCLStates.ACTIVE));
	}

	public int getAutoimmuneCellKill() {
		return autoimmuneCellKill;
	}
}
