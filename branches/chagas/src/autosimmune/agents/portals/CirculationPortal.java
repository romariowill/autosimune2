package autosimmune.agents.portals;

import java.util.ArrayList;
import repast.simphony.engine.schedule.ScheduledMethod;
import autosimmune.agents.Agent;
import autosimmune.agents.cells.BCell;
import autosimmune.agents.cells.CTL;
import autosimmune.agents.cells.ThCell;
import autosimmune.defs.CitokineNames;
import autosimmune.defs.ZoneNames;
import autosimmune.defs.PortalDirections;
import autosimmune.env.Environment;

public class CirculationPortal extends Portal {

	public CirculationPortal(Environment z, PortalDirections d, int x, int y) {
		super(z, d, x, y);
	}
	
	
	/** funcao chamada pelo framework a cada tick */
	@ScheduledMethod(start = 0, interval = 1)
	public void step(){
		
		//pega lista de agentes que passam perto do portal
		ArrayList<Agent> agents = this.getNeighbors();
		
		//para cada agente vizinho
		for(Agent a: agents){
			
			//rotina de transporte dos linfocitos T citoliticos
			if (a instanceof CTL){
				
				Portal target = null;
				ArrayList<Portal> tissuePortals = Environment.getEnvironment(ZoneNames.Tissue).getPortalList();
				
				for(int i = 0; i < tissuePortals.size(); i++){
					TissuePortal p = (TissuePortal) tissuePortals.get(i);
					if(p.isFuncional() && p.getReleasingCitokines().containsKey(CitokineNames.PK1)){
						target = p;
						break;
					}
				}
				
				if (target != null){
					Portal.transportToZone(a, ZoneNames.Tissue, target.getX(), target.getY());
				}
				
			}
			
			//rotine de transporte dos linfocitos T helper e B
			if (a instanceof ThCell || a instanceof BCell){
				
				Portal target = null;
				ArrayList<Portal> tissuePortals = Environment.getEnvironment(ZoneNames.Tissue).getPortalList();
				
				for(int i = 0; i < tissuePortals.size(); i++){
					TissuePortal p = (TissuePortal) tissuePortals.get(i);
					if(p.isFuncional() && ( 
							p.getReleasingCitokines().containsKey(CitokineNames.MK1) ||
							p.getReleasingCitokines().containsKey(CitokineNames.PK1) )){
						target = p;
						break;
					}
				}
				
				if (target != null){
					Portal.transportToZone(a, ZoneNames.Tissue, target.getX(), target.getY());
				}
			}
			
			//rotina de transporte dos granulocitos 
			/*
			if ( a instanceof Gran && this.getCitokineValue(CitokineNames.PK1) > 0 ){
				
				Portal target = null;
				ArrayList<Portal> tissuePortals = Environment.getEnvironment(ZoneNames.Tissue).getPortalList();
				
				for(int i = 0; i < tissuePortals.size(); i++){
					TissuePortal p = (TissuePortal) tissuePortals.get(i);
					if(p.isFuncional() && p.getReleasingCitokines().containsKey(CitokineNames.PK1) ){
						target = p;
						break;
					}
				}
				
				if (target != null){
					a.transportToZone(ZoneNames.Tissue, target.getX(), target.getY());
				}
			}
			*/
		}
	}
}
