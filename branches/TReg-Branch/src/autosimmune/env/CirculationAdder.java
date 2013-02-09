package autosimmune.env;

import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridAdder;
import autosimmune.agents.Agent;
import autosimmune.defs.ZoneNames;

//implementa um Adder, necessario para pegar os objetos no contexto e adiciona-los
//a sua respectiva zona
public class CirculationAdder<T extends Agent> implements GridAdder<T> {
	@Override
	public void add(Grid<T> space, T obj) {	
		if(obj.getZoneName().name().equals(ZoneNames.Circulation.name())){
			if(!space.moveTo(obj, obj.getX(), obj.getY())){
				System.err.println("Erro ao adicionar objeto a zona Circulacao.");
			}	
		} else {
			
		}
	}
}
