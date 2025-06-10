package com.gestionproyectoscolaborativos.backend.services.dto.response.projects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ComentsRecentResponseDto {
    private Integer projectId;
    private String author;
    private String titleProject;
    private String lastTime;
}
