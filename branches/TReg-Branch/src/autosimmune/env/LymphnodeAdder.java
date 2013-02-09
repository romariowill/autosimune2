package autosimmune.env;

import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridAdder;
import autosimmune.agents.Agent;
import autosimmune.defs.ZoneNames;


public class LymphnodeAdder<T extends Agent> implements GridAdder<T> {
	@Override
	public void add(Grid<T> space, T obj) {	
		if(obj.getZoneName().name().equals(ZoneNames.Lymphnode.name())){
			if(!space.moveTo(obj, obj.getX(), obj.getY())){
				System.err.println("Erro ao adicionar objeto a zona Linfonodo.");
			}	
		} else {
			
		}
	}
}
