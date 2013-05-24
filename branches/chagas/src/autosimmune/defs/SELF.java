package autosimmune.defs;

import autosimmune.env.Global;
import autosimmune.utils.Pattern;

public enum SELF {
	MACROPHAGE (Global.getInstance().getStringParameter(EnvParameters.MACROPHAGE_SELF_PATTERN)),
	NK (Global.getInstance().getStringParameter(EnvParameters.NK_SELF_PATTERN)),
	PC (Global.getInstance().getStringParameter(EnvParameters.PC_SELF_PATTERN)),
	DENDRITIC (Global.getInstance().getStringParameter(EnvParameters.DENDRITIC_SELF_PATTERN)),
	BCEL (Global.getInstance().getStringParameter(EnvParameters.BCELL_SELF_PATTERN)),
	CTL (Global.getInstance().getStringParameter(EnvParameters.CTL_SELF_PATTERN)),
	ThCELL (Global.getInstance().getStringParameter(EnvParameters.TH_SELF_PATTERN));
	
	private String n;
	
	SELF(String n){
		this.n = n;
	}
	
	public Pattern getPattern(){
		return new Pattern(this.n);
	}
	@Override
	public String toString(){
		return this.n;
	}
}
