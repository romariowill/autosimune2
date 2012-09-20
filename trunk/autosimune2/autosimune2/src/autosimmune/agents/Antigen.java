package autosimmune.agents;

import autosimmune.env.Environment;
import autosimmune.utils.Pattern;

/**
 * Class Antigen
 * @author root
 *
 */
public class Antigen extends Agent {

	private Pattern self;
	
	public Antigen(Environment zone, int x, int y, Pattern self){
		super(zone, x, y);
		this.self = self;
	}

	public Pattern getSelf(){
		return this.self;
	}
	
	@Override
	public void step() {	}
}
