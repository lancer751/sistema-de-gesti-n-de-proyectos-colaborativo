package com.gestionproyectoscolaborativos.backend.Controller.dashboard.History;

import com.gestionproyectoscolaborativos.backend.services.dashboard.AdminFunctionsServices;
import com.gestionproyectoscolaborativos.backend.services.dto.request.ProjectHistoryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboardadmin")
public class ProjectHistoryController {
    @Autowired
    private AdminFunctionsServices adminFunctionsServices;

    @PostMapping("/project/restore")
    private ResponseEntity<?> asignHistoryRol (@RequestBody ProjectHistoryDto projectHistoryDto) {
        return adminFunctionsServices.asingProjectHistory(projectHistoryDto);
    }
}
