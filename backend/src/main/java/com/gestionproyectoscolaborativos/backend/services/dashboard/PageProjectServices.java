package com.gestionproyectoscolaborativos.backend.services.dashboard;

import com.gestionproyectoscolaborativos.backend.entitys.Coment;
import com.gestionproyectoscolaborativos.backend.entitys.Project;
import com.gestionproyectoscolaborativos.backend.entitys.State;
import com.gestionproyectoscolaborativos.backend.entitys.enums.Priority;
import com.gestionproyectoscolaborativos.backend.repository.ComentRepository;
import com.gestionproyectoscolaborativos.backend.repository.ProjectRepository;
import com.gestionproyectoscolaborativos.backend.repository.StateRepository;
import com.gestionproyectoscolaborativos.backend.repository.UserProjectRepository;
import com.gestionproyectoscolaborativos.backend.services.dto.request.ProjectDto;
import com.gestionproyectoscolaborativos.backend.services.dto.request.StateDto;
import com.gestionproyectoscolaborativos.backend.services.dto.response.dashboard.StatePatch;
import com.gestionproyectoscolaborativos.backend.services.dto.response.projects.ComentsRecentResponseDto;
import com.gestionproyectoscolaborativos.backend.services.dto.response.projects.ProjectRecentResponseDto;
import com.gestionproyectoscolaborativos.backend.services.dto.response.projects.UserRolProjectRequest;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class PageProjectServices {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ComentRepository comentRepository;

    @Autowired
    private StateRepository stateRepository;
    @Autowired
    private UserProjectRepository userProjectRepository;

    // recently published projects
    public ResponseEntity<?> readprojectrecient () {
        try {
            List<ProjectRecentResponseDto> responseDtoList = projectRepository.findAll().stream()
                    .sorted(Comparator.comparing(
                            (Project p) -> p.getAuditFields().getCreatedAt()
                    ).reversed())
                    .limit(10)
                    .map(project -> {
                        ProjectRecentResponseDto dto = new ProjectRecentResponseDto();
                        dto.setId(project.getId());
                        dto.setTitle(project.getName());
                        return dto;
                    })
                    .toList();
            return ResponseEntity.ok().body(responseDtoList);
        } catch (Exception e) {
            Map<String, String> json = new HashMap<>();
            json.put("message", "El error fue " + e.getMessage());
            return ResponseEntity.badRequest().body(json);
        }
    }
    // validar a la fecha de hoy
    // y si el proyecto sigue activo
    public ResponseEntity<?> readnextdelivery () {
        try {
            LocalDateTime today = LocalDateTime.now();
            List<ProjectRecentResponseDto> responseDtoList = projectRepository.findAll().stream()
                    .filter(project -> project.getDateDeliver() != null && !project.getDateDeliver().isBefore(LocalDateTime.now()))
                    .sorted(Comparator.comparing(Project::getDateDeliver))
                    .limit(5)
                    .map(project -> {
                        ProjectRecentResponseDto dto = new ProjectRecentResponseDto();
                        dto.setId(project.getId());
                        dto.setTitle(project.getName());
                        long days = ChronoUnit.DAYS.between(today.toLocalDate(), project.getDateDeliver().toLocalDate());
                        if ( days == 0) {
                            dto.setTimeFinish("Finaliza hoy");
                        } else if (days == 1) {
                            dto.setTimeFinish("Finaliza en 1 dia");
                        }else if (days < 7) {
                            dto.setTimeFinish("Finaliza en " + days + " días");
                        }else {
                            long weeks = (long) Math.ceil(days / 7.0);
                            dto.setTimeFinish("Finaliza en " + weeks + (weeks == 1 ? " semana" : " semanas"));
                        }
                        return dto;
                    })
                    .toList();
            return ResponseEntity.ok().body(responseDtoList);
        } catch (Exception e) {
            Map<String, String> json = new HashMap<>();
            json.put("message", "El error fue " + e.getMessage());
            return ResponseEntity.badRequest().body(json);
        }
    }

    // recently published coments
    public ResponseEntity<?> readcomentrecient () {
        Map<String, String> json = new HashMap<>();
        try {
            List<ComentsRecentResponseDto> recentResponseDtos = comentRepository.findAll()
                    .stream()
                    .sorted(Comparator.comparing((Coment c) -> c.getAuditFields().getCreatedAt()).reversed())
                    .limit(5)
                    .map(c -> {
                        ComentsRecentResponseDto coment = new ComentsRecentResponseDto();
                        coment.setProjectId(c.getProject().getId());
                        coment.setAuthor(c.getUsers().getName() + " " + c.getUsers().getLastname());
                        coment.setTitleProject(c.getProject().getName());
                        coment.setLastTime(tiempoTranscurrido(c.getAuditFields().getCreatedAt()));
                        return coment;
                    })
                    .toList();
            return ResponseEntity.ok().body(recentResponseDtos);
        } catch (Exception e) {
            json.put("message", "error " + e.getMessage());
            return ResponseEntity.badRequest().body(json);
        }
    }

    public static String tiempoTranscurrido(LocalDateTime fechaEvento) {
        LocalDateTime ahora = LocalDateTime.now();
        Duration duracion = Duration.between(fechaEvento, ahora);

        long segundos = duracion.getSeconds();

        if (segundos < 60) return "Hace un momento";
        if (segundos < 3600) return "Hace " + (segundos / 60) + " minutos";
        if (segundos < 86400) return "Hace " + (segundos / 3600) + " horas";
        if (segundos < 172800) return "Hace 1 día";
        return "Hace " + (segundos / 86400) + " días";
    }


    public ResponseEntity<?> readProjectsAdmin(Pageable pageable,
                                               String search,
                                               Integer iduser,
                                               String state,
                                               Priority priority,
                                               @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date startp,
                                               @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date endp) {

        Specification<Project> spec = Specification.where(null);

        if (search != null && !search.isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("name")), "%" + search.toLowerCase() + "%"));
        }

        if (priority != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("priority"), priority));
        }

        if (startp != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("dateStart"), startp));
        }

        if (endp != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("dateDeliver"), endp));
        }

        if (state != null && !state.isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.join("state").get("name"), state));
        }

        if (iduser != null) {
            spec = spec.and((root, query, cb) -> {
                query.distinct(true); // ¡importante! para evitar duplicados
                Join<Object, Object> join = root.join("userProjects", JoinType.INNER);
                Predicate byUser = cb.equal(join.get("users").get("id"), iduser);
                Predicate byRole = cb.equal(join.get("rolproject"), "Lider");
                return cb.and(byUser, byRole);
            });
        }

        Page<Project> projectPage = projectRepository.findAll(spec, pageable);

        List<ProjectDto> projectDtos = projectPage.stream().map(p -> {
            List<UserRolProjectRequest> userDtos = userProjectRepository.findByProject(p).stream().filter(pr -> pr.getRolproject().equals("Lider")).map(u -> {
                UserRolProjectRequest dto = new UserRolProjectRequest();
                dto.setId(u.getUsers().getId());
                dto.setName(u.getUsers().getName());
                dto.setLastname(u.getUsers().getLastname());
                dto.setNumberPhone(u.getUsers().getNumberPhone());
                dto.setDescription(u.getUsers().getDescription());
                dto.setEmail(u.getUsers().getEmail());
                dto.setRolProject(u.getRolproject());
                return dto;
            }).collect(Collectors.toList());
            List<UserRolProjectRequest> userDtos2 = userProjectRepository.findByProject(p).stream().filter(pr -> !Objects.equals(pr.getRolproject(), "Lider")).map(u -> {
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
            ProjectDto dto = new ProjectDto();
            dto.setId(p.getId());
            dto.setName(p.getName());
            dto.setCreatedBy(p.getCreatedBy());
            dto.setUserLiders(userDtos);
            dto.setActive(p.isActive());
            dto.setPriority(p.getPriority());
            dto.setDescription(p.getDescription());
            dto.setDateStart(p.getDateStart());
            dto.setDateDeliver(p.getDateDeliver());
            dto.setStateDto(new StateDto(p.getState().getName()));
            dto.setUserRolProjectRequestList(userDtos2);
            return dto;
        }).collect(Collectors.toList());

        Map<String, Object> json = new HashMap<>();
        json.put("project", projectDtos);
        json.put("currentPage", projectPage.getNumber());
        json.put("totalItems", projectPage.getTotalElements());
        json.put("totalPages", projectPage.getTotalPages());
        return ResponseEntity.ok().body(json);
    }


    public ResponseEntity<?> projectseditlist(StatePatch statePatch) {
        List<Integer> ids = statePatch.getIdProjects();

        List<Project> projects = StreamSupport
                .stream(projectRepository.findAllById(ids).spliterator(), false)
                .collect(Collectors.toList());

        if (projects.size() != ids.size()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Uno o más proyectos no existen");
        }

        State state = stateRepository.findByName(statePatch.getStateName())
                .orElseThrow(() -> new RuntimeException("Estado no encontrado"));

        projects.forEach(project -> project.setState(state));

        projectRepository.saveAll(projects); // más eficiente: guarda todo de una

        Map<String, Object> json = new HashMap<>();
        json.put("messge", "edit correct");

        return ResponseEntity.ok(json);
    }

}
