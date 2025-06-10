package com.gestionproyectoscolaborativos.backend.Controller.dashboard.projects;

import com.gestionproyectoscolaborativos.backend.entitys.enums.Priority;
import com.gestionproyectoscolaborativos.backend.services.dashboard.PageProjectServices;
import com.gestionproyectoscolaborativos.backend.services.dto.response.dashboard.StatePatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;

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

    @GetMapping("/admin-projects")
    public ResponseEntity<?> readProjectsAdmin(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "15") int size,
            @RequestParam(value = "iduser", required = false) Integer iduser,
            @RequestParam(value = "state", required = false) String state,
            @RequestParam(value = "priority", required = false) Priority priority,
            @RequestParam(value = "sortBy", defaultValue = "name") String sortBy,
            @RequestParam(value = "startp", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            Date startp,
            @RequestParam(value = "endp", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endp,
            @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return ResponseEntity.ok(
                projectServices.readProjectsAdmin(pageable, search, iduser, state, priority, startp, endp)
        );
    }

    @PatchMapping("/projectseditlist")
    private  ResponseEntity<?> projectseditlist (@RequestBody StatePatch statePatch) {
        return projectServices.projectseditlist(statePatch);
    }




}
