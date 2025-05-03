package com.gestionproyectoscolaborativos.backend.Controller.dashboard.users;

import com.gestionproyectoscolaborativos.backend.services.UserServices;
import com.gestionproyectoscolaborativos.backend.services.dto.request.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dashboardadmin")
public class PageUser {
    @Autowired
    private UserServices userServices;

    // Pagination users
    @GetMapping("/user")
    public ResponseEntity<?> read(
            @RequestParam(defaultValue = "") String role,
            @RequestParam(defaultValue = "") String enable,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "2") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir){
        Pageable pageable;

        if ("rol".equalsIgnoreCase(sortBy)) {
            // No sort en la BD si el ordenamiento es por rol
            pageable = PageRequest.of(page, size);
        } else {
            Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
            pageable = PageRequest.of(page, size, sort);
        }

        return userServices.read(pageable, sortBy, sortDir, enable, role);
    }

    @PostMapping("/registeruser")
    public ResponseEntity<?> register(@RequestBody UserDto userDto){

        return userServices.save(userDto);
    }
}
