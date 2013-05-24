package autosimmune.utils;

import autosimmune.defs.EnvParameters;
import autosimmune.env.Global;

public class Affinity {

	private Affinity(){
		
	}
	
	public static double calc(Pattern a, Pattern b) {
		String peptideA = a.getEpitope();
		String peptideB = b.getEpitope();
		
		if (peptideA == null || peptideB == null || peptideA.length() == 0 || peptideB.length() == 0) {
	        return 0;
	    }
	 
	    int maxLen = 0;
	    int fl = peptideA.length();
	    int sl = peptideB.length();
	    int[][] table = new int[fl][sl];
	 
	    for (int i = 0; i < fl; i++) {
	        for (int j = 0; j < sl; j++) {
	            if (peptideA.charAt(i) == peptideB.charAt(j)) {
	                if (i == 0 || j == 0) {
	                    table[i][j] = 1;
	                }
	                else {
	                    table[i][j] = table[i - 1][j - 1] + 1;
	                }
	                if (table[i][j] > maxLen) {
	                    maxLen = table[i][j];
	                }
	            }
	        }
	    }
	    return maxLen;
	}

	public static boolean match(Pattern a, Pattern b) {
		if (a == null || b == null) return false;
		return calc(a, b) >= Global.getInstance().getIntegerParameter(EnvParameters.AFFINITY_THRESHOLD);
	} 
}
