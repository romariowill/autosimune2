package autosimmune.env;

import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.WrapAroundBorders;
import autosimmune.agents.Agent;
import autosimmune.agents.cells.Dendritic;
import autosimmune.agents.cells.PC;
import autosimmune.agents.logger.TissueLogger;
import autosimmune.agents.portals.TissuePortal;
import autosimmune.defs.CitokineNames;
import autosimmune.defs.EnvParameters;
import autosimmune.defs.ZoneNames;
import autosimmune.utils.RandomUtils;

public class Tissue extends Environment {

	public Tissue() {
		super(ZoneNames.Tissue);
			
		//pega o tamanho deste espaco
		int w = global.getIntegerParameter(EnvParameters.TISSUE_WIDTH);
		int h = global.getIntegerParameter(EnvParameters.TISSUE_HEIGHT);
		
		//tamanho do quarteirao
		int quarterLen = global.getIntegerParameter(EnvParameters.TISSUE_QUARTER);
			
		//cria uma nova projecao, do tipo grid, com o tamanho especificado pelo paramentro
		space = GridFactoryFinder.createGridFactory(null)
		.createGrid(getEnvName()+"Grid", this, new GridBuilderParameters<Agent>(
				new WrapAroundBorders(), new TissueAdder<Agent>(), true, w, h));		
		
		setWidth(w);
		setHeight(h);
		
		// criando as citocinas que serão liberadas nesta zona
		createCitokineLayer(CitokineNames.NECROTIC);
		createCitokineLayer(CitokineNames.APOPTOSIS);
		createCitokineLayer(CitokineNames.PK1);
		createCitokineLayer(CitokineNames.MK1);
		createCitokineLayer(CitokineNames.CK1);
		
		//numero de celular dendriticas a serem adicionadas
		int numTotalDendriticsCells = global.getIntegerParameter(EnvParameters.DENDRITIC_CELLS_COUNT);

		//criando e adicionando celulas dendriticas ao modelo
		for(int i = 0; i < numTotalDendriticsCells; i++){
			int xi = RandomUtils.getRandomFromTo(0, this.getWidth()-1);
			int yi = RandomUtils.getRandomFromTo(0, this.getHeight()-1);
			
			Dendritic den = new Dendritic(this, xi, yi); 
			addAgent(den);
		}

		//cria as celulas parenquimais
		
		//calcula o tamanho dos "quarteroes"
		int quarter = quarterLen;
		
		//preenche verticalmente
		for(int j = 0; j < this.getHeight(); j+=quarter){
			for(int i = 0; i < this.getWidth(); i++){
				this.addAgent(new PC(this, i, j));
				this.addAgent(new PC(this, i, j+1));
			}
		}
		
		//preenche horizontalmente
		for(int i = 0; i < this.getWidth(); i+=quarter){
			for(int j = 0; j < this.getHeight(); j++){
				if ( ((j % quarter) != 0) && ((j % quarter) != 1) ){
					this.addAgent(new PC(this, i, j));
					this.addAgent(new PC(this, i+1, j));
				}
			}
		}		
		
		//cria os portais da zona Tissue
		//pQ = portalQuarter = quarteirao do portal
		int offset = (quarter/2)+1;
		for(int i = offset; i < this.getWidth()-offset; i+=quarter){
			for(int j = offset; j < this.getHeight()-offset; j+=quarter){
				this.addPortal(new TissuePortal(this, null, i, j));
			}
		}
	
		TissueLogger tl = new TissueLogger(this);
		this.add(tl);
		
		System.out.println("Criou subcontexto: " + getEnvName());
		
		//global.addSubContext(this);
	}
	
}
