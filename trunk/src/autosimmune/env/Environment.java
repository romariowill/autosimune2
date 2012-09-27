package autosimmune.env;

import java.util.ArrayList;
import java.util.HashMap;

import repast.simphony.context.DefaultContext;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.WrapAroundBorders;
import repast.simphony.valueLayer.GridValueLayer;
import repast.simphony.valueLayer.ValueLayerDiffuser;
import autosimmune.agents.Agent;
import autosimmune.agents.portals.Portal;
import autosimmune.defs.CitokineNames;
import autosimmune.defs.EnvParameters;
import autosimmune.defs.ZoneNames;

/**
 * Classe generica que representa um ambiente generico
 * 
 * @author maverick
 * 
 */
public abstract class Environment extends DefaultContext<Agent> {

	/** Lista de ambientes */
	protected static HashMap<ZoneNames, Environment> environments;

	/** Referencia o Contexto global */
	protected static Global global;

	/** Nome desse ambiente */
	protected ZoneNames name;

	/** Projecao desse ambiente */
	protected Grid<Agent> space;

	/** Largura da projecao do ambiente */
	protected Integer width;

	/** Altura da projecao do ambiente */
	protected Integer height;

	/** Citocinas presentes no ambiente */
	protected HashMap<CitokineNames, ValueLayerDiffuser> citokines;

	/** Lista de portais de cada ambiente */
	protected ArrayList<Portal> portals;

	/**
	 * Construtor do ambiente
	 * 
	 * @param name
	 *            Nome do ambiente
	 */
	public Environment(ZoneNames name) {
		super(name.name());
		this.name = name;
		Environment.global = Global.getInstance();

		citokines = new HashMap<CitokineNames, ValueLayerDiffuser>();

		if (environments == null) {
			environments = new HashMap<ZoneNames, Environment>();
		}

		if (portals == null) {
			portals = new ArrayList<Portal>();
		}

		environments.put(name, this);
	}

	/**
	 * Obtem o valor de uma determinada citocina, em uma determinada posicao
	 * 
	 * @param citokine
	 *            Nome da Citocina
	 * @param x
	 *            Coordenada X
	 * @param y
	 *            Coordenada Y
	 * @return double Valor da Citocina
	 */
	@Deprecated
	public double getCitokineValue(CitokineNames citokine, int x, int y) {
		if (citokines.containsKey(citokine)) {
			double valor = citokines.get(citokine).getValueLayer().get(x, y);
			/*
			 * if (valor > 1){ return valor; } else { return 0; }
			 */
			return valor;
		} else {
			return 0;
		}
	}

	/**
	 * Retorna a instancia de um ambiente
	 * 
	 * @param Nome
	 *            do Ambiente
	 * @return Environment
	 * @see Environment
	 */
	public static Environment getEnvironment(ZoneNames name) {
		return Environment.environments.get(name);
	}

	/**
	 * Adiciona um portal ao ambiente
	 * 
	 * @param x
	 * @param y
	 * @param d
	 */
	protected void addPortal(Portal p) {
		add(p);
		portals.add(p);
	}

	/**
	 * Adiciona um agente ao contexto. O contexto, atraves das classes herdadas
	 * de GridAdder presentes em cada Zona, definirá para qual zona o agente
	 * deverá ir, através do atributo "Zone" de cada agente.
	 * 
	 * @param a
	 *            Agente a ser adicionado ao contexto
	 */
	public void addAgent(Agent a) {
		if (!add(a)) {
			System.err.println("Erro ao adicionar agente ao ambiente.");
			return;
		}
	}

	/**
	 * Obtem a lista de vizinhos de tipos especificos de um determinado agente
	 * 
	 * @param a
	 *            O agente central
	 * @param r
	 *            Raio
	 * @param type
	 *            O tipo de agente procurado
	 * @return Um vetor do tipo "type", contendo os objetos encontrados
	 */
	@SuppressWarnings("unchecked")
	public <T extends Agent> ArrayList<T> getEspecificNeighbors(Agent a, int r,
			Class<T> type) {

		// array que contera o resultado
		ArrayList<T> agents = new ArrayList<T>();

		// percorre todos os vizinhos e pega os do tipo especificado
		for (Agent agent : this.getNeighborsOf(a, r)) {
			if (type.isInstance(agent)) {
				T tipo = (T) agent;
				agents.add(tipo);
			}
		}

		// retorna o resultado
		return agents;
	}

	/**
	 * Obtem a lista de vizinhos, de um tipo especifico, em um ponto especifico
	 * 
	 * @param type
	 * @param x
	 * @param y
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends Agent> ArrayList<T> getEspecificAgentsAt(Class<T> type,
			int x, int y) {

		// array que contera o resultado
		ArrayList<T> agents = new ArrayList<T>();

		// percorre todos os vizinhos e pega os do tipo especificado
		for (Agent agent : space.getObjectsAt(x, y)) {
			if (type.isInstance(agent)) {
				T tipo = (T) agent;
				agents.add(tipo);
			}
		}

		// retorna o resultado
		return agents;
	}

	/**
	 * Obtem a lista de agentes em uma determinada coordenada (X, Y) na grid
	 * 
	 * @param x
	 *            Coordenada X
	 * @param y
	 *            Coordenada Y
	 * @return Uma lista de agentes
	 */
	public ArrayList<Agent> getAgentsAt(int x, int y) {
		ArrayList<Agent> agents = new ArrayList<Agent>();
		for (Object o : space.getObjectsAt(x, y)) {
			agents.add((Agent) o);
		}
		return agents;
	}

	/**
	 * Obtem a lista de vizinhos na Vizinhanca de Moore, com raio = 1
	 * 
	 * @param c
	 *            Agente central, para o qual serao listados os vizinhos
	 * @return Uma lista de agentes
	 */
	public ArrayList<Agent> getNeighborsOf(Agent c) {
		return getNeighborsOf(c, 1);
	}

	/**
	 * Obtem a lista de vizinhos na Vizinhanca de Moore, com raio r
	 * 
	 * @param c
	 *            Agente central, para o qual serao listados os vizinhos
	 * @param r
	 *            Raio: numero de camadas de celulas da grid que serao
	 *            verificadas
	 * @return Uma lista de agentes
	 */
	public ArrayList<Agent> getNeighborsOf(Agent c, int r) {

		// array de vizinhos
		ArrayList<Agent> agents = new ArrayList<Agent>();

		// pega a posicao atual
		int x = getX(c);
		int y = getY(c);

		// recupera todos os vizinhos
		for (int i = -r; i <= r; i++) {
			for (int j = -r; j <= r; j++) {
				// correcao de bug: o proprio agente nao eh vizinho de si
				if (i == 0 && j == 0)
					continue;
				for (Object ce : space.getObjectsAt(x + i, y + j)) {
					agents.add((Agent) ce);
				}
			}
		}

		// retorna lista de vizinhos
		return agents;
	}

	/**
	 * Cria uma camada que representa a difusao de uma citocina. Deve ser
	 * chamado no construtor de cada zona
	 * 
	 * @param citokine
	 *            Nome da Citocina
	 */
	public void createCitokineLayer(CitokineNames citokine) {
		if (!citokines.containsKey(citokine)) {
			// cria o grid de valores
			GridValueLayer gvl = new GridValueLayer(this.name.toString()
					+ citokine.toString(), true, new WrapAroundBorders(),
					getWidth(), getHeight());

			// cria o layer que utiliza o grid de valores
			ValueLayerDiffuser vld = new ValueLayerDiffuser(gvl,
					global.getFloatParameter(EnvParameters.EVAPORATION_RATE),
					global.getFloatParameter(EnvParameters.DIFFUSION_CONSTANT),
					true);

			addValueLayer(gvl);
			// env.addValueLayer(gvl);

			// adiciona o novo layer a nossa estrutura
			citokines.put(citokine, vld);

		} else {
			System.err.println("Tentou criar citocina que ja existe: "
					+ citokine.name());
		}
	}

	/**
	 * Libera uma citocina especifica, na posicao (X,Y) com valor default
	 * 
	 * @param citokine
	 *            Tipo de citocina
	 * @param x
	 *            Coordenada X
	 * @param y
	 *            Coordenada Y
	 * @see EnvParameters
	 */
	public void releaseCitokine(CitokineNames citokine, int x, int y) {
		releaseCitokine(citokine,
				global.getFloatParameter(EnvParameters.CITOKINE_VALUE), x, y);
	}

	/**
	 * Libera uma citocina especifica, com valor determinado, na posicao (X,Y)
	 * 
	 * @param citokine
	 *            Tipo de citocina
	 * @param value
	 *            Quantidade de citocina
	 * @param x
	 *            Coordenada X
	 * @param y
	 *            Coordenada Y
	 * @deprecated (use o outro metodo, para utilizar o parametro do sistema)
	 */
	public void releaseCitokine(CitokineNames citokine, double value, int x,
			int y) {

		if (!citokines.containsKey(citokine)) {
			return;
		}

		if (x >80 || y >80) {
			  System.out.println("X/Y:"+x+"/"+y);
		}
	
		// libera a citocina no layer
		Double currentValue = citokines.get(citokine).getValueLayer().get(x, y);
		try {
			citokines.get(citokine).getValueLayer()
					.get(currentValue + value, x, y);
		} catch (Exception e) {
            System.out.println("X/Y:"+x+"/"+y);
		}
	}

	/**
	 * Atualiza o ambiente
	 */
	public void updateEnvironment() {

		if (citokines == null) {
			System.err.println("Erro: citokines == null");
			return;
		}

		// atualiza a difusao das citocinas
		for (ValueLayerDiffuser vld : citokines.values()) {
			if (vld == null) {
				System.err.println("Erro: vld == null");
				return;
			}
			vld.diffuse();
		}
	}

	// retorna a posicao X da celula nesta zona
	public int getX(Agent c) {
		if (space == null || c == null) {
			return 0;
		}
		if (space.getLocation(c) != null) {
			return space.getLocation(c).getX();
		} else {
			return -1;
		}
	}

	// retorna a posicao Y da celula nesta zona
	public int getY(Agent c) {
		if (space == null || c == null) {
			return 0;
		}
		if (space.getLocation(c) != null) {
			return space.getLocation(c).getY();
		} else {
			return -1;
		}
	}

	/**
	 * Atualiza a posicao atual do agent
	 * 
	 * @param c
	 *            Agente
	 * @param x
	 *            Nova coordenada X
	 * @param y
	 *            Nova coordenada Y
	 */
	public void moveTo(Agent c, int x, int y) {
		if (space == null || c == null) {
			System.err.println("Erro: space or Agent is null!");
			return;
		}
		space.moveTo(c, x, y);
	}

	/**
	 * Atualiza a posicao atual do agente, baseado em um offset (x,y)
	 * 
	 * @param c
	 *            Agente
	 * @param x
	 *            Distancia a andar em X (celulas da grid)
	 * @param y
	 *            Distancia a andar em Y (celulas da grid)
	 */
	public void walkTo(Agent c, int x, int y) {
		if (space == null || c == null) {
			System.err.println("Erro: space or Agent is null!");
			return;
		}

		space.moveTo(c, c.getX() + x, c.getY() + y);
	}

	// metodos get and set
	public int getWidth() {
		return width;
	}

	public void setWidth(int w) {
		width = w;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int h) {
		height = h;
	}

	/**
	 * Obtem o nome da zona
	 * 
	 * @return ZoneNames Nome da Zona
	 */
	public ZoneNames getEnvName() {
		return this.name;
	}

	/**
	 * Remove um agente, tanto da zona que está, quanto do contexto
	 * 
	 * @param agent
	 */
	public void removeAgent(Agent agent) {
		remove(agent);
	}

	/**
	 * Obtem a lista de portais de uma zona
	 * 
	 * @return
	 */
	public ArrayList<Portal> getPortalList() {
		return portals;
	}

}
