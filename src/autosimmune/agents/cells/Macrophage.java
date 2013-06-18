package autosimmune.agents.cells;

import java.util.ArrayList;

import repast.simphony.annotate.AgentAnnot;
import repast.simphony.engine.schedule.ScheduledMethod;
import autosimmune.agents.Antibody;
import autosimmune.agents.pathogens.TCruzi;
import autosimmune.agents.pathogens.Virus;
import autosimmune.defs.CitokineNames;
import autosimmune.defs.EnvParameters;
import autosimmune.defs.MacrophageStates;
import autosimmune.defs.PAMPS;
import autosimmune.env.Environment;
import autosimmune.env.Global;
import autosimmune.utils.Affinity;
import autosimmune.utils.Pattern;

/**
 * Classe representando a Célula Macrófago
 * @author maverick
 *
 */
@AgentAnnot(displayName = "Macrophage Cell")
public class Macrophage extends APC {

	/** Representação dos estados internos da célula */
	private MacrophageStates state;
	
	private int lifetime;

	private int mk1duration;
	
	/**
	 * Construtor da Classe
	 * @param z Zona a qual o macrófago pertence inicialmente
	 * @param self Peptídeo self que identifica o macrófago
	 * @param x Coordenada X inicial
	 * @param y Coordenada Y inicial
	 */
	public Macrophage(Environment z, int x, int y) {
		super(z, x, y, new Pattern(Global.getInstance().getStringParameter(EnvParameters.MACROPHAGE_SELF_PATTERN)));
		state = MacrophageStates.NORMAL;
		initiateParameters();
	}
	
	private void initiateParameters(){
		this.lifetime = Global.getInstance().getIntegerParameter(EnvParameters.MACROPHAGE_LIFETIME);
		this.mk1duration = Global.getInstance().getIntegerParameter(EnvParameters.MACROPHAGE_MK1_DURATION);
		this.tcruzis = new ArrayList<TCruzi>();
	}
	
	/**
	 * Método que executa o passo do macrofago a cada tick
	 */
	@ScheduledMethod(start = 0, interval = 1)
	public void step(){
		
		if (getTicks() > lifetime){
			this.state = MacrophageStates.APOPTOSIS;
		}
		
		switch(state){

			case NORMAL: {
				followCitokineByGradient(true, CitokineNames.PK1, CitokineNames.APOPTOSIS, CitokineNames.NECROTIC);
				
				double necrotic = getCitokineValue(CitokineNames.NECROTIC);
				double pk = getCitokineValue(CitokineNames.PK1);
				double apop = getCitokineValue(CitokineNames.APOPTOSIS);

				if (necrotic > 0 || pk > apop ){
					this.state = MacrophageStates.INFLAMATORY;
				}
				
			} break;
			
			case INFLAMATORY: {
				
				if(getCitokineValue(CitokineNames.NECROTIC) > 0){
					followCitokineByGradient(true, CitokineNames.NECROTIC);
				} else if (getCitokineValue(CitokineNames.PK1) > 10){
					followCitokineByGradient(true, CitokineNames.PK1);
				} else {
					randomWalk();
				}
				
				for(PC pc: getEspecificNeighbors(PC.class)){
					if (pc.isDead()){
						//os antigenos capturados na fagocitose de uma celula infectada
						//sao semelhantes aos apresentados pelo MHCI
						this.antigen = pc.MHCI();
						pc.clean();
						this.state = MacrophageStates.ACTIVE;
					}
				}
				
				for(Antibody ab: getEspecificNeighbors(Antibody.class)){
					ab.clean();
				}
				
				PAMPS pamps = PAMPS.getInstance();
				for(Virus v: getEspecificNeighbors(Virus.class)){
					for(Pattern p: pamps.getPamps()){
						if (Affinity.match(p, v.getSelf())){
							if (v != null) {
								if(v.neutralize()){
									this.state = MacrophageStates.ACTIVE;
								}
							}
						}
					}
				}
				
			} break;
			
			case ACTIVE: {
				
				if(getCitokineValue(CitokineNames.NECROTIC) > 0){
					followCitokineByGradient(true, CitokineNames.NECROTIC);
				} else if (getCitokineValue(CitokineNames.PK1) > 10){
					followCitokineByGradient(true, CitokineNames.PK1);
				} else {
					randomWalk();
				}
				
				if(mk1duration > 0){
					releaseCitokine(CitokineNames.MK1);
					mk1duration--;
				}
				/*remove Células PC mortas*/
				for(PC pc: getEspecificNeighbors(PC.class)){
					if (pc.isDead()){
						pc.clean();
					}
				}
				/*remove Células Macrophage mortas*/
				for(Macrophage ma: getEspecificNeighbors(Macrophage.class)){
					if (ma.isDead()){
						ma.clean();
					}
				}
				
				for(Antibody ab: getEspecificNeighbors(Antibody.class)){
					ab.clean();
				}
				
				PAMPS pamps = PAMPS.getInstance();
				for(Virus v: getEspecificNeighbors(Virus.class)){
					for(Pattern p: pamps.getPamps()){
						if (Affinity.match(p, v.getSelf())){
							if (v != null) {
								v.neutralize();
							}
						}
					}
				}
				
			} break;

			case APOPTOSIS: {
				super.apoptosis();
				this.die();
			}break;
		
			case NECROSIS: {
				//TODO parametrizar taxa de liberacao da citocina NECROTIC por celula morta
				if(getTicks() % 20 == 0){
					releaseCitokine(CitokineNames.NECROTIC);
				}
				if(this.tcruzis.size() >= Global.getInstance().getIntegerParameter(EnvParameters.TCRUZI_NUM_BREACH))
					removeParasites();
			}break;
		}
	}

	@Override
	public boolean infectedBy(Virus virus) {
		return false;
	}

	@Override
	public void contact(boolean b) {
		//TODO parametrizar fatores de prolongamento de vida
		mk1duration+=10;
		lifetime+=10;
	}
	
	/**
	 * Informa se a célula está morta
	 * @return
	 */
	public boolean isDead() {
		return (this.state == MacrophageStates.APOPTOSIS || this.state == MacrophageStates.NECROSIS);
	}

	/**
	 * Remove uma celula morta do tecido.
	 * Funcao chamada quando um macrofago remove a celula morta
	 */
	@Override
	public void clean() {
		removeParasites();
		zone.removeAgent(this);
	}

}
