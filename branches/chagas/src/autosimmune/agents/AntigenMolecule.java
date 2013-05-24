package autosimmune.agents;

import autosimmune.env.Environment;
import autosimmune.utils.Pattern;

public class AntigenMolecule extends Agent {

	private Pattern antigen;
	
	public AntigenMolecule(Environment z, Pattern p, int x, int y) {
		super(z, x, y);
		this.antigen = p;
	}

	public String getSelfPattern(){
		return this.antigen.getEpitope();
	}
	
	@Override
	public void step() { }

}
