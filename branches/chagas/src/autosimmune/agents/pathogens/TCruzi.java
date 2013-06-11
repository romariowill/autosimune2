package autosimmune.agents.pathogens;

import java.util.ArrayList;

import repast.simphony.annotate.AgentAnnot;
import repast.simphony.engine.schedule.ScheduledMethod;
import autosimmune.agents.Antigen;
import autosimmune.agents.cells.Cell;
import autosimmune.agents.cells.PC;
import autosimmune.defs.EnvParameters;
import autosimmune.defs.MacrophageStates;
import autosimmune.defs.TCruziStates;
import autosimmune.env.Environment;
import autosimmune.env.Global;
import autosimmune.utils.Affinity;
import autosimmune.utils.Pattern;

/**
 * Classe que representa o T. Cruzi
 * @author WillianF
 *
 */
@AgentAnnot(displayName = "TCruzi")
public class TCruzi extends Antigen{

	/** peptideo algo do TCruzi */
	private Pattern target;
		
	/** referencia a celula infectada */
	private Cell host = null;
	
	/** numero de vezes que multiplicou */
	private int numMult = 0;
	
	/** Representação dos estados internos do parasita*/
	private TCruziStates state;
	
	/**
	 * Construtor do TCruzi
	 * @param z Zona
	 * @param x Coordenada X
	 * @param y Coordenada Y
	 * @param target Cadeia de Peptideos Alvo
	 * @param antigen Cadeia de Peptideos do proprio TCruzi
	 */
	public TCruzi(Environment z, int x, int y) {
		super(z, x, y, new Pattern(Global.getInstance().getStringParameter(EnvParameters.TCRUZI_SELF_PATTERN)));
		this.target = new Pattern(Global.getInstance().getStringParameter(EnvParameters.TCRUZI_TARGET_PATTERN));
	}
	
	/**
	 * Funcao que realiza, a cada tick, o comportamento do TCruzi.
	 * Seu comportamento é simples: procura em sua volta por celulas
	 * que possa infectar, ou seja, que tenham afinidade com sua membrana.
	 * Caso achem, infectam e se reproduzem. Senão, andam aleatoriamente.
	 */
	@ScheduledMethod(start = 0, interval = 1)
	public void step(){
		
		tick();
		
		//procurando uma celula
		if (this.host == null ){
			randomWalk(true);
			//ArrayList<Cell> cells = super.getEspecificNeighbors(Cell.class);
			//for(Cell c: cells){
			ArrayList<PC> cells = super.getEspecificNeighbors(PC.class);
			for(PC c: cells){
				//o antigeno apresentado pelo MHC eh similar ao proprio antigeno da superficie da celula
				Pattern p = c.MHCI();
				if(Affinity.match(this.target, p)){
					//if (this.infect(c)){
					//	return;
					//}
				}
			}
		}
	}
	
	/** 
	 * Funcao chamada quando tenta infectar uma celula-alvo
	 * @param c Celula-alvo
	 */
	/*private boolean infect(Cell c) {
		//if (c.infectedBy(this)){
		//	this.host = c;
		//	return true;
		//}
		return false;
	}*/
	
	/**
	 * Remove o TCruzi da celula. Funcao chamada
	 * quando a celula é destruida
	 * @param c
	 */
	public void removeHost(Cell c){
		if (this.host == c){
			this.host = null;
		}
	}

	/**
	 * Funcao do virus executada por uma celula hospedeira.
	 * É chamada pela propria celula hospedeira infectada.
	 * @param cell
	 */
	public void multiplica(Cell cell) {
		if (host != null && host == cell){
			numMult++;
			int x = cell.getX();
			int y = cell.getY();
			this.moveTo(x, y);
			int virulency = Global.getInstance().getIntegerParameter(EnvParameters.TCRUZI_VIRULENCY);
			int virusLatency = Global.getInstance().getIntegerParameter(EnvParameters.TCRUZI_LATENCY);
			if (numMult % virusLatency == 0) {
				if (numMult > virusLatency){
					cell.necrosis();
					this.host = null;
					numMult = 0;
				} else {
					for(int i = 0; i < virulency; i++){
						TCruzi tcruzi = new TCruzi(this.zone, this.getX(), this.getY());
						zone.addAgent(tcruzi);
					}
				}
			}
		}
	}
	
	/**
	 * Funcao chamada quando o TCruzi é neutralizado. Quando o Macrophage fagocita-o, por exemplo
	 * @return true se foi possivel neutraliza-lo (TCruzi intracelulares retornam false, por exemplo)
	 */
	public boolean neutralize(){
		return neutralize(false);
	}
	
	public boolean neutralize(boolean force){
		if(this.host == null){
			this.die();
			return true;
		} else if (force) {
			this.removeHost(this.host);
			this.host = null;
			this.die();
			return true;
		} else {
			return false;
		}
	}

}
