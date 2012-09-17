package autosimmune.env;

import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.RandomGridAdder;
import repast.simphony.space.grid.WrapAroundBorders;
import autosimmune.agents.Agent;
import autosimmune.agents.portals.CirculationPortal;
import autosimmune.defs.CitokineNames;
import autosimmune.defs.ZoneNames;
import autosimmune.defs.EnvParameters;
import autosimmune.defs.PortalDirections;
import autosimmune.utils.RandomUtils;

public class Circulation extends Environment {

	public Circulation() {
		
		super(   ZoneNames.circulation);

		//pega o tamanho deste espaco
		int w = global.getIntegerParameter(EnvParameters.CIRCULATION_WIDTH);
		int h = global.getIntegerParameter(EnvParameters.CIRCULATION_HEIGHT);
		
		//cria uma nova projecao, do tipo grid, com o tamanho especificado pelo paramentro
		space = GridFactoryFinder.createGridFactory(null)
		.createGrid(getEnvName()+"Grid", this, new GridBuilderParameters<Agent>(
				new WrapAroundBorders(), new RandomGridAdder<Agent>(), true, w, h));
		
		System.out.println("Criou Grid: " + getEnvName()+"Grid");
		
		setWidth(w);
		setHeight(h);
		
		createCitokineLayer(CitokineNames.APOPTOSIS);
		createCitokineLayer(CitokineNames.MK1);
		createCitokineLayer(CitokineNames.CK1);
		
		//cria os portais de entrada na zona Circulation
		for(int i = 0; i < 5; i++){
			int x = RandomUtils.getRandomFromTo(0, this.getWidth());
			int y = RandomUtils.getRandomFromTo(0, this.getHeight());
			this.addPortal(new CirculationPortal(this, PortalDirections.IN, x, y));
		}
		for(int i = 0; i < 50; i++){
			int x = RandomUtils.getRandomFromTo(0, this.getWidth());
			int y = RandomUtils.getRandomFromTo(0, this.getHeight());
			this.addPortal(new CirculationPortal(this, PortalDirections.OUT, x, y));
		}
	}
}
