package com.gestionproyectoscolaborativos.backend.services.dashboard;

import com.gestionproyectoscolaborativos.backend.entitys.Project;
import com.gestionproyectoscolaborativos.backend.entitys.Rol;
import com.gestionproyectoscolaborativos.backend.entitys.Users;
import com.gestionproyectoscolaborativos.backend.entitys.histories.UserProjectRoleHistory;
import com.gestionproyectoscolaborativos.backend.entitys.tablesintermedate.UserProjectRol;
import com.gestionproyectoscolaborativos.backend.repository.*;
import com.gestionproyectoscolaborativos.backend.services.dto.request.ProjectHistoryDto;
import com.gestionproyectoscolaborativos.backend.services.dto.request.RolDto;
import com.gestionproyectoscolaborativos.backend.services.dto.request.UserDto;
import com.gestionproyectoscolaborativos.backend.services.dto.request.UserPatchDto;
import com.gestionproyectoscolaborativos.backend.services.dto.response.UserDtoResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

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

    @Transactional(readOnly = true)
    public ResponseEntity<?> userByID (Integer id){
        try {
            Users users = userRepository.findById(id).orElseThrow();
            UserDtoResponse userDtoResponse = new UserDtoResponse();
            userDtoResponse.setId(users.getId());
            userDtoResponse.setName(users.getName());
            userDtoResponse.setLastname(users.getLastname());
            userDtoResponse.setEmail(users.getEmail());
            userDtoResponse.setDescription(users.getDescription());
            userDtoResponse.setEntryDate(users.getEntryDate());
            Set<String> roleUnique = new HashSet<>();

            List<RolDto> rolDtoList = users.getUserProjectRols().stream()
                    .map(rol -> rol.getRol().getName())
                    .filter(roleUnique::add) // solo agrega si es nuevo
                    .map(nombre -> {
                        RolDto rolDto = new RolDto();
                        rolDto.setName(nombre);
                        return rolDto;
                    }).collect(Collectors.toList());
            userDtoResponse.setRolDtoList(rolDtoList);
            userDtoResponse.setActive(users.isEnable());
            return ResponseEntity.ok().body(userDtoResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    public ResponseEntity<Map<String, String>> editarUsuarios(UserPatchDto userPatchDtos) {
        Map<String, String> json = new HashMap<>();

        try {
            for (Integer id : userPatchDtos.getUserIds()) {
                Users user = userRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Usuario con ID " + id + " no encontrado"));

                // Actualizar atributos generales si están presentes
                if (userPatchDtos.getEntryDate() != null) {
                    user.setEntryDate(userPatchDtos.getEntryDate());
                }

                user.setEnable(userPatchDtos.isEnable());

                // Actualizar rol si viene en el DTO
                if (userPatchDtos.getRol() != null && !userPatchDtos.getRol().isBlank()) {
                    editUserRol(user, userPatchDtos); // Este método ya guarda el nuevo rol
                }

                // Guardar cambios del usuario
                userRepository.save(user);
            }

            json.put("message", "Usuarios editados exitosamente");
            return ResponseEntity.ok(json);

        } catch (Exception e) {
            json.put("message", "Hubo un error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(json);
        }
    }


    private static UserProjectRoleHistory getUserProjectRoleHistory(UserProjectRol upr) {
        UserProjectRoleHistory userProjectRoleHistory = new UserProjectRoleHistory();
        userProjectRoleHistory.setIdProject(upr.getProject().getId());
        userProjectRoleHistory.setNameProject(upr.getProject().getName());
        userProjectRoleHistory.setIdUser(upr.getUsers().getId());
        userProjectRoleHistory.setUserName(upr.getUsers().getName() + " " + upr.getUsers().getLastname());
        userProjectRoleHistory.setIdRol(upr.getRol().getId());
        userProjectRoleHistory.setRolName(upr.getRol().getName());
        return userProjectRoleHistory;
    }

    private void editUserRol(Users user, UserPatchDto dto) {
        // 1. Obtener los roles actuales del usuario
        List<UserProjectRol> rolesActuales = userProjectRolRepository.findByUsers(user);

        // 2. Obtener el nuevo rol desde el DTO
        String nuevoRol = dto.getRol();

        // 3. Si el usuario ya tiene ese rol, no hacemos nada
        boolean yaTieneRol = rolesActuales.stream()
                .anyMatch(r -> r.getRol().getName().equalsIgnoreCase(nuevoRol));

        if (yaTieneRol) {
            return; // Nada que actualizar
        }

        // 4. Guardar historial si tenía algún rol "fuerte" (por ejemplo, lider o admin)
        for (UserProjectRol upr : rolesActuales) {
            String nombreRolActual = upr.getRol().getName();

            if (nombreRolActual != null && (nombreRolActual.startsWith("ROLE_LIDER") || nombreRolActual.startsWith("ROLE_ADMIN"))) {
                if (upr.getProject() != null) {
                    UserProjectRoleHistory historial = getUserProjectRoleHistory(upr);
                    userProjectRoleHistoryRepository.save(historial);
                }
            }

            userProjectRolRepository.delete(upr); // eliminar todos los roles actuales
        }
        Rol rol = rolRepository.findByName(nuevoRol).orElseThrow();
        // 5. Asignar el nuevo rol
        UserProjectRol nuevo = new UserProjectRol();
        nuevo.setUsers(user);
        nuevo.setRol(rol);
        userProjectRolRepository.save(nuevo);
    }


}
