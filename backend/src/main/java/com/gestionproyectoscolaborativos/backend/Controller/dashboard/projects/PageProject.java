package com.gestionproyectoscolaborativos.backend.Controller.dashboard.projects;

import com.gestionproyectoscolaborativos.backend.services.dashboard.PageProjectServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/dashboardadmin")
@RestController
public class PageProject {
    @Autowired
    private PageProjectServices projectServices;

    @GetMapping("/projectrecent")
    private ResponseEntity<?> readprojectrecient (){
        return projectServices.readprojectrecient();
    }
    @GetMapping("/projectnextdelivery")
    private ResponseEntity<?> readprojectdevilery () {
        return projectServices.readnextdelivery();
    }
    @GetMapping("/comentrecient")
    private ResponseEntity<?> readcomentrecient() {
        return projectServices.readcomentrecient();
    }
    @GetMapping("/projectall")
    private ResponseEntity<?> readprojectsall ()  {
        return projectServices.readProjectsAdmin();
    }
}
