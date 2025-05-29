package com.gestionproyectoscolaborativos.backend.services.dto.response.dashboard;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KpisClave {
    private long totalProjectActive;
    private long tasksProgress;
    private long averageProjectFinish;
    private long averageTaskFinish;
}
