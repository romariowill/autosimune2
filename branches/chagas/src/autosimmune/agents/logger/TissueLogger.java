package autosimmune.agents.logger;

import org.apache.velocity.runtime.directive.Macro;

import autosimmune.agents.Agent;
import autosimmune.agents.Antibody;
import autosimmune.agents.cells.BCell;
import autosimmune.agents.cells.CTL;
import autosimmune.agents.cells.Macrophage;
import autosimmune.agents.cells.NK;
import autosimmune.agents.cells.PC;
import autosimmune.agents.cells.ThCell;
import autosimmune.agents.pathogens.TCruzi;
import autosimmune.agents.pathogens.Virus;
import autosimmune.defs.EnvParameters;
import autosimmune.defs.MacrophageStates;
import autosimmune.defs.PCStates;
import autosimmune.defs.ZoneNames;
import autosimmune.env.Environment;
import autosimmune.env.Global;
import autosimmune.env.Tissue;
import autosimmune.utils.Pattern;



public class TissueLogger extends Agent {

	private Pattern virusPattern;
	
	private Tissue zone;

	private Global global;
	
	public TissueLogger(Tissue z) {
		super(z, 0, 0);
		this.zone = z;
		global = Global.getInstance();
		virusPattern = new Pattern(global.getStringParameter(EnvParameters.VIRUS_SELF_PATTERN));
	}
	
	public int getVirusCount(){
		return this.zone.getObjects(Virus.class).size();
	}
	
	public int getTCruziCount(){
		int x = this.zone.getObjects(TCruzi.class).size();
		//System.out.println(x);
		return x;
	}
	
	public int getMacrophageCount(){
		return this.zone.getObjects(Macrophage.class).size();
	}
	
	public int getTotalMacrophageNoDead(){
		int cont = 0;
		for(Agent mc: this.zone.getObjects(Macrophage.class)){
			if(!((Macrophage)mc).isDead())
				cont++;
		}
		return cont;
	}
	
	public int getTotalMacrophageActive(){
		int cont = 0;
		for(Agent mc: this.zone.getObjects(Macrophage.class)){
			if(((Macrophage)mc).getState()==MacrophageStates.ACTIVE)
				cont++;
		}
		return cont;
	}
	
	public int getTotalMacrophageInflamatory(){
		int cont = 0;
		for(Agent mc: this.zone.getObjects(Macrophage.class)){
			if(((Macrophage)mc).getState()==MacrophageStates.INFLAMATORY)
				cont++;
		}
		return cont;
	}
	
	public int getNKCount(){
		return this.zone.getObjects(NK.class).size();
	}
	
	public int getAntibodyCount(){
		return this.global.getObjects(Antibody.class).size();
	}
	
	public int getCTLCount(){
		return this.global.getObjects(CTL.class).size();
	}
	
	public int getBCellCount(){
		return this.global.getObjects(BCell.class).size();
	}
	
	public int getThCellCount(){
		return this.global.getObjects(ThCell.class).size();
	}
	
	public int getEspecificThCellCount(){
		int count = 0;
		for(Agent th : this.global.getObjects(ThCell.class)){
			if(((ThCell) th).isEspecific(virusPattern)){
				count++;
			}
		}
		return count;
	}
	
	public int getEspecificCTLCount(){
		int count = 0;
		for(Agent ctl : this.global.getObjects(CTL.class)){
			if(((CTL) ctl).isEspecific(virusPattern)){
				count++;
			}
		}
		return count;
	}
	
	public int getEspecificBCellCount(){
		int count = 0;
		for(Agent b : this.global.getObjects(BCell.class)){
			if(((BCell) b).isEspecific(virusPattern)){
				count++;
			}
		}
		return count;
	}
	
	public int getEspecificAgCount(){
		int count = 0;
		for(Agent ag : this.global.getObjects(Antibody.class)){
			if(((Antibody) ag).isEspecific(virusPattern)){
				count++;
			}
		}
		return count;
	}
	
	public int getNonEspecificThCellCount(){
		int count = 0;
		for(Agent th : this.global.getObjects(ThCell.class)){
			if(!((ThCell) th).isEspecific(virusPattern)){
				count++;
			}
		}
		return count;
	}
	
	public int getNonEspecificCTLCount(){
		int count = 0;
		for(Agent ctl : this.global.getObjects(CTL.class)){
			if(!((CTL) ctl).isEspecific(virusPattern)){
				count++;
			}
		}
		return count;
	}
	
	public int getNonEspecificBCellCount(){
		int count = 0;
		for(Agent b : this.global.getObjects(BCell.class)){
			if(!((BCell) b).isEspecific(virusPattern)){
				count++;
			}
		}
		return count;
	}
	
	public int getNonEspecificAgCount(){
		int count = 0;
		for(Agent ag : this.global.getObjects(Antibody.class)){
			if(!((Antibody) ag).isEspecific(virusPattern)){
				count++;
			}
		}
		return count;
	}
	
	public int getAutoimmuneCTLCount(){
		int count = 0;
		for(Agent ctl : this.global.getObjects(CTL.class)){
			if(((CTL) ctl).isAutoimunne()){
				count++;
			}
		}
		return count;
	}
	
	public int getTissueAutoimmuneCTLCount(){
		int count = 0;
		for(Agent ctl : this.zone.getObjects(CTL.class)){
			if(((CTL) ctl).isAutoimunne()){
				count++;
			}
		}
		return count;
	}
	
	public int getAutoimmuneCTLCellKillCount(){
		int count = 0;
		for(Agent actl : this.global.getObjects(CTL.class)){
			CTL ctl = (CTL) actl;
			count += ctl.getAutoimmuneCellKill();
		}
		return count;
	}
	
	public int getHealthPC(){
		int hpc = 0;
		for(Agent apc : this.zone.getObjects(PC.class)){
			PC pc = (PC) apc;
			if(pc.getState() == PCStates.NORMAL){
				hpc++;
			}
		}
		return hpc;
		
	}

	@Override
	public void step() { }
	
}
