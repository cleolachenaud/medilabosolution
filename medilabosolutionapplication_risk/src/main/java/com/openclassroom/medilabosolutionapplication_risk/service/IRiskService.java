package com.openclassroom.medilabosolutionapplication_risk.service;

import com.openclassroom.common.model.RiskDTO;

/**
 * interface qui liste les actions que le service va proposer : calculer. Pour l'instant 
 * la liste des actions est limitée, car ce service ne sert que à calculer le niveau de risque diabete. 
 */
public interface IRiskService {

	RiskDTO calculNiveauRisk(Integer patientId) throws Exception;

}
