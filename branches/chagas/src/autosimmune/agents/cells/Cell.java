package autosimmune.agents.cells;

import java.util.ArrayList;

import autosimmune.agents.Antigen;
import autosimmune.agents.pathogens.TCruzi;
import autosimmune.agents.pathogens.Virus;
import autosimmune.defs.CitokineNames;
import autosimmune.env.Environment;
import autosimmune.utils.Pattern;

/**
 * Classe que representa as Celulas (nucleadas) do Sistema
 * @author maverick
 *
 */
abstract public class Cell extends Antigen {

	/** Número de celulas */
	protected static Integer numCells = 0;
	
	/** String representando os peptídeos gerados pela célula */
	protected ArrayList<Pattern> patterns;
	
	/** Referencia ao virus que a infectou */
	protected Virus virus = null;

	/** Referencia aos Tcruzis que o estao infectando */
	protected ArrayList<TCruzi> tcruzis = null;
	
	/**
	 * Contrutor
	 * @param z Zone a qual a celula pertence
	 * @param x Coordenada X inicial
	 * @param y Coordenada Y inicial
	 */
	public Cell(Environment z, int x, int y, Pattern self) {
		super(z, x, y, self);
		numCells++;
	}
	
	/**
	 * Destrutor, decrementa numero de celulas
	 */
	/*
	protected void finalize() throws Throwable {
		numCells--;
	}
	*/
	
	/**
	 * Retorna o numero de instancias
	 */
	public static int getNumCells(){
		return numCells;
	}
	
	/**
	 * Retorna o valor da citocina na posicao atual do agente
	 * @param citokine
	 * @return double Valor da Citocina
	 */
//	@SuppressWarnings("deprecation")
	public double getCitokineValue(CitokineNames citokine){
		return zone.getCitokineValue(citokine, getX(), getY());
	}
	
	/**
	 * Libera uma citocina especifica, na posicial atual, e com valor default
	 * @param citokine
	 */
	protected void releaseCitokine(CitokineNames citokine){
		zone.releaseCitokine(citokine, getX(), getY());
	}
	
	//segue o gradiente de determinada citocina
//	@SuppressWarnings("deprecation")
	public void followCitokineByGradient(boolean randomWalk, CitokineNames ... citokine){
		
		//pega a posicao atual
		int x = getX();
		int y = getY();
		
		//a melhor posicao ate agora eh a posicao atual
		int bestX = x;
		int bestY = y;
		
		//pega a citocina mais forte na posicao atual
		int bestCitokineId = 0;
		double auxBestCitokineValue = 0;
		for(int ci = 0; ci<citokine.length; ci++){
			if (zone.getCitokineValue(citokine[ci], x, y) > auxBestCitokineValue){
				auxBestCitokineValue = zone.getCitokineValue(citokine[ci], x, y);
				bestCitokineId = ci;
			}
		}
		
		//acha o maior valor do gradiente na vizinhanca de Moore
		for(int i=-1; i<=1; i++){
			for(int j=-1; j<=1; j++){
				
				//pega a citocina mais forte desse vizinho
				double bestCitokineValue = 0;
				int citokineId = 0;
				for(int ci = 0; ci<citokine.length; ci++){
					if (zone.getCitokineValue(citokine[ci], x+i, y+j) > bestCitokineValue){
						bestCitokineValue = zone.getCitokineValue(citokine[ci], x+i, y+j);
						citokineId = ci;
					}
				}
				
				//se a citocina mais forte desse vizinho for maior que a citocina mais forte da posicao anterior
				if (zone.getCitokineValue(citokine[bestCitokineId], bestX, bestY) < bestCitokineValue)
				{
					bestX = x+i;
					bestY = y+j;
					bestCitokineId = citokineId;
				}
			}
		}
		
		//nao encontrou nada e randomWalk == true
		if (randomWalk && bestX == x && bestY == y){
			randomWalk();
		} else {		
			//movimenta na direcao de maior gradiente
			moveTo(bestX, bestY);
		}
	}
	
	/**
	 * Apresenta peptideos pelo "Major Histocompatibility Complex Class I
	 * @return String representando os peptideos produzidos pela celula
	 */
	public Pattern MHCI(){
		if(this.virus != null){
			return new Pattern(this.getSelf().getEpitope().concat(virus.getSelf().getEpitope()));
		} else {
			return this.getSelf();
		}
	}
	
	/**
	 * Mata a celula por apoptose
	 */
	public void apoptosis(){
		removeParasites();
		releaseCitokine(CitokineNames.APOPTOSIS);
		this.clean();
	}
	
	/**
	 * Infecta por virus
	 * @param virus
	 */
	public boolean infectedBy(Virus virus){
		if (this.virus == null){
			this.virus = virus;
			return true;
		}
		return false;
	}
	
	/**
	 * Mata a celula por necrose
	 */
	public void necrosis(){
		removeParasites();
		releaseCitokine(CitokineNames.NECROTIC);
		//die();
		//neste caso a celula nao executa o die, pois ainda precisa representar a presenca
		//de detritos no local da morte.
		//quando a funcao "clean" for chamada, é dado entao o "die"
	}
	
	/**
	 * Remove a celula da simulacao
	 */
	public void clean() {
		removeParasites();
		zone.removeAgent(this);
	}
	
	/**
	 * Infecta por TCruzi
	 * @param tcruzi
	 */
	public boolean infectedBy(TCruzi tcruzi){
		if (this.tcruzis != null){
			return this.tcruzis.add(tcruzi);
		}
		return false;
	}
	
	/**
	 * Libera os parasitas da célula
	 * @return boolean
	 */
	public void removeParasites(){
		if (this.tcruzis != null){
			for(TCruzi tc: this.tcruzis){
				tc.removeHost(this);
				this.tcruzis.remove(tc);
			}
		}
		if(this.virus != null){
			this.virus.removeHost(this);
			this.virus = null;
		}
	}
	
	public ArrayList<TCruzi> getTCruzis(){
		return tcruzis;
	}
}
