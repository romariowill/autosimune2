package autosimmune.env;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;

import repast.simphony.context.Context;
import repast.simphony.context.DefaultContext;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.parameter.Parameters;
import autosimmune.agents.Agent;
import autosimmune.agents.pathogens.Virus;
import autosimmune.defs.EnvParameters;
import autosimmune.defs.PAMPS;
import autosimmune.defs.ZoneNames;
import autosimmune.utils.Pattern;

public class Global extends DefaultContext<Agent>  {

	private static Global instance;

	private int ticks;
	
	private Logger log = null;
	
	public Global(){
		super("AutoSimmune");
		instance = this;
		this.ticks = 0;
		log = Logger.getRootLogger();
		ConsoleAppender ca = new ConsoleAppender(new SimpleLayout());
		log.addAppender(ca);
	}
	
	public Context<Object> build(Context<Object> context) {
	
		context.setId("AutoSimmune");
		
		PAMPS pamps = PAMPS.getInstance();
		pamps.addPamp(new Pattern("00111100"));
		
		Tissue tissue = new Tissue();
		//Circulation circulation = new Circulation();
		//Lymphnode lymphnode = new Lymphnode();
		
		addSubContext(tissue);
	
		//addSubContext(circulation);
		
		//addSubContext(lymphnode);
		
		ISchedule schedule = RunEnvironment.getInstance().getCurrentSchedule();
		ScheduleParameters params = ScheduleParameters.createOneTime(1);
		schedule.schedule(params, this, "updateEnvironment");
		
		//termina a funcao retornando o ambiente ja configurado
		return context;
	}
	
	//retorna um parametro inteiro
	public int getIntegerParameter(EnvParameters n){
		//acessa os parâmetros do ambiente
		Parameters p = RunEnvironment.getInstance().getParameters();
		return (Integer) p.getValue(n.toString());
	}
	
	//retorna um parametro booleano
	public boolean getBoolParameter(EnvParameters n){
		//acessa os parâmetros do ambiente
		Parameters p = RunEnvironment.getInstance().getParameters();
		return p.getValue(n.toString()).toString().equals("true");
	}
	
	/**
	 * Obtem o valor de um parametro do tipo ponto flutuante
	 * @param n Nome do Parametro
	 * @return float Valor do Parametro
	 * @see EnvParameters
	 */
	public float getFloatParameter(EnvParameters n){
		//acessa os parâmetros do ambiente
		Parameters p = RunEnvironment.getInstance().getParameters();
		return Float.parseFloat(p.getValueAsString(n.toString()));
	}
	
	/**
	 * Obtem o valor de um parametro do tipo String
	 * @param n Nome do Parametro
	 * @return String Valor do Parametro
	 * @see EnvParameters
	 */
	public String getStringParameter(EnvParameters n){
		Parameters p = RunEnvironment.getInstance().getParameters();
		return p.getValueAsString(n.toString());
	}
	

	//realiza atualizacoes globais
	//@ScheduledMethod(start=0,interval=1)
	public void updateEnvironment(){
		Iterable<Context<? extends Agent>> it  = getSubContexts();
		for(Context<? extends Agent> c: it){
			((Environment) c).updateEnvironment();
		}
		
		ticks++;
		
		if(ticks == 15){
			Tissue ts = (Tissue) Environment.getEnvironment(ZoneNames.Tissue);
			//int vx = RandomUtils.getRandomFromTo(0, ts.getWidth());
			//int vy = RandomUtils.getRandomFromTo(0, ts.getHeight());
			for(int i = 0; i < 40; i++){
				//ts.addAgent(new Virus(ts, vx, vy));
				ts.addAgent(new Virus(ts, 75, 75));
			}
		}
		
		//re-infeccao
		//TODO parametrizar a opção de simular re-infeccao
		/*
		if(ticks == 300){
			Tissue ts = (Tissue) Environment.getEnvironment(EnvNames.Tissue);
			if(ts.getObjects(Virus.class).size() == 0){
				for(int i = 0; i < 40; i++){
					ts.addAgent(new Virus(ts, 150, 150, SELF.PC.getPattern(), new Pattern(this.getStringParameter(EnvParameters.VIRUS_ANTIGEN))));
				}
			}
		}
		*/
		
	}

	public static Global getInstance() {
		return instance;
	}
}