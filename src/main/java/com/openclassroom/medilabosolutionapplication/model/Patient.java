package com.openclassroom.medilabosolutionapplication.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;



import com.openclassroom.medialabosolutionapplication.util.Genre;

import jakarta.annotation.Nonnull;
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
	    @NotNull
	    private String nom;

	    @Column(name = "prenom")
	    @NotNull
	    private String prenom;

		@Column(name = "date_naissance")
	    @NotNull
	    private LocalDate dateNaissance;

	    @Column(name = "genre")
	    @Enumerated(EnumType.STRING)
	    @NotNull
	    private Genre genre;
	 
	    @Column(name = "adresse")
	    private String adresse;
	 
	    @Column(name = "telephone")
	    // rejex pour être sur qu'un téléphone valide soit inséré en bdd
	    @Pattern(regexp = "\\d{10}", message = "Le téléphone doit contenir exactement 10 chiffres")
	    private Integer telephone;
	    
}
