package autosimmune.agents.portals;

import java.util.ArrayList;

import repast.simphony.engine.schedule.ScheduledMethod;
import autosimmune.agents.cells.Macrophage;
import autosimmune.agents.cells.NK;
import autosimmune.agents.cells.PC;
import autosimmune.defs.CitokineNames;
import autosimmune.defs.EnvParameters;
import autosimmune.defs.MacrophageStates;
import autosimmune.defs.PCStates;
import autosimmune.defs.PortalDirections;
import autosimmune.defs.TissuePortalStates;
import autosimmune.env.Global;
import autosimmune.env.Tissue;

/**
 * Class TissuePortal
 * 
 * @extends Portal
 * 
 * @author maverick
 *
 */
public class TissuePortal extends Portal {
	
	/** estados do portal */
	private TissuePortalStates state;
	
	/** numero de macrofagos a enviar */
	private int sendMacrophageNumber;
	
	/** numero de NK a enviar */
	private int sendNKNumber;
	
	private int ticks;
	
	/** Construtor */
	public TissuePortal(Tissue z, PortalDirections d, int x, int y){
		super(z, d, x, y);
		state = TissuePortalStates.FUNCIONAL;
		sendMacrophageNumber = env.getIntegerParameter(EnvParameters.PORTAL_MACROPHAGE_TO_SEND);
		sendNKNumber = env.getIntegerParameter(EnvParameters.PORTAL_NK_TO_SEND);
		ticks = 0;
	}
	
	/**
	 * Retorna true se o portal estiver funcionando
	 * @return if Funcional
	 * @see TissuePortal.verifyFuncional
	 */
	public boolean isFuncional(){
		return (state == TissuePortalStates.FUNCIONAL);
	}
	
	/**
	 * Verifica se existem células parenquimais suficientes na vizinhança para que exista um portal no local
	 * @return
	 */
	private boolean verifyFuncional(){
		ArrayList<PC> pcs = getEspecificNeighbors(PC.class, 14);
		for(PC pc: pcs){
			if (pc.getState() != PCStates.APOPTOSIS && pc.getState() != PCStates.NECROSIS){
				return true;
			}
		}
		return false;
	}
	
	
	/** funcao chamada pelo framework a cada tick */
	//@SuppressWarnings("deprecation")
	@ScheduledMethod(start = 0, interval = 1)
	public void step(){
		
		//atualiza a liberacao de citocinas do portal
		super.updatePortal();
		
		ticks++;
		
		//TODO parametrizar taxa de envio de Mg e NK pelos Portais
		if (ticks % 15 == 0){
			sendMacrophageNumber = 10;
			sendNKNumber = 10;
		}
		
		switch(state){
			case FUNCIONAL: {

				if (!verifyFuncional()){
					this.state = TissuePortalStates.NONFUNCIONAL;
					return;
				}
				
				double streesCitokine = zone.getCitokineValue(CitokineNames.PK1, getX(), getY());
				if (streesCitokine > Global.getInstance().getFloatParameter(EnvParameters.PORTAL_PK1_THRESHOLD)){
					if (sendMacrophageNumber > 0){
						Macrophage m0 = new Macrophage(zone, getX(), getY());
						//m0.changeState(MacrophageStates.ACTIVE);
						zone.addAgent(m0);
						sendMacrophageNumber--;
					}
				}
				
				double proInflamatory = zone.getCitokineValue(CitokineNames.MK1, getX(), getY());
				if (proInflamatory > Global.getInstance().getFloatParameter(EnvParameters.PORTAL_MK1_THRESHOLD)){
					if(sendNKNumber > 0){
						NK nk = new NK(zone, getX(), getY()); 
						zone.addAgent(nk);
						sendNKNumber--;
					}
				}

				//Imaginando que um portal é um canal que liga o ponto a ao ponto b,
				//aqui nos analisamos o ambiente no ponto a, e se houver alguma citonina, enviamos para o ponto b
				
				//cpl = circulation portal list
				/*
				ArrayList<Portal> cpl = Environment.getEnvironment(EnvNames.Circulation).getPortalList();
				Portal p = cpl.get(RandomUtils.getRandomFromTo(0, cpl.size()-1));
				//TODO parametrizar threshold de "retransmissao" de substancias por portais
				if (proInflamatory > 10){
					p.emitCitokine(CitokineNames.MK1, 100);
				} else {
					p.emitCitokine(CitokineNames.MK1, 0);
				}
				*/
			}
			break;
			
			case NONFUNCIONAL: {
				if (verifyFuncional()){
					this.state = TissuePortalStates.FUNCIONAL;
				}
			}
			break;
		}
	}

}
