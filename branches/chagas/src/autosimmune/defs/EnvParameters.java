package autosimmune.defs;

public enum EnvParameters {
	
	/* Parametros das substancias e seu processo de difusao */
	EVAPORATION_RATE ("evaporationRate"),
	DIFFUSION_CONSTANT ("difusionConstant"),
	CITOKINE_VALUE ("citokineValue"),
		
	/* Parametros da especificidade */
	SELF_PATTERN_LENGHT ("selfPatternLenght"),
	RECEPTOR_PATTERN_LENGHT ("receptorPatternLenght"),
	AFFINITY_THRESHOLD ("affinityThreshold"),
	
	/* Parametros da zona Tissue */
	TISSUE_WIDTH ("tissueWidth"),
	TISSUE_HEIGHT ("tissueHeight"),
	TISSUE_QUARTER ("tissueQuarter"),
	
	/* Parametros da zona Circulation */
	CIRCULATION_WIDTH ("circulationWidth"),
	CIRCULATION_HEIGHT ("circulationHeight"),

	/* Parametros da zona Lymphnode */
	LYMPHNODE_WIDTH ("lymphnodeWidth"),
	LYMPHNODE_HEIGHT ("lymphnodeHeight"),
	
	/* Parametros das células Dendríticas */
	DENDRITIC_CELLS_COUNT ("dendriticCellsCount"),
	DENDRITIC_SELF_PATTERN ("dendriticSelfPattern"),
	DENDRITIC_LYMPHNODE_LIFETIME ("dendriticLymphnodeLifetime"),
	DENDRITIC_PK1_ACTIVATION_THRESHOLD ("dendriticPK1ActivationThreshold"),
	DENDRITIC_MK1_ACTIVATION_THRESHOLD ("dendriticMK1ActivationThreshold"),
	DENDRITIC_TISSUE_MK1_DURATION ("dendriticTissueMK1Duration"),
	DENDRITIC_LYMPHNODE_MK1_DURATION ("dendriticLymphnodeMK1Duration"),
	
	/* Parametros dos anticorpos */
	ANTIBODY_LIFETIME ("antibodyLifetime"),
	ANTIBODY_SELF_PATTERN ("antibodySelfPattern"),
	
	/* Parametros dos Virus */
	VIRUS_SELF_PATTERN ("virusSelfPattern"),
	VIRUS_TARGET_PATTERN ("virusTargetPattern"),
	VIRUS_VIRULENCY ("virusVirulency"),
	VIRUS_LATENCY ("virusLatency"),
	
	/* Parametros dos TCRUZIs */
	TCRUZI_SELF_PATTERN ("tcruziSelfPattern"),
	TCRUZI_TARGET_PATTERN ("tcruziTargetPattern"),
	TCRUZI_VIRULENCY ("tcruziVirulency"),
	TCRUZI_LATENCY ("tcruziLatency"),
	TCRUZI_NUM_BREACH ("tcruziNumBreach"),
	
	/* Parametros das celulas PC */
	PC_SELF_PATTERN ("pcSelfPattern"),
	PC_STRESS_TRHESHOLD ("pCStressThreshold"),
	PC_STRESS_MAX_TIME ("pCStressMaxTime"),
	
	/* Parametros das celulas NK */
	NK_SELF_PATTERN ("nKSelfPattern"),
	NK_KILL_LIMIT ("nKKillLimit"),
	NK_NOKILL_TIMEOUT ("nKNoKillTimeout"),
	NK_LIFETIME ("nKLifetime"),
	NK_CK1_DURATION ("nKCK1Duration"),
	
	/* Parametros das celulas Th */
	TH_SELF_PATTERN ("thSelfPattern"),
	TH_CK1_DURATION ("thCK1Duration"),
	TH_LIFETIME ("thLifetime"),
	TH_PROLIFERATION_COUNT ("thProliferationCount"),
	TH_CK1_MEMORY_THRESHOLD ("thCK1MemoryThreshold"),
	TH_MEMORY_PROLIFERATION_COUNT ("thMemoryProliferationCount"),
	
	/* Parametros das celulas CTL */
	CTL_SELF_PATTERN ("cTLSelfPattern"),
	CTL_TISSUE_LIFETIME ("cTLTissueLifetime"),
	CTL_LYMPHNODE_LIFETIME ("cTLLymphnodeLifetime"),
	CTL_TISSUE_CK1_DURATION ("cTLTissueCK1Duration"),
	CTL_LYMPHNODE_CK1_DURATION ("cTLLymphnodeCK1Duration"),
	CTL_CK1_MEMORY_THRESHOLD ("cTLCK1MemoryThreshold"),
	CTL_PK1_MEMORY_THRESHOLD ("cTLPK1MemoryThreshold"),
	CTL_PROLIFERATION_COUNT ("cTLProliferationCount"),
	
	/* Parametros das celulas Macrofagos */
	MACROPHAGE_SELF_PATTERN ("macrophageSelfPattern"),
	MACROPHAGE_MK1_DURATION ("macrophageMK1Duration"),
	MACROPHAGE_LIFETIME ("macrophageLifetime"),
	
	/* Parametros das celulas BCell */
	BCELL_SELF_PATTERN ("bCellSelfPattern"),
	BCELL_GERMINAL_PROBABILITY ("bCellGerminalProbability"),
	BCELL_MK1_MEMORY_THRESHOLD ("bCellMK1MemoryThreshold"),
	BCELL_CK1_MEMORY_THRESHOLD ("bCellCK1MemoryThreshold"),
	BCELL_LIFETIME ("bCellLifetime"),
	BCELL_ANTIBODY_COUNT ("bCellAntibodyCount"),
	
	/* Parametros dos Portais */
	PORTAL_MACROPHAGE_TO_SEND ("portalMacrophageToSend"),
	PORTAL_NK_TO_SEND ("portalNKToSend"),
	PORTAL_PK1_THRESHOLD ("portalPK1Threshold"),
	PORTAL_MK1_THRESHOLD ("portalMK1Threshold"),
	
	XFICTICIO ("ficticio");
	
	
	private String n;
	
	EnvParameters(String n){
		this.n = n;
	}
	
	@Override
	public String toString(){
		return this.n;
	}
}
