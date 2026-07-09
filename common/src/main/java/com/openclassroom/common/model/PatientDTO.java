package com.openclassroom.common.model;

import java.time.LocalDate;
/**
 * Création d'une classe Patient DTO pour pouvoir transiter simplement un patient front/back
 */

import org.springframework.format.annotation.DateTimeFormat;

import com.openclassroom.common.util.Genre;

import lombok.Data;

@Data
public class PatientDTO {
    private Integer id;
    private String nom;
    private String prenom;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateNaissance;
    private Genre genre;
    private String adresse;
    private String telephone;
}
