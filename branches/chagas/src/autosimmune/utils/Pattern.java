package autosimmune.utils;

public class Pattern {
	
	private String epitope;
	
	public Pattern(String epitope){
		this.setEpitope(epitope);
	}
	
	public String getEpitope(){
		return epitope;
	}
	
	public void setEpitope(String epitope){
		this.epitope = epitope;
	}
}
