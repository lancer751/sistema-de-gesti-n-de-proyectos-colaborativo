package com.gestionproyectoscolaborativos.backend.Controller.dashboard.rol;

import com.gestionproyectoscolaborativos.backend.services.RolServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboardadmin")
public class RolController {
    @Autowired
    private RolServices rolServices;

    @GetMapping("/rollist")
    public ResponseEntity<?> readRol () {
        return rolServices.readRol();
    }
}
