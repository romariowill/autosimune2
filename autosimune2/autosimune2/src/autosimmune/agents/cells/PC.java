package autosimmune.agents.cells;

import repast.simphony.annotate.AgentAnnot;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.parameter.Parameter;
import autosimmune.agents.pathogens.Virus;
import autosimmune.defs.CitokineNames;
import autosimmune.defs.EnvParameters;
import autosimmune.defs.PCStates;
import autosimmune.env.Environment;
import autosimmune.env.Global;
import autosimmune.utils.Pattern;

/**
 * Classe que define uma célula de parenquima
 * @author maverick 
 * @version 0.1
 */
@AgentAnnot(displayName = "PC Cell")
public class PC extends Cell{

	/** Estado atual da celula */
	private PCStates state;
	
	/** Estado anterior da celula */
	private PCStates previous_state;
	
	/** Guarda a informacao de quando a celula se tornou estressada */
	private int stressedStartTick;
	
	/** Tempo máximo que uma célula fica stressada */
	private int stressedMaxTime;
	
	/** tamanho do quarteirão. Usado na regeneracao celular */
	private int quarter;
	
	private boolean regenerated = false;
	
	private boolean canReproduce = false;
	
	private int pcStressThreshold;
	
	private PC(Environment zone, int x, int y, boolean reg){
		super(zone, x, y, new Pattern(Global.getInstance().getStringParameter(EnvParameters.PC_SELF_PATTERN)));
		state = PCStates.NORMAL;
		previous_state = PCStates.NORMAL;
		this.regenerated = true;
		this.canReproduce = true;
		initiateParameters();
	}
	
	/**
	 * Construtor da Celula de Parenquima
	 * @param zone Zona a qual a celula pertence
	 * @param pep Peptideo self
	 * @param x Coordenada X inicial
	 * @param y Coordenada Y inicial
	 */
	public PC(Environment zone, int x, int y){
		super(zone, x, y, new Pattern(Global.getInstance().getStringParameter(EnvParameters.PC_SELF_PATTERN)));
		state = PCStates.NORMAL;
		previous_state = PCStates.NORMAL;
		initiateParameters();
	}
	
	private void initiateParameters(){
		this.quarter = Global.getInstance().getIntegerParameter(EnvParameters.TISSUE_QUARTER);
		this.pcStressThreshold = Global.getInstance().getIntegerParameter(EnvParameters.PC_STRESS_TRHESHOLD);
		this.stressedMaxTime = Global.getInstance().getIntegerParameter(EnvParameters.PC_STRESS_MAX_TIME);
	}
	
	public boolean isRegenerated(){
		return this.regenerated;
	}
	
	
	@SuppressWarnings("deprecation")
	private void reproduce() {
		if(!canReproduce){
			return;
		}
		for(int i = -1; i<=1; i++){
			for(int j = -1; j<=1; j++){
				if (i==0 && j==0){
					continue;
				}
				int xi = this.getX() + i;
				int yj = this.getY() + j;
				if ( (xi % quarter == 0 || xi % quarter == 1) || (yj % quarter == 0 || yj % quarter == 1) ){
					if( this.getEspecificAgentsAt(PC.class, xi, yj).size() == 0){
						zone.addAgent(new PC(this.zone, xi, yj, true));
					}
				}
			}
		}
	}

	/**
	 * Altera o estado atual da celula
	 * @param newstate
	 */
	private void changeState(PCStates newstate){
		//se a celula estiver morta, nao vai pra estado nenhum
		if (state == PCStates.APOPTOSIS || state == PCStates.NECROSIS) {
			//return;
		}
		previous_state = state;
		state = newstate;
	}
	
	/**
	 * Metodo responsavel pela acao da celula.
	 * Faz a celula agir de acordo com seu estado interno, e as influencias exteriores
	 */
	@ScheduledMethod(start = 0, interval = 1)
	public void step() {
		
		//incrimenata ticks
		tick();
		
		//se foi infectada por virus, executa o DNA do virus
		if (this.virus != null){
			virus.executeDNA(this);
		}
		
		//por questoes de desempenho, uma celula PC so pode tentar se reproduzir (canReproduce)
		//se houver evidencias que uma de suas vizinhas foi morta. Por exemplo, se ela detectar esses
		//sinais, entao pode ser que alguma vizinha tenha sido morta.
		//isso impede que celulas que nao estao participando da infeccao em si percam tempo com a funcao
		//de reproducao, que eh muito custosa
		if(getCitokineValue(CitokineNames.PK1) >= 1 || 
				getCitokineValue(CitokineNames.APOPTOSIS) >= 1 ||
				getCitokineValue(CitokineNames.NECROTIC) >= 1){
			this.canReproduce = true;
		}
		
		switch(state){
			
			case NORMAL: {

				if (getCitokineValue(CitokineNames.NECROTIC) >= pcStressThreshold){
					changeState(PCStates.STRESSED);
					canReproduce = true;
					break;
				}
				this.reproduce();
				changeState(PCStates.NORMAL);
			}
			break;
			
			case STRESSED: {
				releaseCitokine(CitokineNames.PK1);
				if ( (this.virus == null) && (getCitokineValue(CitokineNames.NECROTIC) < pcStressThreshold) && (getTicks() > stressedStartTick + stressedMaxTime)){
					changeState(PCStates.NORMAL);
					break;
				}
				changeState(PCStates.STRESSED);
			}
			break;
			
			case NECROSIS: {
				if(this.previous_state != PCStates.NECROSIS){
					releaseCitokine(CitokineNames.NECROTIC);
				}
				
				//TODO parametrizar taxa de liberacao da citocina NECROTIC por celula morta
				if(getTicks() % 20 == 0){
					releaseCitokine(CitokineNames.NECROTIC);
				}
				if (this.virus != null){
					this.virus.removeHost(this);
					this.virus.neutralize();
					this.virus = null;
				}
				changeState(PCStates.NECROSIS);
			}break;
			
			case APOPTOSIS: {
				if (this.virus != null){
					this.virus.removeHost(this);
					this.virus.neutralize();
					this.virus = null;
				}
				changeState(PCStates.APOPTOSIS);
				this.die();
			}break;
		
		}
	}

	/**
	 * Define uma celula de parenquima como stressada
	 */
	public void setStressed() {
		if (this.state != PCStates.NECROSIS && this.state != PCStates.APOPTOSIS){
			stressedStartTick = getTicks();
			changeState(PCStates.STRESSED);
		}
	}
	
	public boolean isInfected(){
		return (this.state != PCStates.APOPTOSIS && this.state != PCStates.NECROSIS && this.virus != null);
	}
	
	/**
	 * Obtem o estado atual da celula
	 * @return Estado da Celula
	 */
	public PCStates getState(){
		return this.state;
	}

	/**
	 * Obtem o nome do estado atual da celula
	 * @return
	 */
	@Parameter(usageName="state", displayName="Cells State: ")
	public String getStateName(){
		return this.state.name();
	}
	
	/**
	 * Mata a celula agressivamente
	 */
	@Override
	public void necrosis() {
		changeState(PCStates.NECROSIS);
	}
	
	/**
	 * Solicita que a célula morra atraves de morte celular programada
	 */
	@Override
	public void apoptosis() {
		changeState(PCStates.APOPTOSIS);
	}

	@Override
	public boolean infectedBy(Virus virus) {
		if (this.virus == null && this.state != PCStates.NECROSIS && this.state != PCStates.APOPTOSIS){
			this.virus = virus;
			this.setStressed();
			return true;
		}
		return false;
	}

	/**
	 * Informa se a célula está stressada
	 * @return
	 */
	public boolean isStressed() {
		return (this.state == PCStates.STRESSED);
	}

	/**
	 * Informa se a célula está morta
	 * @return
	 */
	public boolean isDead() {
		return (this.state == PCStates.APOPTOSIS || this.state == PCStates.NECROSIS);
	}

	/**
	 * Remove uma celula morta do tecido.
	 * Funcao chamada quando um macrofago remove a celula morta
	 */
	@Override
	public void clean() {
		//if(this.isDead()){
			if (this.virus != null){
				this.virus.removeHost(this);
				this.virus.neutralize(true);
				this.virus = null;
			}
			zone.removeAgent(this);
		//}
	}
}
