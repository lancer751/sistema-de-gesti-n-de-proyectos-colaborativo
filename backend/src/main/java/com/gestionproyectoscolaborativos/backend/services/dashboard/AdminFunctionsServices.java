package com.gestionproyectoscolaborativos.backend.services.dashboard;

import com.gestionproyectoscolaborativos.backend.entitys.Project;
import com.gestionproyectoscolaborativos.backend.entitys.Rol;
import com.gestionproyectoscolaborativos.backend.entitys.Users;
import com.gestionproyectoscolaborativos.backend.entitys.histories.UserProjectRoleHistory;
import com.gestionproyectoscolaborativos.backend.entitys.tablesintermedate.UserProjectRol;
import com.gestionproyectoscolaborativos.backend.repository.*;
import com.gestionproyectoscolaborativos.backend.services.dto.request.ProjectHistoryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminFunctionsServices {

    @Autowired
    private UserProjectRoleHistoryRepository userProjectRoleHistoryRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserProjectRolRepository userProjectRolRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private RolRepository rolRepository;

    @Transactional
    public ResponseEntity<?> asingProjectHistory (ProjectHistoryDto projectHistoryDto) {
        try {
            Users userNewLider = userRepository.findById(projectHistoryDto.getIdNuevoLider()).orElseThrow();
            // 1. Obtener historial de roles eliminados con ese rol específico
            List<UserProjectRoleHistory> historiales = userProjectRoleHistoryRepository.findByRolName(projectHistoryDto.getNombreRol());

            Rol rol = rolRepository.findByName(projectHistoryDto.getNombreRol())
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + projectHistoryDto.getNombreRol()));

            for (UserProjectRoleHistory hisotryProject : historiales ) {
                Integer projectId = hisotryProject.getIdProject();

                if (projectId != null) {
                    Project proyecto = projectRepository.findById(projectId)
                            .orElse(null);

                    // Verificar si ya tiene ese rol en ese proyecto
                    boolean yaAsignado = userProjectRolRepository.existsByUsersAndRolAndProject(userNewLider, rol, proyecto);

                    if (!yaAsignado && proyecto != null) {
                        UserProjectRol nuevaAsignacion = new UserProjectRol();
                        nuevaAsignacion.setUsers(userNewLider);
                        nuevaAsignacion.setRol(rol);
                        nuevaAsignacion.setProject(proyecto);
                        userProjectRolRepository.save(nuevaAsignacion);
                    }
                }
            }
            return ResponseEntity.ok().body("Restaurado con exito");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error" + e.getMessage());
        }

    }
}
