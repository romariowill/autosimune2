package autosimmune.agents.cells;

import autosimmune.env.Environment;
import autosimmune.utils.Pattern;

public abstract class APC extends Cell {

	protected Pattern antigen;
	
	public APC(Environment z, int x, int y, Pattern self) {
		super(z, x, y, self);
	}

	/**
	 * Simula o complexo de histocompatibilidade humana classe 2, MHC II
	 * @return
	 */
	public Pattern MHCII(){
		return this.antigen;
	}
	
	/**
	 * Informa se a celula APC fez um contato com uma celula
	 * que reconheceu o antigeno apresentado pelo MHC II
	 * @param b TRUE se reconheceu
	 */
	public abstract void contact(boolean b);
}
