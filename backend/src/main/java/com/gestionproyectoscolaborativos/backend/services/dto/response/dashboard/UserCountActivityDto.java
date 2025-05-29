package com.gestionproyectoscolaborativos.backend.services.dto.response.dashboard;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserCountActivityDto {
    private String fullName;
    private Long tareasAsigandas;
    private Long tareasCompletadas;
}
