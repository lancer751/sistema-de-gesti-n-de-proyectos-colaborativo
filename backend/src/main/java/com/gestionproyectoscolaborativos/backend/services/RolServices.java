package com.gestionproyectoscolaborativos.backend.services;

import com.gestionproyectoscolaborativos.backend.repository.RolRepository;
import com.gestionproyectoscolaborativos.backend.services.dto.request.RolDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RolServices {

    @Autowired
    private RolRepository rolRepository;

    public ResponseEntity<?> readRol () {
        try {
            List<RolDto> rolDtoList = rolRepository.findAll().stream().map(r -> {
                RolDto rolDto = new RolDto();
                rolDto.setName(r.getName());
                return rolDto;
            }).toList();
            if (!rolDtoList.isEmpty()){
                return ResponseEntity.ok().body(rolDtoList);
            }
            return ResponseEntity.ok().body(rolDtoList);
        } catch ( Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error en los roles" + e.getMessage());
        }
    }
}
