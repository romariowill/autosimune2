package autosimmune.agents;

import autosimmune.env.Environment;

/**
 * Classe Wrapper para permitir que o Portal tenha acesso ao método "package-private"
 * da classe Agent
 * @see Agent#setZone(Environment)
 * @author root
 *
 */
public class AgentHelper {
	
	/**
	 * Modifica a Zone à qual o agente pertente
	 * @param a Agente
	 * @param zone Nova Zona
	 */
	public static void changeAgentZone(Agent a, Environment zone){
		a.setZone(zone);
	}
	
	private AgentHelper(){
		
	}
}
