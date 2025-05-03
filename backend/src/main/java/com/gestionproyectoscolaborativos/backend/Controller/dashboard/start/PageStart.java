package com.gestionproyectoscolaborativos.backend.Controller.dashboard.start;

import com.gestionproyectoscolaborativos.backend.services.dashboard.PageStartServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboardadmin")
public class PageStart {
    @Autowired
    private PageStartServices pageStartServices;

    @GetMapping("/")
    private ResponseEntity<?> userAuthenticate () {
        return pageStartServices.userAuthenticate();
    }


    @GetMapping("/projectmonth")
    private ResponseEntity<?> readdatabarchart () {
        return pageStartServices.readdatabarchart();
    }
    @GetMapping("/projectstates")
    private  ResponseEntity<?> readallproject() {
        return pageStartServices.readstatesbarchart();
    }

    @GetMapping("/taksbystate")
    private ResponseEntity<?> readstatestasks () {
        return pageStartServices.readstatestasks();
    }
    @GetMapping("/projectactivitysusers")
    private ResponseEntity<?> readactivitysbyusers() {
        return pageStartServices.readactivitysbyusers();
    }
    @GetMapping("/projectreadkpisClave")
    private ResponseEntity<?> readkpisClave () {
        return pageStartServices.readkpisClave();
    }
}
