package autosimmune.agents;

import repast.simphony.engine.schedule.ScheduledMethod;
import autosimmune.agents.cells.Cell;
import autosimmune.agents.cells.PC;
import autosimmune.agents.pathogens.Virus;
import autosimmune.defs.EnvParameters;
import autosimmune.env.Environment;
import autosimmune.env.Global;
import autosimmune.utils.Affinity;
import autosimmune.utils.Pattern;

/**
 * Classe que representa uma molécula de anticorpo
 * @author root
 *
 */
public class Antibody extends Antigen {

	private Pattern target;
	
	private int duration;
	
	private boolean bind;
	
	private Cell bindcell = null;
	
	private boolean especific = false;
	
	private boolean calculedEspecific = false;
	
	public Antibody(Environment zone, int x, int y, Pattern target) {
		super(zone, x, y, new Pattern(Global.getInstance().getStringParameter(EnvParameters.ANTIBODY_SELF_PATTERN)));
		this.target = target;
		this.bind = false;
		this.duration = Global.getInstance().getIntegerParameter(EnvParameters.ANTIBODY_LIFETIME);
	}
	
	public boolean gotVirus(){
		return this.bind && bindcell == null;
	}

	public Pattern getTarget(){
		return this.target;
	}
	
	@ScheduledMethod(start = 0, interval = 1)
	public void step() {
		
		if(bind){
			
		} else {
			
			//TODO sera que o anticorpo não é atraído por citocinas?
			randomWalk();
			
			//pega os virus proximos e destroi eles. Ta, eu sei, anticorpo nao destroi o virus,
			//mas eh meu nivel de abstracao
			for(Virus v: getEspecificNeighbors(Virus.class, 3)){
				if(Affinity.match(this.target, v.getSelf())){
					if(v.neutralize()){
						this.bind = true;
						return;
					}
				}
			}
			
			//sera que podemos dizer que o antibody se liga na celula pelas
			//moleculas apresentadas pelo MHCI?
			//FIXME: Por que mesmo que o antibody se liga nas PCs?
			for(PC pc: getEspecificNeighbors(PC.class, 3)){
				if(Affinity.match(this.target, pc.MHCI())){
					this.bind = true;
					this.bindcell = pc;
					return;
				}
			}
			
			duration--;
			if(duration <= 0){
				this.die();
			}
		}
	}
	
	/**
	 * Função utilitária para gerar os gráficos
	 * @param p Padrão para o qual será testado se o anticorpo é específico
	 * @return true se o anticorpo é específico para o padrão passado por parâmetro
	 */
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
	
	public void clean(){
		if(this.bindcell != null){
			try{
				bindcell.apoptosis();
			} catch(Exception e){
				
			}
		}
		this.die();
	}

	
}
