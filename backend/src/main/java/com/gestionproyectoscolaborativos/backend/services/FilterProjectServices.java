package com.gestionproyectoscolaborativos.backend.services;

import com.gestionproyectoscolaborativos.backend.entitys.Users;
import com.gestionproyectoscolaborativos.backend.entitys.tablesintermedate.UserProject;
import com.gestionproyectoscolaborativos.backend.repository.ProjectRepository;
import com.gestionproyectoscolaborativos.backend.repository.StateRepository;
import com.gestionproyectoscolaborativos.backend.repository.UserProjectRepository;
import com.gestionproyectoscolaborativos.backend.services.dto.response.StateDto;
import com.gestionproyectoscolaborativos.backend.services.dto.response.UserDtoResponse;
import com.gestionproyectoscolaborativos.backend.services.dto.response.dashboard.SearcLPDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilterProjectServices {
    @Autowired
    private StateRepository stateRepository;


    @Autowired
    private UserProjectRepository userProjectRepository;

    public ResponseEntity<?> readAllState () {
        try {
            List<StateDto> stateDtos = stateRepository.findAll().stream().map(s -> {
                StateDto stateDto = new StateDto();
                stateDto.setName(s.getName());
                return  stateDto;
            }).toList();
            return ResponseEntity.ok().body(stateDtos);
        } catch (Exception e) {
            return  ResponseEntity.badRequest().body("Hubo un error" + e.getMessage());
        }
    }

    public ResponseEntity<Map<String, Object>> searchLiderProject(String search, int page) {
        Map<String, Object> response = new HashMap<>();

        List<SearcLPDto> searchDtos = new ArrayList<>();
        Pageable pageable = PageRequest.of(page, 6, Sort.by("id"));
        Page<Users> usersPage = userProjectRepository.findDistinctLiderUsers(search, pageable);
        

        List<SearcLPDto> paginatedLiders = usersPage.getContent().stream()
                .map(u -> {
                    SearcLPDto dto = new SearcLPDto();
                    dto.setId(u.getId());
                    dto.setName(u.getName() + " " + u.getLastname());
                    return dto;
                }).toList();

        Map<String, Object> jsonPaginado = new HashMap<>();
        jsonPaginado.put("currentPage", usersPage.getNumber());
        jsonPaginado.put("totalItems", usersPage.getTotalElements());
        jsonPaginado.put("totalPages", usersPage.getTotalPages());
        jsonPaginado.put("lider", paginatedLiders);
        response.put("liderproject", jsonPaginado);


        return ResponseEntity.ok(response);
    }

}
