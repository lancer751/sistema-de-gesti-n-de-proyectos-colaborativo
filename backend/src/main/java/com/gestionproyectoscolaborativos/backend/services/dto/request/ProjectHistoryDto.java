package com.gestionproyectoscolaborativos.backend.services.dto.request;

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
public class ProjectHistoryDto {


    private Integer idNuevoLider;
    private String nombreRol;
}
