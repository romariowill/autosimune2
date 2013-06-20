package autosimmune.agents.pathogens;

import java.util.ArrayList;

import repast.simphony.annotate.AgentAnnot;
import repast.simphony.engine.schedule.ScheduledMethod;
import autosimmune.agents.Antigen;
import autosimmune.agents.cells.Cell;
import autosimmune.agents.cells.Macrophage;
import autosimmune.defs.EnvParameters;
import autosimmune.defs.TCruziStates;
import autosimmune.defs.ZoneNames;
import autosimmune.env.Environment;
import autosimmune.env.Global;
import autosimmune.env.Tissue;
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
		
	/** referencia ao macrófago infectado */
	private Macrophage host = null;
	
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
		state = TCruziStates.CIRCULATING;
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
		
		//System.out.println("Num tcruzi - " + getTotalTcruzi());
		
		switch(state){

			case CIRCULATING: {
				if (this.host == null ){
					randomWalk();
					ArrayList<Macrophage> macrof = super.getEspecificNeighbors(Macrophage.class);
					for(Macrophage c: macrof){
						//o antigeno apresentado pelo MHC eh similar ao proprio antigeno da superficie da celula
						Pattern p = c.MHCI();
						if(Affinity.match(this.target, p)){
							if (this.infect(c)){
								state = TCruziStates.MULTIPLYING;
								break;
							}
						}
					}
				}else{
					state = TCruziStates.MULTIPLYING;
				}
			} break;
			
			case MULTIPLYING: {
				if(this.host != null){
					if(this.host.getTCruzis().size() >= Global.getInstance().getIntegerParameter(EnvParameters.TCRUZI_NUM_BREACH))
						state = TCruziStates.AWAITING_BREACH;
				}
			} break;
			
			case AWAITING_BREACH: {
				if(this.host == null)
					state = TCruziStates.CIRCULATING;
			} break;
		}
	}
	
	/** 
	 * Funcao chamada quando tenta infectar uma celula-alvo
	 * @param c Celula-alvo
	 */
	private boolean infect(Macrophage m) {
		if (m.infectedBy(this)){
			this.host = m;
			return true;
		}
		return false;
	}
	
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
	 * Funcao do TCruzi executada por uma celula hospedeira.
	 * É chamada pela propria celula hospedeira infectada.
	 * @param cell
	 */
	public void multiplica(Cell cell) {
		if (host != null && host == cell){
			int x = cell.getX();
			int y = cell.getY();
			this.moveTo(x, y);
			if (cell.getTCruzis().size() >= Global.getInstance().getIntegerParameter(EnvParameters.TCRUZI_NUM_BREACH)){
				cell.necrosis();
				numMult = 0;
			} else {
				TCruzi tcruzi = new TCruzi(this.zone, this.getX(), this.getY());
				zone.addAgent(tcruzi);
				numMult++;
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
	
	/**
	 * Funcao que retorna o numero de vezes que o tcruzi multiplicou
	 * @return int
	 */
	public int getNumMult(){
		return numMult;
	}
	
	public int getTotalTcruzi(){
		int x = ((Tissue) Environment.getEnvironment(ZoneNames.Tissue)).getObjects(TCruzi.class).size();
		return x;
	}
}
