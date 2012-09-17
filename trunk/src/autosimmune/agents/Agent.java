package autosimmune.agents;

import java.util.ArrayList;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.util.ContextUtils;
import autosimmune.defs.ZoneNames;
import autosimmune.env.Environment;
import autosimmune.utils.RandomUtils;


/**
 * Classe que define as propriedades básicas de um agente
 * @author maverick
 *
 */
public abstract class Agent {

	/** Zone na qual o agente está */
	protected Environment zone;
	
	/** Coordenada X do agente */
	protected int x;
	
	/** Coordenada Y do agente */
	protected int y;
	
	/** Coordenada X anterior */
	protected int previous_x;
	
	/** Coordenada Y anterior */
	protected int previous_y;
	
	/** Número de ticks de idade do agente */
	protected int ticks;
	
	/**
	 * Construtor
	 * @param z Zone a qual o agente pertence
	 * @param x Coordenada X inicial do agente
	 * @param y Coordenada Y inicial do agente
	 */
	public Agent(Environment z, int x, int y){
		this.zone = z;
		this.x = x;
		this.y = y;
		ticks = 0;
	}
	
	/** 
	 * Incrementa em 1 o numero de ticks de idade do agente 
	 */
	protected void tick(){
		ticks++;
	}
	
	/**
	 * Método abstrato, deve ser implementado com as ações dos agentes
	 * e deve ser inseria a annotation para agendar o método como uma
	 * "task" no framework repast
	 * @see ScheduledMethod
	 */
	public abstract void step();
	
	/**
	 * Obtem o numero de ticks de idade do agente
	 * @return Numero de Ticks
	 */
	protected int getTicks(){
		return ticks;
	}
	
	/**
	 * Anda aleatoriamente para uma das celulas da vizinhanca de Moore
	 */
	public void randomWalk(){
		zone.walkTo(this, RandomUtils.getRandomFromTo(-1, 1), RandomUtils.getRandomFromTo(-1, 1));
	}
	
	/**
	 * Anda aleatoriamente, com a opcao de movimento fluido
	 * @param fluid
	 */
	public void randomWalk(boolean fluid){
		if (fluid){
			int randomX;
			int randomY;
			if (getX()-getPreviousX() > 0){
				randomX = RandomUtils.getRandomFromTo(0, 1);
			} else if (getX()-getPreviousX() < 0) {
				randomX = RandomUtils.getRandomFromTo(-1, 0);
			} else {
				randomX = RandomUtils.getRandomFromTo(-1, 1);
			}
			if (getY()-getPreviousY() > 0){
				randomY = RandomUtils.getRandomFromTo(0, 1);
			} else if (getY()-getPreviousY() < 0){
				randomY = RandomUtils.getRandomFromTo(-1, 0);
			} else {
				randomY = RandomUtils.getRandomFromTo(-1, 1);
			}
			zone.walkTo(this, randomX, randomY);
		} else {
			randomWalk();
		}
	}
	
	/**
	 * Pega a coordenada anterior X
	 * @return int
	 */
	protected int getPreviousX(){
		return previous_x;
	}
		
	/**
	 * Pega a coordenada anterior Y 
	 * @return int
	 */
	protected int getPreviousY(){
		return previous_y;
	}
	
	/**
	 * Obtem os vizinhos
	 * @return Uma lista de vizinhos (agents)
	 */
	public ArrayList<Agent> getNeighbors(){
		return zone.getNeighborsOf(this);
	}
	
	/**
	 * Obtem a lista de vizinhos de um tipo especifico, com raio 1
	 * @param type Tipo de vizinhos esperado
	 * @return Uma lista de agentes, do tipo T, definido em type
	 */
	public <T extends Agent> ArrayList<T> getEspecificNeighbors(Class<T> type){
		return zone.getEspecificNeighbors(this, 1, type);
	}
	
	/**
	 * Obtem a lista de vizinhos de um tipo T especifico, em um ponto especifico
	 * @param type
	 * @param x
	 * @param y
	 * @return
	 */
	@Deprecated
	public <T extends Agent> ArrayList<T> getEspecificAgentsAt(Class<T> type, int x, int y){
		return zone.getEspecificAgentsAt(type, x, y);
	}
	
	/**
	 * Obtem a lista de vizinhos de um tipo especifico, com raio R
	 * @param type Tipo de vizinhos esperado
	 * @param radius Raio 
	 * @return Uma lista de agentes, do tipo T, definido em type
	 */
	public <T extends Agent> ArrayList<T> getEspecificNeighbors(Class<T> type, int radius){
		return zone.getEspecificNeighbors(this, radius, type);
	}
	
	/**
	 * Obtem a coordenada X do agente
	 * @return int Coordenada X
	 */
	public int getX(){
		if (zone.getX(this) != -1)
			x = zone.getX(this);
		return x;
	}
	
	/**
	 * Obtem a coordenada Y do agente
	 * @return int Coordenada Y
	 */
	public int getY(){
		if (zone.getY(this) != -1)
			y = zone.getY(this);
		return y;
	}
	
	/**
	 * Retorna o nome da Zona a qual o agente pertence
	 * @return ZoneNames Nome da Zona
	 */
	public ZoneNames getZoneName(){
		return zone.getEnvName();
	}

	/**
	 * Atualiza a posicao atual do agente
	 * @param x Nova coordenada X
	 * @param y Nova coordenada Y
	 */
	public void moveTo(int x, int y){
		previous_x = x;
		previous_y = y;
		this.x = x;
		this.y = y;
		zone.moveTo(this, x, y);
	}
	
	/**
	 * Metodo "packge-private", nao possui "public" mesmo!!!
	 * @param zone
	 * @see AgentHelper#changeAgentZone(Agent, Environment)
	 */
	void setZone(Environment zone){
		this.zone = zone;
	}
	
	/**
	 * Mata um agente (retirando-o do sistema, da colonia)
	 */
	protected void die(){
		try{
			if (!ContextUtils.getContext(this).remove(this)){
				Logger.getLogger(this.getClass().getName()).log(Level.ERROR, "Erro ao remover agente");
			}
		} catch (Exception e){
			Logger.getLogger(this.getClass().getName()).log(Level.ERROR, "Erro ao remover agente", e);
		}
	}
}
