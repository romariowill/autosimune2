package autosimmune.defs;

import java.util.ArrayList;

import autosimmune.utils.Pattern;

public class PAMPS {
	
	private static final PAMPS instance = new PAMPS();
	
	private ArrayList<Pattern> pamps;
	
	private PAMPS(){
		pamps = new ArrayList<Pattern>();
	}
	
	public static PAMPS getInstance(){
		return instance;
	}
	
	public void addPamp(Pattern pamp){
		pamps.add(pamp);
	}
	
	public ArrayList<Pattern> getPamps(){
		return pamps;
	}
}