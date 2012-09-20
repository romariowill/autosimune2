package autosimmune.agents.cells;

import repast.simphony.annotate.AgentAnnot;
import repast.simphony.engine.schedule.ScheduledMethod;
import autosimmune.agents.pathogens.Virus;
import autosimmune.agents.portals.Portal;
import autosimmune.defs.CitokineNames;
import autosimmune.defs.DendriticStates;
import autosimmune.defs.EnvParameters;
import autosimmune.defs.ZoneNames;
import autosimmune.env.Environment;
import autosimmune.env.Global;
import autosimmune.utils.Pattern;

/**
 * Classe que representa a célula Dendrítica
 * @author maverick
 *
 */
@AgentAnnot(displayName = "Dendritic Cell")
public class Dendritic extends APC {

	/** Estado atual da célula dendritica */
	private DendriticStates state;
	
	/** Numero de iteracoes da celula dendritica no linfonodo antes de cessar a movimentação */
	private int ticksInLynphnode;
	
	/** Tempo de vida da célula dendritica no linfonodo */
	private int lifeInLynphnode;
	
	private float pk1ActivationThreshold;
	
	private float mk1ActivationThreshold;
	
	private int tissueMK1Duration;
	
	private int lymphnodeMK1Duration;
	
	private boolean antigenFromCell = false;

	/**
	 * Construtor da Classe
	 * @param z Zona a qual ela pertence
	 * @param self Peptídeo self das Células Dendríticas
	 * @param x Coordenada X inicial
	 * @param y Coordenada Y inicial
	 */
	public Dendritic(Environment z, int x, int y) {
		super(z, x, y, new Pattern(Global.getInstance().getStringParameter(EnvParameters.DENDRITIC_SELF_PATTERN)));
		state = DendriticStates.SURVEILLANCE;
		initiateParameters();
	}

	private void initiateParameters(){
		this.lifeInLynphnode = Global.getInstance().getIntegerParameter(EnvParameters.DENDRITIC_LYMPHNODE_LIFETIME);
		this.pk1ActivationThreshold = Global.getInstance().getFloatParameter(EnvParameters.DENDRITIC_PK1_ACTIVATION_THRESHOLD);
		this.mk1ActivationThreshold = Global.getInstance().getFloatParameter(EnvParameters.DENDRITIC_MK1_ACTIVATION_THRESHOLD);
		this.tissueMK1Duration = Global.getInstance().getIntegerParameter(EnvParameters.DENDRITIC_TISSUE_MK1_DURATION);
		this.lymphnodeMK1Duration = Global.getInstance().getIntegerParameter(EnvParameters.DENDRITIC_LYMPHNODE_MK1_DURATION);
	}
	
	/**
	 * Executa um passo da celula
	 */
	@ScheduledMethod(start = 0, interval = 1)
	public void step() {
		
		tick();
	
		switch(state){
			case SURVEILLANCE: {
				randomWalk();
				if (getCitokineValue(CitokineNames.PK1) >= pk1ActivationThreshold || getCitokineValue(CitokineNames.MK1) >= mk1ActivationThreshold){
					this.state = DendriticStates.ACTIVETED;
				}
			} break;
			
			case ACTIVETED: {
				followCitokineByGradient(true, CitokineNames.PK1);
				
				if(tissueMK1Duration > 0){
					releaseCitokine(CitokineNames.MK1);
					tissueMK1Duration--;
				}
				
				if (getCitokineValue(CitokineNames.PK1) < pk1ActivationThreshold){
					this.state = DendriticStates.SURVEILLANCE;
					break;
				}
				
				for(Virus v: getEspecificNeighbors(Virus.class)){
					
					if(v!=null){
						this.antigen = v.getSelf();
						if(v.neutralize()){ //sucesso em fagocitar o virus
							this.state = DendriticStates.MIGRATING;
						}
						return;
					}
				}
				
				for(PC pc: getEspecificNeighbors(PC.class)){
					if(pc.isInfected()){
						//o antigeno capturado de uma celula infectada processada eh similar ao 
						//antigeno apresentado pelo MHCI por esta mesma celula fagocitada
						//neste caso, a celula dendritica esta apresentando auto-antigenos tambem
						this.antigen = pc.MHCI();
						pc.clean();
						this.state = DendriticStates.MIGRATING;
						antigenFromCell = true;
						return;
					} else if (pc.isDead()){
						pc.clean();
					}
				}
			} break;
			
			case MIGRATING: {
				followCitokineByGradient(true, CitokineNames.MK1);
				if(getCitokineValue(CitokineNames.MK1)>2){
					Portal.transportToZone(this, ZoneNames.lymphnode, 10, 10);
					this.state = DendriticStates.PRESENTING;
				}
			} break;
			
			case PRESENTING: {
				
				lifeInLynphnode--;
				if (lifeInLynphnode <= 0){
					this.state = DendriticStates.APOPTOSIS;
					return;
				}
				
				//move aleatorimante apenas um pouco, entao fica estacionaria
				if (ticksInLynphnode < 100){
					ticksInLynphnode++;
					randomWalk();
				}
				
				//mas se sentir que esta muito proxima de outra celula dendritica
				//entao anda mais um pouco aleatoriamente, no intuito de formar
				//uma rede bem homogenea de celulas dendriticas
				if (!getEspecificNeighbors(Dendritic.class, 2).isEmpty()){
					ticksInLynphnode-=3;
				}
				
				if(lymphnodeMK1Duration > 0){
					releaseCitokine(CitokineNames.MK1);
					lymphnodeMK1Duration--;
				}
				
			} break;
			
			case APOPTOSIS: {
				
				apoptosis();
				
			} break;
		}
	}
	
	

	@Override
	public boolean infectedBy(Virus virus) {
		return false;
	}

	@Override
	public void contact(boolean b) {
		lifeInLynphnode+=10;
		lymphnodeMK1Duration += 10;
		if(antigenFromCell){
			System.out.println("Célula dendrítica apresentou antígeno de células fagocitadas...");
		}
	}

}
