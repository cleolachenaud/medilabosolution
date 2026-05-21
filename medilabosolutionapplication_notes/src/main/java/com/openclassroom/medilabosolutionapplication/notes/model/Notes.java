package com.openclassroom.medilabosolutionapplication.notes.model;


import java.time.Instant;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
@Document(collection = "notes")
@Data
public class Notes {

	@Id
	private String id;
	
	private Integer patientId;
	
	private String notes;
	
	private Instant date;

}
