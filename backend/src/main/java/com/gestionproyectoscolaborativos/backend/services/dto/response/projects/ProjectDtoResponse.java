package com.gestionproyectoscolaborativos.backend.services.dto.response.projects;

import com.gestionproyectoscolaborativos.backend.entitys.enums.Priority;
import com.gestionproyectoscolaborativos.backend.services.dto.request.StateDto;
import com.gestionproyectoscolaborativos.backend.services.dto.response.UserDtoResponse;
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
public class ProjectDtoResponse {
    private String name;
    private String description;
    private LocalDateTime dateStart;
    private LocalDateTime dateDeliver; // fecha limite
    private String createdBy;
    private Priority priority;
    private StateDto stateDto;
    private boolean active = true;
    private List<UserDtoResponse> userDtos; // creador del proyecto
    private List<UserRolProjectRequest> userRolProjectRequestList; // integrantes del proyecto
    private List<UserRolProjectRequest> userLider;
}
