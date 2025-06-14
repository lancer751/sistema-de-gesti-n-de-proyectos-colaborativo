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
public class ActivityProjecRequest {
    private String name;
    private String description;
    private LocalDateTime dateStart;
    private LocalDateTime dateDeliver; // fecha entrega
    private Priority prioridad;
    private StateDto state;
    private Integer activityFatherId;
    private List<UserRolProjectRequest> users;
}
