package com.openclassroom.patient.model;

import java.time.LocalDate;

import com.openclassroom.patient.util.Genre;
import com.openclassroom.patient.util.MessageErreur;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "patient")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
 
    @Column(name = "nom")
    @NotNull(message = "le nom est obligatoire")
    @Size(min = 1, max = 250, message = MessageErreur.CHAMP_TAILLE)
    private String nom;

    @Column(name = "prenom")
    @NotNull(message = "Le prénom est obligatoire")
    @Size(min = 1, max = 250, message = MessageErreur.CHAMP_TAILLE)
    private String prenom;

	@Column(name = "date_naissance")
	@NotNull(message = "La date de naissance est obligatoire")
	
    private LocalDate dateNaissance;

    @Column(name = "genre")
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Le genre est obligatoire")
    private Genre genre;
 
    @Column(name = "adresse")
    private String adresse;
 
    @Column(name = "telephone")
    // rejex pour être sur qu'un téléphone valide soit inséré en bdd
    @Pattern(regexp = "^(\\d{10})?$", message = "Le téléphone doit contenir exactement 10 chiffres")
    private String telephone;
}
