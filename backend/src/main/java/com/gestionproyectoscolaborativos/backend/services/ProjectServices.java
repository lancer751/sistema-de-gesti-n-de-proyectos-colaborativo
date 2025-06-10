package com.gestionproyectoscolaborativos.backend.services;

import com.gestionproyectoscolaborativos.backend.entitys.Project;
import com.gestionproyectoscolaborativos.backend.entitys.Rol;
import com.gestionproyectoscolaborativos.backend.entitys.State;
import com.gestionproyectoscolaborativos.backend.entitys.Users;
import com.gestionproyectoscolaborativos.backend.entitys.tablesintermedate.UserProject;
import com.gestionproyectoscolaborativos.backend.entitys.tablesintermedate.UserProjectRol;
import com.gestionproyectoscolaborativos.backend.repository.*;
import com.gestionproyectoscolaborativos.backend.services.dto.request.ProjectDto;
import com.gestionproyectoscolaborativos.backend.services.dto.request.RolDto;
import com.gestionproyectoscolaborativos.backend.services.dto.response.projects.ProjectDtoResponse;
import com.gestionproyectoscolaborativos.backend.services.dto.request.StateDto;
import com.gestionproyectoscolaborativos.backend.services.dto.response.UserDtoResponse;
import com.gestionproyectoscolaborativos.backend.services.dto.response.projects.UserRolProjectRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static org.apache.logging.log4j.util.Strings.isNotBlank;

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

    @Autowired
    private UserProjectRepository userProjectRepository;



    @Transactional(readOnly = true)
    public ResponseEntity<?> readprojet () {
        Users users = getAuthenticatedUser();
        List<UserProject> userProjectRolList = userProjectRepository.findByUsers(users);
        List<ProjectDto> projectDtos = userProjectRolList.stream()
                .map(p -> {
                    Project project = p.getProject();
                    // Lista de usuarios del proyecto
                    List<UserRolProjectRequest> userDtos = userProjectRepository.findByProject(p.getProject()).stream().map(u -> {
                        UserRolProjectRequest userRolProjectRequest = new UserRolProjectRequest();
                        userRolProjectRequest.setId(u.getUsers().getId());
                        userRolProjectRequest.setEmail(u.getUsers().getEmail());
                        userRolProjectRequest.setRolProject(u.getRolproject());

                        return userRolProjectRequest;
                    }).collect(Collectors.toList());

                    // Armar DTO del proyecto
                    ProjectDto projectDto = new ProjectDto();
                    projectDto.setId(projectDto.getId());
                    projectDto.setName(project.getName());
                    projectDto.setCreatedBy(project.getCreatedBy());
                    projectDto.setActive(project.isActive());
                    projectDto.setPriority(project.getPriority());
                    projectDto.setDescription(project.getDescription());
                    projectDto.setDateStart(project.getDateStart());
                    projectDto.setDateDeliver(project.getDateDeliver());
                    projectDto.setStateDto(new StateDto(project.getState().getName()));
                    projectDto.setUserRolProjectRequestList(userDtos);
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



        // Crear o usar estado
        State state = getOrCreateState(projectDto.getStateDto().getName());

        // Crear el proyecto
        Project project = createAndSaveProject(projectDto, state, user);

        if (projectDto.getUserLider() != null) {
            for (UserRolProjectRequest userDto : projectDto.getUserLider()) {
                userRepository.findById(userDto.getId()).ifPresent(extraUser -> {
                    createUserProjectRelation(extraUser, userDto.getRolProject(), project);
                });
            }
        }

        // Asociar a otros usuarios (si vienen en la lista)
        if (projectDto.getUserRolProjectRequestList() != null) {
            for (UserRolProjectRequest userDto : projectDto.getUserRolProjectRequestList()) {
                userRepository.findById(userDto.getId()).ifPresent(extraUser -> {
                        createUserProjectRelation(extraUser, userDto.getRolProject(), project);
                });
            }
        }
        return ResponseEntity.ok("project created successful");
    }


    @Transactional
    public ResponseEntity<?> editproject(Integer id, ProjectDtoResponse projectDto) {
        Users currentUser = getAuthenticatedUser();

        // Buscar el proyecto
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado con ID: " + id));

        // Actualizar campos del proyecto
        updateProjectFields(project, projectDto);

        // Guardar cambios del proyecto
        project = projectRepository.save(project);

        // Obtener todos los usuarios actuales asignados
        List<UserProject> currentUserProjects = userProjectRepository.findByProject(project);
        Set<Integer> currentUserIds = currentUserProjects.stream()
                .map(up -> up.getUsers().getId())
                .collect(Collectors.toSet());

        // Obtener todos los nuevos usuarios (colaboradores y líderes)
        List<UserRolProjectRequest> totalUsersDto = new ArrayList<>();
        if (projectDto.getUserRolProjectRequestList() != null)
            totalUsersDto.addAll(projectDto.getUserRolProjectRequestList());
        if (projectDto.getUserLider() != null)
            totalUsersDto.addAll(projectDto.getUserLider());

        Set<Integer> newUserIds = totalUsersDto.stream()
                .map(UserRolProjectRequest::getId)
                .collect(Collectors.toSet());

        // Calcular usuarios a eliminar y a agregar
        Set<Integer> userIdsToRemove = new HashSet<>(currentUserIds);
        userIdsToRemove.removeAll(newUserIds);

        Set<Integer> userIdsToAdd = new HashSet<>(newUserIds);
        userIdsToAdd.removeAll(currentUserIds);

        // Eliminar relaciones obsoletas
        for (Integer userId : userIdsToRemove) {
            Users user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + userId));
            userProjectRepository.deleteByUsersAndProject(user, project);
        }

        // Agregar nuevas relaciones
        for (UserRolProjectRequest request : totalUsersDto) {
            if (userIdsToAdd.contains(request.getId())) {
                Users user = userRepository.findById(request.getId())
                        .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + request.getId()));

                UserProject newUserProject = new UserProject();
                newUserProject.setUsers(user);
                newUserProject.setProject(project);
                newUserProject.setRolproject(request.getRolProject());
                userProjectRepository.save(newUserProject);
            }
        }

        return ResponseEntity.ok("Proyecto editado correctamente.");
    }


    @Transactional
    public ResponseEntity<?> deleteproject (Integer id) {
        try{

            Project project = projectRepository.findById(id).orElseThrow(() -> new RuntimeException("Not exits this project"));

            userProjectRepository.deleteByProjectId(project.getId());

            projectRepository.deleteById(id);
            Map<String, String> json = new HashMap<>();
            json.put("message", "delete project correct");
            return ResponseEntity.ok(json);
        } catch (Exception e) {
            Map<String, String> json = new HashMap<>();
            json.put("message", "error " + e.getMessage());
            return ResponseEntity.badRequest().body(json);
        }

    }

    private Users getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    private Rol getUserValidRol(Users user) {
        System.out.println(user.getUserProjectRols().get(0).getRol().getName());
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
    private Project createAndSaveProject(ProjectDtoResponse dto, State state, Users users) {
        Project project = new Project();
        project.setName(dto.getName());
        project.setDescription(dto.getDescription());
        project.setDateStart(dto.getDateStart());
        project.setCreatedBy(users.getName() + " " + users.getLastname());
        project.setDateDeliver(dto.getDateDeliver());
        project.setPriority(dto.getPriority());
        project.setActive(dto.isActive());
        project.setState(state);
        return projectRepository.save(project);
    }

    private void createUserProjectRelation(Users user, String rol, Project project) {
        UserProject userProjectRol = new UserProject();
        userProjectRol.setUsers(user);
        userProjectRol.setRolproject(rol);
        userProjectRol.setProject(project);
        userProjectRepository.save(userProjectRol);
    }
    private void updateProjectFields(Project project, ProjectDtoResponse dto) {
        if (isNotBlank(dto.getName())) project.setName(dto.getName());
        if (isNotBlank(dto.getDescription())) project.setDescription(dto.getDescription());
        if (dto.getDateDeliver() != null) project.setDateDeliver(dto.getDateDeliver());
        if (dto.getDateStart() != null) project.setDateStart(dto.getDateStart());
        if (dto.getPriority() != null) project.setPriority(dto.getPriority());
        if (dto.getCreatedBy() != null) project.setCreatedBy(dto.getCreatedBy());
        project.setActive(dto.isActive());

        if (dto.getStateDto() != null && isNotBlank(dto.getStateDto().getName())) {
            State state = getOrCreateState(dto.getStateDto().getName());
            project.setState(state);
        }
    }

    public ResponseEntity<?>  findProjectState (String state) {
        try {
            State state1 = stateRepository.findByName(state).orElseThrow();
            List<ProjectDto> projectDtos = projectRepository.findByState(state1).stream().map(p -> {
                List<UserRolProjectRequest> userLider = p.getUserProjects().stream().filter(u -> u.getRolproject().equals("Lider")).map(u -> {
                    UserRolProjectRequest userRolProjectRequest = new UserRolProjectRequest();
                    userRolProjectRequest.setId(Math.toIntExact(u.getUsers().getId()));
                    userRolProjectRequest.setName(u.getUsers().getName());
                    userRolProjectRequest.setLastname(u.getUsers().getLastname());
                    userRolProjectRequest.setEmail(u.getUsers().getEmail());
                    userRolProjectRequest.setRolProject(u.getRolproject());
                    return  userRolProjectRequest;
                }).toList();
                List<UserRolProjectRequest> userColaboreitor = userProjectRepository.findByProject(p).stream().filter(pr -> !Objects.equals(pr.getRolproject(), "Lider")).map(u -> {
                    UserRolProjectRequest dto = new UserRolProjectRequest();
                    dto.setId(u.getUsers().getId());
                    dto.setEmail(u.getUsers().getEmail());
                    dto.setName(u.getUsers().getName());
                    dto.setLastname(u.getUsers().getLastname());
                    dto.setNumberPhone(u.getUsers().getNumberPhone());
                    dto.setDescription(u.getUsers().getDescription());
                    dto.setRolProject(u.getRolproject());
                    return dto;
                }).collect(Collectors.toList());
                StateDto stateDto = new StateDto(state1.getName());
                ProjectDto projectDto = new ProjectDto();
                projectDto.setId(p.getId());
                projectDto.setName(p.getName());
                projectDto.setDescription(p.getDescription());
                projectDto.setPriority(p.getPriority());
                projectDto.setDateStart(p.getDateStart());
                projectDto.setDateDeliver(p.getDateDeliver());
                projectDto.setCreatedBy(p.getCreatedBy());
                projectDto.setUserLiders(userLider);
                projectDto.setStateDto(stateDto);
                projectDto.setUserRolProjectRequestList(userColaboreitor);
                return projectDto;
            }).toList();

            return ResponseEntity.ok().body(projectDtos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("hay un error " + e.getMessage());
        }
    }

}
