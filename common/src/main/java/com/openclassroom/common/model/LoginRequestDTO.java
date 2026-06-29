package com.openclassroom.common.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginRequestDTO {
	private String username;
	private String password;
}