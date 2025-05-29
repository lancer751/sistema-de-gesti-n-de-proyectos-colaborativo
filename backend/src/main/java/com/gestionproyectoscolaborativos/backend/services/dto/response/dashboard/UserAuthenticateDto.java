package com.gestionproyectoscolaborativos.backend.services.dto.response.dashboard;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserAuthenticateDto {
    private String username;
    private String lastName;
}
