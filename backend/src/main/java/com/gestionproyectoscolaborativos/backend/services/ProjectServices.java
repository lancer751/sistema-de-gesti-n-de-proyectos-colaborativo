package com.gestionproyectoscolaborativos.backend.services;

import com.gestionproyectoscolaborativos.backend.entitys.Project;
import com.gestionproyectoscolaborativos.backend.entitys.Rol;
import com.gestionproyectoscolaborativos.backend.entitys.State;
import com.gestionproyectoscolaborativos.backend.entitys.Users;
import com.gestionproyectoscolaborativos.backend.entitys.tablesintermedate.UserProjectRol;
import com.gestionproyectoscolaborativos.backend.repository.*;
import com.gestionproyectoscolaborativos.backend.services.dto.request.RolDto;
import com.gestionproyectoscolaborativos.backend.services.dto.request.UserDto;
import com.gestionproyectoscolaborativos.backend.services.dto.request.ProjectDto;
import com.gestionproyectoscolaborativos.backend.services.dto.response.ProjectDtoResponse;
import com.gestionproyectoscolaborativos.backend.services.dto.request.StateDto;
import com.gestionproyectoscolaborativos.backend.services.dto.response.UserDtoResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProjectServices {

    @Autowired
    private RolRepository repository;
    @Autowired
    private UserProjectRolRepository userProjectRolRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private StateRepository stateRepository;

    @Transactional(readOnly = true)
    public ResponseEntity<?> readprojet () {
        Users users = getAuthenticatedUser();
        List<UserProjectRol> userProjectRolList = userProjectRolRepository.findByUsers(users);
        List<ProjectDtoResponse> projectDtos = userProjectRolList.stream()
                .filter(p -> p.getProject() != null)
                .map(p -> {
                    Project project = p.getProject();
                    // Lista de usuarios del proyecto
                    List<UserDtoResponse> userDtos = userProjectRolRepository.findByProject(project).stream()
                            .map(us -> {
                                List<RolDto> rolDtoList = userProjectRolRepository.findRolesByUser(us.getUsers()).stream().map( r -> {
                                    RolDto rolDto = new RolDto();
                                    if(!r.getName().equals("ROL_ADMIN")){
                                        rolDto.setName(r.getName());
                                        return rolDto;
                                    }
                                    return  rolDto;
                                }).collect(Collectors.toList());
                                UserDtoResponse user = new UserDtoResponse();
                                user.setName(us.getUsers().getName());
                                user.setLastname(us.getUsers().getLastname());
                                user.setEmail(us.getUsers().getEmail());
                                user.setRolDtoList(rolDtoList);
                                return user;
                            }).collect(Collectors.toList());
                    // Armar DTO del proyecto
                    ProjectDtoResponse projectDto = new ProjectDtoResponse();
                    projectDto.setName(project.getName());
                    projectDto.setPriority(project.getPriority());
                    projectDto.setDescription(project.getDescription());
                    projectDto.setDateStart(project.getDateStart());
                    projectDto.setDateDeliver(project.getDateDeliver());
                    projectDto.setStateDto(new StateDto(project.getState().getName()));
                    projectDto.setUserDtos(userDtos);
                    return projectDto;
                }).collect(Collectors.toList());

        if (projectDtos.isEmpty()) {
            return ResponseEntity.ok("You don't have any associated projects");
        }

        return ResponseEntity.ok(projectDtos);
    }

    @Transactional
    public ResponseEntity<?> save (ProjectDtoResponse projectDto){
        // Obtener usuario autenticado
        Users user = getAuthenticatedUser();

        // Verificar que tenga un rol válido
        Rol userRol = getUserValidRol(user);
        if (userRol == null) {
            return ResponseEntity.badRequest().body("The user rol is invalid");
        }

        // Crear o usar estado
        State state = getOrCreateState(projectDto.getStateDto().getName());

        // Crear el proyecto
        Project project = createAndSaveProject(projectDto, state);

        // Asociar al usuario autenticado
        createUserProjectRelation(user, userRol, project);

        // Asociar a otros usuarios (si vienen en la lista)
        if (projectDto.getUserDtos() != null) {
            for (UserDtoResponse userDto : projectDto.getUserDtos()) {
                userRepository.findByEmail(userDto.getEmail()).ifPresent(extraUser -> {
                    Rol extraRol = extraUser.getUserProjectRols().stream().map(UserProjectRol::getRol)
                            .filter(r -> !"ROLE_ADMIN".equals(r.getName()))
                            .findFirst()
                            .orElse(null);
                    if (extraRol != null) {
                        createUserProjectRelation(extraUser, extraRol, project);
                    }
                });
            }
        }
        return ResponseEntity.ok("project created successful");
    }

    @Transactional
    public ResponseEntity<?> editproject (Integer id, ProjectDtoResponse projectDto) {
        Integer idexit = 0;

        Users user = getAuthenticatedUser();

        List<UserProjectRol> userProjectRolList = userProjectRolRepository.findByUsers(user);

        for (UserProjectRol userProjectRol : userProjectRolList) {
            Project project = userProjectRol.getProject();
            if (project != null && project.getId().equals(id)) {
                idexit = id;
            }
        }
        if (idexit == 0 ) {
            return ResponseEntity.badRequest().body("Your cant this project");
        }
        Project project = projectRepository.findById(idexit).orElseThrow();

        State state = getOrCreateState(projectDto.getStateDto().getName());
        project.setName(projectDto.getName());
        project.setDescription(projectDto.getDescription());
        project.setState(state);
        project.setDateDeliver(projectDto.getDateDeliver());
        project.setDateStart(projectDto.getDateStart());
        project.setPriority(projectDto.getPriority());
        project = projectRepository.save(project);

        List<UserProjectRol> actualUserProjectRol = userProjectRolRepository.findByProject(project);
        Set<String> emailsActual = actualUserProjectRol.stream().map(upr -> upr.getUsers().getEmail()).collect(Collectors.toSet());

        //emails news
        Set<String> emailsNew = projectDto.getUserDtos().stream().map(UserDtoResponse::getEmail).collect(Collectors.toSet());

        Set<String> emailsForEliminated = new HashSet<>(emailsActual);
        emailsForEliminated.removeAll(emailsNew);

        Set<String> emailsForAdd = new HashSet<>(emailsNew);
        emailsForAdd.removeAll(emailsActual);

        //deleted users in the project
        for (String email : emailsForEliminated) {
            Users users = userRepository.findByEmail(email).orElseThrow();
            userProjectRolRepository.deleteByUsersAndProject(users, project);
        }

        // add users
        for (String email : emailsForAdd) {
            Users users = userRepository.findByEmail(email).orElseThrow();
            Optional<Rol> rolOptional= users.getUserProjectRols().stream()
                    .map(UserProjectRol::getRol)
                    .filter(rol -> !"ROLE_ADMIN".equals(rol.getName()))
                    .findFirst();

            if (rolOptional.isPresent()) {
                UserProjectRol nuevo = new UserProjectRol();
                nuevo.setUsers(users);
                nuevo.setRol(rolOptional.get());
                nuevo.setProject(project);
                userProjectRolRepository.save(nuevo);
            }
        }

        return ResponseEntity.ok().body("se edito el project");
    }

    @Transactional
    public ResponseEntity<?> deleteproject (Integer id) {
        try{

            Project project = projectRepository.findById(id).orElseThrow(() -> new RuntimeException("Not exits this project"));

            userProjectRolRepository.deleteByProjectId(project.getId());

            projectRepository.deleteById(id);

            return ResponseEntity.ok("delete project correct");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    private Users getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    private Rol getUserValidRol(Users user) {
        return user.getUserProjectRols().stream()
                .map(UserProjectRol::getRol)
                .filter(role -> !"ROLE_ADMIN".equals(role.getName()))
                .findFirst()
                .orElse(null);
    }

    private State getOrCreateState(String stateName) {
        return stateRepository.findByName(stateName).orElseGet(() -> {
            State newState = new State();
            newState.setName(stateName);
            return stateRepository.save(newState);
        });
    }
    private Project createAndSaveProject(ProjectDtoResponse dto, State state) {
        Project project = new Project();
        project.setName(dto.getName());
        project.setDescription(dto.getDescription());
        project.setDateStart(dto.getDateStart());
        project.setDateDeliver(dto.getDateDeliver());
        project.setPriority(dto.getPriority());
        project.setState(state);
        return projectRepository.save(project);
    }

    private void createUserProjectRelation(Users user, Rol rol, Project project) {
        UserProjectRol userProjectRol = new UserProjectRol();
        userProjectRol.setUsers(user);
        userProjectRol.setRol(rol);
        userProjectRol.setProject(project);
        userProjectRolRepository.save(userProjectRol);
    }

}
