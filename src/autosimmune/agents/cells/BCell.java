package autosimmune.agents.cells;

import repast.simphony.engine.schedule.ScheduledMethod;
import autosimmune.agents.Antibody;
import autosimmune.agents.portals.Portal;
import autosimmune.agents.portals.TissuePortal;
import autosimmune.defs.BCellStates;
import autosimmune.defs.CitokineNames;
import autosimmune.defs.EnvParameters;
import autosimmune.defs.ZoneNames;
import autosimmune.env.Environment;
import autosimmune.env.Global;
import autosimmune.env.Tissue;
import autosimmune.utils.Affinity;
import autosimmune.utils.Pattern;
import autosimmune.utils.RandomUtils;

public class BCell extends APC implements Lymphocyte {

	private Pattern target;
	
	private BCellStates state;
	
	private int numABtoProduce;
	
	private int lifetime;
	
	private int germinalProbability;
	
	private float mk1MemoryThreshold;
	
	private float ck1MemoryThreshold;
	
	private boolean especific = false;
	
	private boolean calculedEspecific = false;
	
	public BCell(Environment z, int x, int y, Pattern target) {
		super(z, x, y, new Pattern(Global.getInstance().getStringParameter(EnvParameters.BCELL_SELF_PATTERN)));
		this.target = target;
		this.state = BCellStates.INACTIVE;
		initiateParameters();
	}
	
	private void initiateParameters(){
		this.numABtoProduce = Global.getInstance().getIntegerParameter(EnvParameters.BCELL_ANTIBODY_COUNT);
		this.lifetime = Global.getInstance().getIntegerParameter(EnvParameters.BCELL_LIFETIME);
		this.germinalProbability = Global.getInstance().getIntegerParameter(EnvParameters.BCELL_GERMINAL_PROBABILITY);
		this.mk1MemoryThreshold = Global.getInstance().getFloatParameter(EnvParameters.BCELL_MK1_MEMORY_THRESHOLD);
		this.ck1MemoryThreshold = Global.getInstance().getFloatParameter(EnvParameters.BCELL_CK1_MEMORY_THRESHOLD);
	}
	
	private BCell(Environment z, int x, int y, Pattern target, BCellStates initialState) {
		super(z, x, y, new Pattern(Global.getInstance().getStringParameter(EnvParameters.BCELL_SELF_PATTERN)));
		this.target = target;
		this.state = initialState;
		initiateParameters();
	}
	
	/*
	@Parameter(usageName="target", displayName="BCell Target Antigen: ")
	public String getTarget(){
		return this.target.getEpitope();
	}
	*/
	
	@ScheduledMethod(start = 0, interval = 1)
	public void step() {
		
		switch(state){
			
			case INACTIVE: {
				
				randomWalk();
				
				//contato com celula dendritica
				//TODO ela tambem pode fazer contato com um virus, ou com antigeno
				for(APC d : getEspecificNeighbors(APC.class)){
					Pattern antigen = d.MHCII();

					if (Affinity.match(target, antigen)){
						d.contact(true);
						this.state = BCellStates.WAITING;
						return;
					} else {
						d.contact(false);
					}
				}
				
			} break;
			
			case WAITING: {
				
				/* nao sei se isso seria o melhor jeito: entendam o problema: uma vez que a celula B
				 * tenha sido "acordada" por uma celula dendritica, ou por ter achado um virus ou antigeno
				 * ao seu redor, ela passa para este estado. Aqui, ela espera uma celula T helper ativada
				 * vir ativar a celula B tambem. Nao sei se este codigo deveria ficar aqui, ou na celula Th.
				 */
				
				followCitokineByGradient(true, CitokineNames.CK1);
				
				for(ThCell th : getEspecificNeighbors(ThCell.class)){
					if(th.isActive()){
						Pattern p = th.getTargetPattern();
						
						if (Affinity.match(target, p)){
							//pega o antigeno para apresenta-lo como APC
							this.antigen = p;
							
							if(RandomUtils.getTrueAtProbability(germinalProbability)){
								this.state = BCellStates.GERMINAL;
								this.zone.addAgent(new BCell(this.zone, getX(), getY()+1, this.target, this.state));
							} else {
								//TODO parametrizar proliferacao da celula B
								for(int i =0; i<5; i++){
									BCell b = new BCell(this.zone, getX(), getY()+1, this.target, BCellStates.MEMORY);
									//TODO passar por portal?
									Portal.transportToZone(b, ZoneNames.Tissue, 150, 150);
								}
							}
							return;
						}
					}
				}
				
			}break;
			
			case PLASMA_CELL: {
				
				followCitokineByGradient(true, CitokineNames.CK1);
				//envia anticorpos ao sitio de infeccao
				if(numABtoProduce > 0){
					Tissue tissue = (Tissue) Environment.getEnvironment(ZoneNames.Tissue);
					Antibody ab = new Antibody(tissue, getX(), getY(), this.target);
					tissue.addAgent(ab);
					numABtoProduce--;
				}
				if(getCitokineValue(CitokineNames.MK1) < mk1MemoryThreshold){
					this.state = BCellStates.MEMORY;
					return;
				}
				lifetime--;
				if(lifetime <= 0){
					this.apoptosis();
				}
				
			} break;
			
			case GERMINAL: {
				
				randomWalk();
				
				//envia anticorpos ao sitio de infeccao
				if(numABtoProduce > 0){
					Tissue tissue = (Tissue) Environment.getEnvironment(ZoneNames.Tissue);
					int numPortals = tissue.getPortalList().size();
					if (numPortals < 0){
						System.err.println("Nenhum portal encontrado no Tissue.");
						return;
					}
					TissuePortal tp = (TissuePortal) tissue.getPortalList().get(RandomUtils.getRandomFromTo(0, numPortals-1));
					if(tp.isFuncional() && numABtoProduce > 0){
						Antibody ab = new Antibody(tissue, tp.getX(), tp.getY(), this.target);
						tissue.addAgent(ab);
						numABtoProduce--;
					}
				}
				
				if(getCitokineValue(CitokineNames.MK1) < mk1MemoryThreshold && getCitokineValue(CitokineNames.CK1) < ck1MemoryThreshold){
					this.state = BCellStates.MEMORY;
					return;
				}
								
				lifetime--;
				if(lifetime <= 0){
					this.apoptosis();
				}
				
			} break;
			
			case MEMORY: {
				//FIXME o mecanismo de memoria da celula B esta apresentando BUGS
				/*
				followCitokineByGradient(true, CitokineNames.MK1);
				if(getCitokineValue(CitokineNames.MK1) > 20){
					for(ThCell th : getEspecificNeighbors(ThCell.class)){
						if(th.isActivated()){
							Pattern p = th.getTargetPattern();
							
							if (Affinity.match(target, p)){
								//TODO parametrizar a quantidade de cada uma
								//System.out.println("B memória ativado");
								if(RandomUtils.getTrueAtProbability(60)){
									this.state = BCellStates.GERMINAL;
									this.zone.addAgent(new BCell(this.zone, this.target, SELF.BCEL.getPattern(), getX(), getY()+1, this.state));
								} else {
									this.lifetime = 400;
									this.state = BCellStates.PLASMA_CELL;
									for(int i =0; i<5; i++){
										BCell b = new BCell(this.zone, this.target, SELF.BCEL.getPattern(), getX(), getY()+1, BCellStates.MEMORY);
										b.transportToZone(EnvNames.Tissue, 150, 150);
									}
								}
								return;
							}
						}
						
					}
					
				}
				*/
			} break;
		}
	}

	public boolean isEspecific(Pattern p){
		if(!calculedEspecific){
			if(Affinity.match(this.target, p)){
				calculedEspecific = true;
				especific = true;
				return true;
			} else {
				calculedEspecific = true;
				especific = false;
				return false;
			}
		} else {
			return especific;
		}
	}
	
	@Override
	public boolean isActive() {
		return (this.state.equals(BCellStates.PLASMA_CELL) || this.state.equals(BCellStates.GERMINAL));
	}

	@Override
	public void contact(boolean b) { }

}
