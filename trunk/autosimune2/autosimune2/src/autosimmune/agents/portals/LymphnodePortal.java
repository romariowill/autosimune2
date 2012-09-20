package autosimmune.agents.portals;

import repast.simphony.engine.schedule.ScheduledMethod;
import autosimmune.agents.cells.BCell;
import autosimmune.agents.cells.CTL;
import autosimmune.agents.cells.Cell;
import autosimmune.agents.cells.ThCell;
import autosimmune.defs.ZoneNames;
import autosimmune.defs.PortalDirections;
import autosimmune.env.Circulation;
import autosimmune.env.Environment;

public class LymphnodePortal extends Portal {

	private PortalDirections direction;
	
	public LymphnodePortal(Environment z, PortalDirections d, int x, int y) {
		super(z, d, x, y);
		this.direction = d;
	}
	
	/** funcao chamada pelo framework a cada tick */
	@ScheduledMethod(start = 0, interval = 1)
	public void step(){
		
		if (this.direction.equals(PortalDirections.OUT)){
			Circulation circulation = (Circulation) Environment.getEnvironment(ZoneNames.circulation);
			//TODO verificar se o portal é do tipo IN
			CirculationPortal cp = (CirculationPortal) circulation.getPortalList().get(0);
			for(Cell c : getEspecificNeighbors(Cell.class)){
				if(c instanceof ThCell){
					ThCell cell = (ThCell) c;
					if(cell.isActive()){
						Portal.transportToZone(c, ZoneNames.circulation, cp.getX(), cp.getY());
						break;
					}
				} else if (c instanceof BCell){
					BCell cell = (BCell) c;
					if(cell.isActive()){
						Portal.transportToZone(c, ZoneNames.circulation, cp.getX(), cp.getY());
						break;
					}
				} else if (c instanceof CTL){
					CTL cell = (CTL) c;
					if(cell.isActive()){
						Portal.transportToZone(c, ZoneNames.circulation, cp.getX(), cp.getY());
						break;
					}
				}
				
			}
		}
	}

}
