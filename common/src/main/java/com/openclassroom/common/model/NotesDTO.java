package com.openclassroom.common.model;


import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NotesDTO {
	@NotBlank(message = "Le contenu de la note ne peut pas être vide")
	 private String contenu;
	 private LocalDate date;
}
