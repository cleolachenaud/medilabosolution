package com.openclassroom.medilabosolutionapplication_risk.service;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.Period;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.openclassroom.common.model.NotesDTO;
import com.openclassroom.common.model.PatientDTO;
import com.openclassroom.common.model.RiskDTO;
import com.openclassroom.common.util.ConstantesSeuilRisk;
import com.openclassroom.common.util.Genre;
import com.openclassroom.common.util.NiveauRisk;
import com.openclassroom.medilabosolutionapplication_risk.proxies.IMicroserviceNotesProxy;
import com.openclassroom.medilabosolutionapplication_risk.proxies.IMicroservicePatientsProxy;

import feign.FeignException;

@Service
public class RiskServiceImpl implements IRiskService{
	
	// liste des termes déclencheurs
	private Set<String> declencheursPossibles = Set.of("hemoglobine a1c", "microalbumine", "taille", "poids", "fumeur", "fumeuse", "anormal", "cholesterol", "vertiges", "rechute", "reaction", "anticorps");
	private Set<String> declencheursPossiblesNormalises = new HashSet<String>();

    @Autowired
    private final IMicroserviceNotesProxy notesProxy;
    @Autowired
    private final IMicroservicePatientsProxy patientProxy;
    
    // constructeur pour me permettre de récupérer les notes et les patients via feign. 
    public RiskServiceImpl(IMicroserviceNotesProxy notesProxy, IMicroservicePatientsProxy patientProxy) {
        this.notesProxy = notesProxy;
        this.patientProxy = patientProxy;
        // Normalisation de la liste, sécurisant la saisie manuel côté développement 
		for (String declencheur : declencheursPossibles) {
	    	declencheursPossiblesNormalises.add(normalizeText(declencheur.toLowerCase()));
        }
    }
        
    /**
     * retourne le score de risque diabète du patient en fonction des règles métiers.
     * @param authenticatedUser header X-Authenticated-User à propager vers patient et notes
     */
	@Override
	public RiskDTO calculNiveauRisk(Integer patientId) throws Exception {
		PatientDTO patient = getPatient(patientId);
		List<NotesDTO> notes;
		try {
			notes = getNotes(patientId);
		}catch(FeignException.NotFound e) {
			notes = List.of();
		}
		RiskDTO risk = calculRisk(patient, notes);
		return risk;
	}

	/**
	 * récupère les infos du patient
	 */
	private PatientDTO getPatient(Integer patientId) throws Exception {
		ResponseEntity<PatientDTO> patient = patientProxy.getPatientById(patientId);
		if(!patient.hasBody()) {
			throw new Exception("problème lors de la récupération du patient");
		}
		return patient.getBody();
	}
	
	/**
	 * récupère les notes du patient.
	 * Dans le cas d'un patient sans notes, on retourne une liste vide.
	 */
	private List<NotesDTO> getNotes(Integer patientId){
		ResponseEntity<List<NotesDTO>> listeNotes = notesProxy.getNotesByPatient(patientId);
		if (!listeNotes.hasBody() || listeNotes.getBody() == null) {
		    return List.of();
		}
		return listeNotes.getBody();
	}
	
	/**
	 * méthode pour calculer le risque diabète d'un patient 
	 * @param patient
	 * @param notes
	 * @return
	 */
	private RiskDTO calculRisk(PatientDTO patient, List<NotesDTO> notes) {
		int nombreDeclencheursUniques = compterNombreDeclencheurUnique(notes);
		  
		// je determine le risque d'après les données ci-dessus
		RiskDTO riskPatient = new RiskDTO();
		riskPatient.setNiveauRisk(NiveauRisk.AUCUN_RISQUE);
		
		/* Les règles pour déterminer les niveaux de risque sont les suivantes :
			- aucun risque (None) :
				- Le dossier du patient ne contient aucune note du médecin contenant les déclencheurs (terminologie) ;
			- risque limité (Borderline) :
				- Le dossier du patient contient entre deux et cinq déclencheurs et le patient est âgé de plus de 30 ans ;
			- danger (In Danger) :
				- Si le patient est un hommede moins de 30 ans, alors trois termes déclencheurs doivent être présents.
				- Si le patient est une femme et a moins de 30 ans, il faudra quatre termes déclencheurs.
				- Si le patient a plus de 30 ans, alors il en faudra six ou sept ;
			- apparition précoce (Early onset) :
				- Si le patient est un homme de moins de 30 ans, alors au moins cinq termes déclencheurs sont nécessaires.
				- Si le patient est une femme et a moins de 30 ans, il faudra au moins sept termes déclencheurs.
				- Si le patient a plus de 30 ans, alors il en faudra huit ou plus.
		 */
		if (nombreDeclencheursUniques >= 2) {
			// j'analyse le patient 
			int agePatient = calculerAge(patient.getDateNaissance());
			if(agePatient < ConstantesSeuilRisk.AGE_SEUIL) {// patient de moins de 30 ans  
				if(Genre.M.equals(patient.getGenre())){ // si Homme
					if(nombreDeclencheursUniques >= ConstantesSeuilRisk.SEUIL_ADULTE_APPARITIONPRECOCE ) {// apparition précoce cinq termes déclencheurs ou +
						riskPatient.setNiveauRisk(NiveauRisk.APARITION_PRECOCE);
					} else if (nombreDeclencheursUniques >= ConstantesSeuilRisk.SEUIL_HOMME_DANGER ) {// danger  trois termes déclencheurs ou +
						riskPatient.setNiveauRisk(NiveauRisk.DANGER);
					}
				} else if(Genre.F.equals(patient.getGenre())){ // si femme
					if(nombreDeclencheursUniques >= ConstantesSeuilRisk.SEUIL_FEMME_APPARITIONPRECOCE) {// apparition précoce sept termes déclencheurs ou +
						riskPatient.setNiveauRisk(NiveauRisk.APARITION_PRECOCE);
					} else if (nombreDeclencheursUniques >= ConstantesSeuilRisk.SEUIL_FEMME_DANGER) { // danger quatre termes déclencheurs ou +
						riskPatient.setNiveauRisk(NiveauRisk.DANGER);
					}
				}
			} else { // le patient à plus de 30 ans
				if (nombreDeclencheursUniques >= ConstantesSeuilRisk.SEUIL_ADULTE_APPARITIONPRECOCE ){ // apparition précoce huit ou +
					riskPatient.setNiveauRisk(NiveauRisk.APARITION_PRECOCE);
				} else if (nombreDeclencheursUniques >= ConstantesSeuilRisk.SEUIL_ADULTE_DANGER) {
					riskPatient.setNiveauRisk(NiveauRisk.DANGER); // danger 6 ou +
				} else if(nombreDeclencheursUniques >= ConstantesSeuilRisk.SEUIL_ADULTE_LIMITE) {
					riskPatient.setNiveauRisk(NiveauRisk.RISQUE_LIMITE); // risque limité 2 ou +
				}
			}
		}
		// je retourne le risk
		return riskPatient;
	}
/**
 * Permet de compter le nombre de déclencheurs uniques dans les notes du patient
 * @param notes
 * @return
 */
	private int compterNombreDeclencheurUnique(List<NotesDTO> notes) {
		// j'analyse les notes et je compte le nombre de déclencheurs présents
		Set<String> declencheursUniques = new HashSet<>(); 
	
		for (NotesDTO note : notes) {
			String contenuNormalise = normalizeText(note.getContenu().toLowerCase());
			for (String declencheur : declencheursPossiblesNormalises) {
	            if (contenuNormalise.contains(declencheur)) {
	                declencheursUniques.add(declencheur);
	            }
	        }
			if (declencheursUniques.size()>= ConstantesSeuilRisk.SEUIL_MAX_DECLENCHEURS) {
				break; // Risque maximum, parcours des notes suivantes inutiles 
			}
	    }	
		int nombreDeclencheursUniques = declencheursUniques.size();
		return nombreDeclencheursUniques;
	}
	
	// méthodes Utiles // 
	/**
	 * pour supprimer les majuscules et les accents qui pourraient poser pb 
	 */
	private String normalizeText(String text) {
	    String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
	    return normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
	}
	
	/**
	 * pour calculer l'age du patient
	 * @param dateNaissance
	 * @return
	 */
	private int calculerAge(LocalDate dateNaissance) {
	    if (dateNaissance == null) {
	        throw new IllegalArgumentException("La date de naissance ne peut pas être nulle");
	    }
	    LocalDate dateActuelle = LocalDate.now();
	    if (dateNaissance.isAfter(dateActuelle)) {
	        throw new IllegalArgumentException("La date de naissance ne peut pas être dans le futur");
	    }
	    return Period.between(dateNaissance, dateActuelle).getYears();
	}
}
