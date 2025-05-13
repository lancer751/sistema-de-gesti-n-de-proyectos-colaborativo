package com.gestionproyectoscolaborativos.backend.services.dto.response;

import com.gestionproyectoscolaborativos.backend.entitys.enums.Priority;
import com.gestionproyectoscolaborativos.backend.services.dto.request.UserDto;
import com.gestionproyectoscolaborativos.backend.services.dto.request.StateDto;
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
    private Priority priority;
    private StateDto stateDto;
    private List<UserDtoResponse> userDtos;
}
