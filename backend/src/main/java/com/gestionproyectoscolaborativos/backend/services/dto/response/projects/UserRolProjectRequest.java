package com.gestionproyectoscolaborativos.backend.services.dto.response.projects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRolProjectRequest {
    private Integer id;
    private String name;
    private String lastname;
    private String numberPhone;
    private String description;
    private String email;
    private String rolProject;
}
