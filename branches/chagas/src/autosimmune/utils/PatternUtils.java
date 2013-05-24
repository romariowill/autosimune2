package autosimmune.utils;


/**
 * Contem funções úteis à geração de padrões
 *
 */
public class PatternUtils {

	private PatternUtils() {
		
	}
	
	public static Pattern getRandomPattern(int len){
		String pattern = "";
		for (int i = 0; i < len; i++){
			pattern += RandomUtils.getRandomFromTo(0, 1);
		}
		return new Pattern(pattern);
	}
}
