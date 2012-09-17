package autosimmune.agents.portals;

import java.util.HashMap;

import autosimmune.agents.Agent;
import autosimmune.agents.AgentHelper;
import autosimmune.defs.CitokineNames;
import autosimmune.defs.ZoneNames;
import autosimmune.defs.PortalDirections;
import autosimmune.env.Environment;
import autosimmune.env.Global;

public abstract class Portal extends Agent {

	protected Environment zone;
	
	protected Global env;
	
	protected PortalDirections direction;
	
	protected HashMap<CitokineNames, Integer> releasingCitokines;
	
	public Portal(Environment z, PortalDirections d, int x, int y){
		super(z, x, y);
		this.zone = z;
		this.env = Global.getInstance();
		this.direction = d;
		releasingCitokines = new HashMap<CitokineNames, Integer>();
	}

	public ZoneNames getZoneName(){
		return this.zone.getEnvName();
	}
	
	public void updatePortal(){
		for(CitokineNames n: releasingCitokines.keySet()){
			this.zone.releaseCitokine(n, this.getX(), this.getY());
		}
	}
	
	public void emitCitokine(CitokineNames citokine, int value) {
		if (value <= 0){
			releasingCitokines.remove(citokine);
		} else {
			releasingCitokines.put(citokine, value);
		}
	}
	
	protected HashMap<CitokineNames, Integer> getReleasingCitokines(){
		return releasingCitokines;
	}
	
	/**
	 * Transporta o agente de uma zona para outra
	 * @param zoneName Zone de destino
	 * @param y Coordenada X
	 * @param x Coordenada Y
	 */
	public static void transportToZone(Agent a, ZoneNames zoneName, int x, int y) {
		Environment from = Environment.getEnvironment(a.getZoneName());
		from.removeAgent(a);
		Environment to = Environment.getEnvironment(zoneName);
		AgentHelper.changeAgentZone(a, to);
		to.addAgent(a);
		a.moveTo(x, y);
	}
}
