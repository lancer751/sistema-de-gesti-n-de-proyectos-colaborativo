package com.gestionproyectoscolaborativos.backend.Controller.dashboard.projects;


import com.gestionproyectoscolaborativos.backend.services.ProjectServices;
import com.gestionproyectoscolaborativos.backend.services.dto.response.ProjectDtoResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/project")
public class ProjectController {

    @Autowired
    private ProjectServices projectServices;

    @GetMapping("/")
    public ResponseEntity<?> readproject () {
        return projectServices.readprojet();
    }
    @PostMapping("/add")
    public ResponseEntity<?> saveproject (@RequestBody ProjectDtoResponse projectDto){
        return projectServices.save(projectDto);
    }
    @PutMapping("/edit/{id}")
    public  ResponseEntity<?> editproject (@PathVariable Integer id, @RequestBody ProjectDtoResponse projectDto) {
        return projectServices.editproject(id, projectDto);
    }
    @DeleteMapping("/delete/{id}")
    private  ResponseEntity<?> deleteproject(@PathVariable Integer id){
        return projectServices.deleteproject(id);
    }

}
