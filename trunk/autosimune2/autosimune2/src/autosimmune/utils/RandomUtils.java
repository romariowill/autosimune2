package autosimmune.utils;

import repast.simphony.random.RandomHelper;

public class RandomUtils {

	public static int getRandomFromTo(int from, int to){
		if (from < to){
			return RandomHelper.nextIntFromTo(from, to);
		} else if (from > to){
			return RandomHelper.nextIntFromTo(to, from);
		} else {
			return from;
		}
	}
	
	/**
	 * Melhor função do sistema. Vou patentiar e vender pro google
	 * @param percentTrue
	 * @return
	 */
	public static boolean getTrueAtProbability(int percentTrue){
		if (percentTrue >= 100){
			return true;
		} else if (percentTrue <=0){
			return false;
		} else {
			return getRandomFromTo(0, 100) <= percentTrue;
		}
	}
}
