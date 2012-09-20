package autosimmune.env;

import autosimmune.agents.cells.CTL;
import autosimmune.agents.cells.ThCell;
import autosimmune.defs.EnvParameters;
import autosimmune.defs.SELF;
import autosimmune.utils.Affinity;
import autosimmune.utils.Pattern;
import autosimmune.utils.PatternUtils;

/**
 * Ambiente Virtual. Não é representado graficamente na interface do usuario.
 * Sua finalidade eh dar suporte aos metodos de geracao de diversidade e tolerancia central
 * para células T
 *
 */
public class Timus {

	private Timus() {

	}

	/**
	 * Gera um padrão aleatório
	 * @param len Comprimento da "cadeia molecular"
	 * @return um Pattern contendo o padrão aleatório
	 */
	private static Pattern diversityGeneration(int len){
		return PatternUtils.getRandomPattern(len);
	}
	
	/**
	 * Realiza a seleção negativa em um padrão.
	 * Se o padrão for "auto-imune", então a função retorna TRUE,
	 * e o padrão deve ser descartado.
	 * @param p Padrão a ser testado
	 * @return TRUE se o padrão for auto-imune.
	 */
	private static boolean passNegativeSelection(Pattern p){
		for(SELF self: SELF.values()){
			if( Affinity.match(self.getPattern(), p)){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Simula a tolerancia central, gerando vários padrões aleatórios,
	 * e submetendo-os à seleção negativa. 
	 * Retorna o primeiro padrão a passar pela seleção negativa.
	 * @param len Comprimento do padrão
	 * @return Pattern
	 */
	private static Pattern centralTolerance(int len){
		Pattern p;
		//FIXME isso vai entrar em loop se todos os padrões possíveis forem parte do conjunto SELF
		//entrou em LOOP (1 hora para descobrir) quando coloquei um threshold muito alto, e aí passou
		//a não ter afinidade entre nenhum padrão
		do {
			p = diversityGeneration(len);
		} while(!passNegativeSelection(p));
		return p;
	}
	
	public static ThCell createThCell(Environment env, int x, int y){
		int patLen = Global.getInstance().getIntegerParameter(EnvParameters.RECEPTOR_PATTERN_LENGHT);
		
		Pattern p = centralTolerance(patLen);		
		return new ThCell(env, x, y, p);
	}
	
	public static CTL createCTLCell(Environment env, int x, int y){
		
		int patLen = Global.getInstance().getIntegerParameter(EnvParameters.RECEPTOR_PATTERN_LENGHT);
		
		//falha genetica
		//TODO parametrizar a opção de falha na tolerância central dos linfócitos
		//Pattern p = centralTolerance(patLen);
		Pattern p = diversityGeneration(patLen);
		return new CTL(env, x, y, p);
	}
}
