package com.gestionproyectoscolaborativos.backend.services.dto.request;

import com.gestionproyectoscolaborativos.backend.entitys.enums.Priority;
import com.gestionproyectoscolaborativos.backend.services.dto.response.projects.UserRolProjectRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectDto {
    private Integer id;
    private String name;
    private String description;
    private LocalDateTime dateStart;
    private LocalDateTime dateDeliver; // fecha limite
    private Priority priority;
    private StateDto stateDto;
    private String createdBy;
    private boolean active = true;
    private List<UserRolProjectRequest> userLiders;
    private List<UserRolProjectRequest> userRolProjectRequestList; // integrantes del proyecto

}
