package autosimmune.env;

import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.RandomGridAdder;
import repast.simphony.space.grid.WrapAroundBorders;
import autosimmune.agents.Agent;
import autosimmune.agents.cells.BCell;
import autosimmune.agents.cells.CTL;
import autosimmune.agents.cells.ThCell;
import autosimmune.agents.cells.TReg;
import autosimmune.agents.portals.LymphnodePortal;
import autosimmune.defs.CitokineNames;
import autosimmune.defs.ZoneNames;
import autosimmune.defs.EnvParameters;
import autosimmune.defs.PortalDirections;
import autosimmune.utils.RandomUtils;

public class Lymphnode extends Environment {

	private int numCells;
	
	private int numThCells;
	
	private int numCTLCells;
	
	private int numBCells;
	
	private int numTRegCells;
	
	public Lymphnode() {
		super(ZoneNames.Lymphnode);
		
		//pega o tamanho deste espaco
		int w = global.getIntegerParameter(EnvParameters.LYMPHNODE_WIDTH);
		int h = global.getIntegerParameter(EnvParameters.LYMPHNODE_HEIGHT);
		
		//cria uma nova projecao, do tipo grid, com o tamanho especificado pelo paramentro
		space = GridFactoryFinder.createGridFactory(null)
		.createGrid(getEnvName()+"Grid", this, new GridBuilderParameters<Agent>(
				new WrapAroundBorders(), new RandomGridAdder<Agent>(), true, w, h));
		
		System.out.println("Criou Grid: " + getEnvName()+"Grid");
		
		setWidth(w);
		setHeight(h);
		
		// criando as citocinas que serão liberadas nesta zona
		createCitokineLayer(CitokineNames.NECROTIC);
		createCitokineLayer(CitokineNames.APOPTOSIS);
		createCitokineLayer(CitokineNames.MK1);
		createCitokineLayer(CitokineNames.CK1);

		//cria os portais de entrada na zona Lymphnode
		for(int i = 0; i < 20; i++){
			int x = RandomUtils.getRandomFromTo(0, this.getWidth());
			int y = RandomUtils.getRandomFromTo(0, this.getHeight());
			this.addPortal(new LymphnodePortal(this, PortalDirections.OUT, x, y));
		}
		
		//calcular o numero maximo de celulas que irao estar nesse ambiente
		numCells = (w * h)/4;
		
		//calcula a porcentagem de cada celula, de acordo com o numero maximo de celulas
		//TODO parametrizar porcentagens de ThCell, CTL e BCell
		//Checar porcentagens
		numThCells = (int) Math.round(numCells * Double.parseDouble(Global.getInstance().getStringParameter(EnvParameters.THCELL_PERCENTAGE))/100); //0.5
		numCTLCells = (int) Math.round(numCells * Double.parseDouble(Global.getInstance().getStringParameter(EnvParameters.CLTCELL_PERCENTAGE))/100); //0.2
	    numBCells = (int) Math.round(numCells * Double.parseDouble(Global.getInstance().getStringParameter(EnvParameters.BCELL_PERCENTAGE))/100); //0.25
	    numTRegCells  = (int) Math.round(numCells * Double.parseDouble(Global.getInstance().getStringParameter(EnvParameters.TREGCELL_PERCENTAGE))/100); //0.05

		//cria as celulas Th
		for(int i = 0; i < numThCells; i++){
			int x = RandomUtils.getRandomFromTo(0, w);
			int y = RandomUtils.getRandomFromTo(0, h);
			ThCell t = Timus.createThCell(this, x, y);
			addAgent(t);
		}
		
		//cria as celulas CTL
		for(int i = 0; i < numCTLCells; i++){
			int x = RandomUtils.getRandomFromTo(0, w);
			int y = RandomUtils.getRandomFromTo(0, h);
			CTL ctl = Timus.createCTLCell(this, x, y);
			addAgent(ctl);
		}

		//cria as celulas B
		for(int i = 0; i < numBCells; i++){
			int x = RandomUtils.getRandomFromTo(0, w);
			int y = RandomUtils.getRandomFromTo(0, h);
			BCell b = BoneMarrow.createBCell(this, x, y);
			addAgent(b);
		}
		
		//Cria celulas TReg
		for(int i = 0; i < numTRegCells; i++){
			int x = RandomUtils.getRandomFromTo(0, w);
			int y = RandomUtils.getRandomFromTo(0, h);
			TReg tr = Timus.createTRegCell(this, x, y);
			addAgent(tr);
		}
		
		
		System.out.println("Terminou de criar ambiente Lymphnode");
	}
}
