package com.gestionproyectoscolaborativos.backend.Controller.dashboard.projects;

import com.gestionproyectoscolaborativos.backend.services.FilterProjectServices;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dashboardadmin")
public class FilterProjectController {
    @Autowired
    private FilterProjectServices filterProjectServices;

    @GetMapping("/filterstate")
    public ResponseEntity<?> readAllState () {
        return filterProjectServices.readAllState();
    }

    @GetMapping("/directedby")
    public ResponseEntity<?> searchLiderProject(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page) {
        return filterProjectServices.searchLiderProject(search, page);
    }
}
