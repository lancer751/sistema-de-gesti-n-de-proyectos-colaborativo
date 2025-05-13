package com.gestionproyectoscolaborativos.backend.services.dto.request;

import com.gestionproyectoscolaborativos.backend.entitys.enums.Priority;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectDto {
    private String name;
    private String description;
    private LocalDateTime dateStart;
    private LocalDateTime dateDeliver; // fecha limite
    private Priority priority;
    private StateDto stateDto;

}
