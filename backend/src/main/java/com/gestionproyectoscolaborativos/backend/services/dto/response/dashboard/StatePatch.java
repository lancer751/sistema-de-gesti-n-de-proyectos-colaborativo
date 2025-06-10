package com.gestionproyectoscolaborativos.backend.services.dto.response.dashboard;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StatePatch {
    private List<Integer> idProjects;
    private String stateName;
}
