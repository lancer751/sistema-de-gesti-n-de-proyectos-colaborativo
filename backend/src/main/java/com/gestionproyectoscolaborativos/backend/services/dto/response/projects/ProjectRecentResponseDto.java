package com.gestionproyectoscolaborativos.backend.services.dto.response.projects;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectRecentResponseDto {
    private Integer id;
    private String title;
    private String timeFinish;
}
