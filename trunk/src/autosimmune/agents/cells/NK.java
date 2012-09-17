package autosimmune.agents.cells;

import repast.simphony.annotate.AgentAnnot;
import repast.simphony.engine.schedule.ScheduledMethod;
import autosimmune.defs.CitokineNames;
import autosimmune.defs.EnvParameters;
import autosimmune.defs.NKStates;
import autosimmune.env.Environment;
import autosimmune.env.Global;
import autosimmune.utils.Pattern;

@AgentAnnot(displayName = "Natural Killer Cell")
public class NK extends Cell {

	
	/** estado da celula */
	private NKStates state;
	
	/** numero maximo de celulas que este agente pode matar */
	private int killLimit;
	
	/** tempo de vida no tecido */
	private int lifetime;
	
	/** quantidade de citocina 1 que ele pode liberar */
	private int durationCK1;
	private int countCK1 = 0;
	
	/** tempo que pode ficar ativa sem matar nenhuma celula */
	private int noKillTimeout;
	private int noKillCount = 0;
	
	public NK(Environment z, int x, int y) {
		super(z, x, y, new Pattern(Global.getInstance().getStringParameter(EnvParameters.NK_SELF_PATTERN)));
		state = NKStates.RANDOMWALK;
		initiateParameters();
	}
	
	private void initiateParameters(){
		this.killLimit = Global.getInstance().getIntegerParameter(EnvParameters.NK_KILL_LIMIT);
		this.lifetime = Global.getInstance().getIntegerParameter(EnvParameters.NK_LIFETIME);
		this.durationCK1 = Global.getInstance().getIntegerParameter(EnvParameters.NK_CK1_DURATION);
		this.noKillTimeout = Global.getInstance().getIntegerParameter(EnvParameters.NK_NOKILL_TIMEOUT);
	}

	/**
	 * Funcao executada a cada passo do agente
	 */
	@ScheduledMethod(start = 0, interval = 1)
	public void step(){
		
		if (lifetime <= 0 || killLimit <= 0 || (noKillCount >= noKillTimeout) ){
			this.state = NKStates.APOPTOSIS;
		} else {
			lifetime--;
			noKillCount++; //mais um tick sem matar ninguem (quando mata, o contador é zerado)
		}
		
		switch( state ){
		
			case RANDOMWALK: {
				randomWalk();
				if (getCitokineValue(CitokineNames.PK1) > 0){
					this.state = NKStates.SEEKING;
				}
			} break;
			
			case SEEKING: {
				
				if (countCK1 < durationCK1){
					releaseCitokine(CitokineNames.CK1);
					countCK1++;
				} else {
					this.state = NKStates.RANDOMWALK;
				}
				
				followCitokineByGradient(true, CitokineNames.PK1);
				
				//se o STRESS for maior que ANTI-INFLAMATORIO, mata a celula mesmo sem olhar o MHCI
				//no modelo atual, nao sao utilizadas substancias anti-inflamatorias
				//if (getCitokineValue(CitokineNames.PK1) > getCitokineValue(CitokineNames.CK2)){
				
				if (getCitokineValue(CitokineNames.PK1) > 0){
					for(PC pc: getEspecificNeighbors(PC.class)){
						if (pc.isStressed()){
							pc.apoptosis();
							killLimit--;

							//matar uma celula gera fatores que prolongam a vida da NK
							noKillCount = 0;
							countCK1 = 0;
						}
					}
				}
				
				
			} break;
			
			case APOPTOSIS: {
				this.zone.removeAgent(this);
			} break;
		}
	}
	
}
