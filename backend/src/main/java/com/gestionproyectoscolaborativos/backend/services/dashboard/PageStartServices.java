package com.gestionproyectoscolaborativos.backend.services.dashboard;

import com.gestionproyectoscolaborativos.backend.entitys.Activity;
import com.gestionproyectoscolaborativos.backend.entitys.Project;
import com.gestionproyectoscolaborativos.backend.entitys.Users;
import com.gestionproyectoscolaborativos.backend.repository.ActivityRepository;
import com.gestionproyectoscolaborativos.backend.repository.ProjectRepository;
import com.gestionproyectoscolaborativos.backend.repository.UserRepository;
import com.gestionproyectoscolaborativos.backend.services.dto.request.RolDto;
import com.gestionproyectoscolaborativos.backend.services.dto.response.UserDtoResponse;
import com.gestionproyectoscolaborativos.backend.services.dto.response.dashboard.KpisClave;
import com.gestionproyectoscolaborativos.backend.services.dto.response.dashboard.UserAuthenticateDto;
import com.gestionproyectoscolaborativos.backend.services.dto.response.dashboard.UserCountActivityDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@Service
public class PageStartServices {
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private ActivityRepository activityRepository;
    @Autowired
    private UserRepository userRepository;

    @Transactional(readOnly = true)
    public ResponseEntity<?> userAuthenticate () {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Users users = userRepository.findByEmail(authentication.getName()).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            UserDtoResponse userDtoResponse = new UserDtoResponse();
            userDtoResponse.setName(users.getName());
            userDtoResponse.setLastname(users.getLastname());
            userDtoResponse.setEmail(users.getEmail());
            userDtoResponse.setNumberPhone(users.getNumberPhone());
            userDtoResponse.setDescription(users.getDescription());
            Set<String> roleUnique = new HashSet<>();
            userDtoResponse.setRolDtoList(
                    users.getUserProjectRols().stream()
                            .map(rol -> rol.getRol().getName().replaceFirst("ROLE_",  ""))
                            .filter(roleUnique::add) // solo agrega si es nuevo
                            .map(nombre -> {
                                RolDto rolDto = new RolDto();
                                rolDto.setName(nombre);
                                return rolDto;
                            })
                            .collect(Collectors.toList())
            );
            return ResponseEntity.ok().body(userDtoResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("el error fue: " + e.getMessage());
        }
    }
    //filter by last 4 months
    @Transactional(readOnly = true)
    public ResponseEntity<?> readdatabarchart () {
        try {
            Map<String, Map<String,Long>> countMonths = new HashMap<>();
            LocalDateTime dateToday = LocalDateTime.now();

            List<Month> monthList = List.of(
                    dateToday.minusMonths(3).getMonth(),
                    dateToday.minusMonths(2).getMonth(),
                    dateToday.minusMonths(1).getMonth(),
                    dateToday.getMonth()
            );
            for (Month month : monthList) {
                Map<String, Long> stateCount = new HashMap<>();
                stateCount.put("Completado", projectRepository.findAll().stream()
                        .filter(p -> p.getDateStart().getMonth().equals(month) && p.getState().getName().equals("Completado"))
                        .count());
                stateCount.put("cancelado", projectRepository.findAll().stream()
                        .filter(p -> p.getDateStart().getMonth().equals(month) && p.getState().getName().equals("Cancelado"))
                        .count());
                countMonths.put(month.name(), stateCount);
            }
            return ResponseEntity.ok().body(countMonths);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("el error fue" + e.getMessage());
        }
    }
    // projects filter by states
    @Transactional(readOnly = true)
    public  ResponseEntity<?> readstatesbarchart () {
        try {
            Map<String, Long> statesCount = new HashMap<>();
            statesCount.put("Completados", projectRepository.findAll().stream().filter(project -> project.getState().getName().equals("Completado")).count());
            statesCount.put("En Riesgo", projectRepository.findAll().stream().filter(project -> project.getState().getName().equals("En Riesgo")).count());
            statesCount.put("En Pausa", projectRepository.findAll().stream().filter(project -> project.getState().getName().equals("En Pausa")).count());
            statesCount.put("En Curso", projectRepository.findAll().stream().filter(project -> project.getState().getName().equals("En Curso")).count());
            statesCount.put("Cancelado", projectRepository.findAll().stream().filter(project -> project.getState().getName().equals("Cancelado")).count());
            return ResponseEntity.ok().body(statesCount);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("el error fue " + e.getMessage());
        }
    }
    // tasks by states count
    @Transactional(readOnly = true)
    public ResponseEntity<?> readstatestasks () {
        try {
            Map<String, Long> statesCount = new HashMap<>();
            statesCount.put("En Revisión", activityRepository.findAll().stream().filter(activity -> activity.getState().getName().equals("En revision")).count());
            statesCount.put("Sin Empezar", activityRepository.findAll().stream().filter(activity -> activity.getState().getName().equals("Sin Empezar")).count());
            statesCount.put("En Curso", activityRepository.findAll().stream().filter(activity -> activity.getState().getName().equals("En Curso")).count());
            statesCount.put("Archivadas", activityRepository.findAll().stream().filter(activity -> activity.getState().getName().equals("Archivadas")).count());
            statesCount.put("Terminados", activityRepository.findAll().stream().filter(activity -> activity.getState().getName().equals("Terminados")).count());
            return ResponseEntity.ok().body(statesCount);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("el error fue" + e.getMessage());
        }
    }

    // activity count by users
    @Transactional(readOnly = true)
    public ResponseEntity<?> readactivitysbyusers () {
        try {
            List<UserCountActivityDto> userDtoList = userRepository.findAll().stream().map(users -> {
                UserCountActivityDto userDto = new UserCountActivityDto();
                userDto.setFullName(users.getName() + " " + users.getLastname());
                userDto.setTareasAsigandas(users.getActivities().stream().count());
                userDto.setTareasCompletadas(users.getActivities().stream().filter(activity -> activity.equals("Terminados")).count());
                return userDto;
            }).toList();
            return ResponseEntity.ok().body(userDtoList);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Hay un error " + e.getMessage());
        }
    }
    // kpis by project
    @Transactional(readOnly = true)
    public ResponseEntity<?> readkpisClave () {
        try {
            KpisClave kpisClave = new KpisClave();
            Map<LocalDateTime, LocalDateTime> dateStartFinish = new HashMap<>();
            Map<Long, Long> tasksminutesfinish = new HashMap<>();

            for (Project project : projectRepository.findAll()) {
                dateStartFinish.put(project.getDateStart(), project.getDateDeliver());
            }

            // minutes the star and finish task
            for (Activity activity : activityRepository.findAll()) {
                tasksminutesfinish.put((long) activity.getDateStart().getMinute(),(long) activity.getDateDeliver().getMinute());
            }

            long totalWeeks = 0;
            long totalminutes = 0;

            if (!dateStartFinish.isEmpty()) {
                for (Map.Entry<LocalDateTime, LocalDateTime> entry : dateStartFinish.entrySet()) {
                    LocalDateTime star = entry.getKey();
                    LocalDateTime end = entry.getValue();

                    Duration duration = Duration.between(star, end);
                    long days = duration.toDays();
                    long weeks = days / 7 ;

                    totalWeeks += weeks;
                }
                double averageWeeks = (double) totalWeeks / dateStartFinish.size();
                kpisClave.setAverageProjectFinish((long) averageWeeks);
            }else {
                kpisClave.setAverageProjectFinish(0);
            }

            if (!tasksminutesfinish.isEmpty()) {
                for (Map.Entry<Long, Long> minutesEntry : tasksminutesfinish.entrySet()) {
                    LongStream rangeMinute =  LongStream.rangeClosed(minutesEntry.getKey(), minutesEntry.getValue());
                    OptionalDouble total = rangeMinute.average();

                    if (total.isPresent()) {
                        kpisClave.setAverageTaskFinish((long) total.getAsDouble());
                    }else {
                        kpisClave.setAverageTaskFinish(0);
                    }

                }
            }
            kpisClave.setTotalProjectActive(projectRepository.findAll().stream().count());
            kpisClave.setTasksProgress(activityRepository.findAll().stream().filter(activity -> activity.getState().getName().equals("En progreso")).count());
            return ResponseEntity.ok().body(kpisClave );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("El error fue " + e.getMessage());
        }

    }
}
