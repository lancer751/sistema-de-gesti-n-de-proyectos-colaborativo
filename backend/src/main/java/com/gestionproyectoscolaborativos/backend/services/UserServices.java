package com.gestionproyectoscolaborativos.backend.services;

import com.gestionproyectoscolaborativos.backend.entitys.Rol;
import com.gestionproyectoscolaborativos.backend.entitys.Users;
import com.gestionproyectoscolaborativos.backend.entitys.histories.UserProjectRoleHistory;
import com.gestionproyectoscolaborativos.backend.entitys.tablesintermedate.UserProjectRol;
import com.gestionproyectoscolaborativos.backend.repository.*;
import com.gestionproyectoscolaborativos.backend.services.dto.request.RolDto;
import com.gestionproyectoscolaborativos.backend.services.dto.request.UserDto;
import com.gestionproyectoscolaborativos.backend.services.dto.response.UserDtoResponse;
import com.gestionproyectoscolaborativos.backend.services.mail.impl.IEmailServices;
import com.gestionproyectoscolaborativos.backend.services.mail.model.EmailDto;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServices {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private IEmailServices emailServices;
    @Autowired
    private UserProjectRoleHistoryRepository userProjectRoleHistoryRepository;

    @Autowired
    private UserProjectRolRepository userProjectRolRepository;
    // lo inyecta solo caundo se va usar

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public ResponseEntity<?> save (UserDto userDto) {
        try {

            String password = passwordEncoder.encode(userDto.getPassword());
            Users user = new Users();
            user.setName(userDto.getName());
            user.setLastname(userDto.getLastname());
            user.setPassword(password);
            user.setDescription(userDto.getDescription());
            user.setNumberPhone(userDto.getNumberPhone());
            user.setEntryDate(userDto.getEntryDate());
            List<RolDto> rolDtoList = new ArrayList<>();
            List<UserProjectRol> userProjectRolList = new ArrayList<>();

            UserProjectRol userProjectRol = new UserProjectRol();
            userDto.getRolDtoList().forEach(e -> rolDtoList.add(e));

            for (RolDto r : rolDtoList) {
                Optional<Rol> rol = rolRepository.findByName(r.getName());
                if (!rol.isPresent()) {
                    UserProjectRol userProjectRolNew = new UserProjectRol();
                    Rol rol1 = new Rol();
                    rol1.setName(r.getName());
                    rolRepository.save(rol1);

                    userProjectRolNew.setRol(rol1);
                    userProjectRolNew.setUsers(user);
                    userProjectRolNew.setProject(null);
                    userProjectRolList.add(userProjectRolNew);

                }else{
                    UserProjectRol userProjectRolNew = new UserProjectRol();
                    userProjectRolNew.setRol(rol.get());
                    userProjectRolNew.setUsers(user);
                    userProjectRolNew.setProject(null);

                    userProjectRolList.add(userProjectRolNew);
                }
            }
            if (existsByUsername(userDto.getEmail())){
                return ResponseEntity.badRequest().body("EL gmail ya exites, el email no se puede repetir entre usuarios.");
            }
            user.setEmail(userDto.getEmail());
            user.setUserProjectRols(userProjectRolList);
            user.setEnable(true);
            user.setActivities(null);
            userRepository.save(user);
            userProjectRolList.forEach(userProjectRolRepository::save);

            emailSender(userDto);
            return ResponseEntity.ok().body("user created");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("hubo un error " + e.getMessage());
        }

    }

    @Transactional(readOnly = true)
    public  ResponseEntity<?> read (Pageable pageable, String sortBy, String sortDir, String enable, String role, String search) {
        Page<Users> usersPage;
        if (!search.isBlank()) {
            if (!role.isBlank() && (enable.equals("true") || enable.equals("false"))) {
                    boolean enabled = Boolean.parseBoolean(enable);
                    usersPage = userRepository.findByNameContainingIgnoreCaseAndEnableAndUserProjectRols_Rol_NameIgnoreCase(
                            search, enabled,  role, pageable);
            } else if (!role.isBlank()) {

                    usersPage = userRepository.findByNameContainingIgnoreCaseAndUserProjectRols_Rol_NameIgnoreCase(
                            search,  role, pageable);
            } else if (enable.equals("true") || enable.equals("false")) {

                    usersPage = userRepository.findByNameContainingIgnoreCaseAndEnable(
                            search, Boolean.parseBoolean(enable), pageable);
            } else {
                    usersPage = userRepository.findByNameContainingIgnoreCase(search, pageable);
            }
        } else {
            if  (!role.isBlank() && (enable.equals("true") || enable.equals("false"))) {
                boolean enabled = Boolean.parseBoolean(enable);
                usersPage = userRepository.findDistinctByEnableAndUserProjectRols_Rol_NameIgnoreCase(enabled,  role, pageable);
            } else if (!role.isBlank()) {
                usersPage = userRepository.findDistinctByUserProjectRols_Rol_NameIgnoreCase(role, pageable);
            } else if (enable.equals("true") || enable.equals("false")) {
                usersPage = userRepository.findByEnable(Boolean.parseBoolean(enable), pageable);
            } else {
                usersPage = userRepository.findAll(pageable); // Sin filtros
            }
        }


        List<UserDtoResponse> userDtoResponses = usersPage.getContent().stream().map(users -> {
            UserDtoResponse userDtoResponse = new UserDtoResponse();
            userDtoResponse.setId(users.getId());
            userDtoResponse.setName(users.getName());
            userDtoResponse.setLastname(users.getLastname());
            userDtoResponse.setEmail(users.getEmail());
            userDtoResponse.setNumberPhone(users.getNumberPhone());
            userDtoResponse.setDescription(users.getDescription());
            userDtoResponse.setActive(users.isEnable());
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

            return  userDtoResponse;
        }).collect(Collectors.toList());


        // 👇 Aquí haces la ordenación por rol si corresponde
        if ("rol".equalsIgnoreCase(sortBy)) {
            Comparator<UserDtoResponse> comparator = Comparator.comparing(user ->
                    user.getRolDtoList().isEmpty() ? "" : user.getRolDtoList().get(0).getName()
            );

            if ("desc".equalsIgnoreCase(sortDir)) {
                comparator = comparator.reversed();
            }

            userDtoResponses.sort(comparator);
        }
        Map<String, Object> response = new HashMap<>();
        response.put("users", userDtoResponses);
        response.put("currentPage", usersPage.getNumber());
        response.put("totalItems", usersPage.getTotalElements());
        response.put("totalPages", usersPage.getTotalPages());
        return ResponseEntity.ok().body(response);
    }

    // one user
    @Transactional
    public ResponseEntity<?> edit (Integer id, UserDto userDto){
        Map<String, String> json = new HashMap<>();

        return userRepository.findById(id).map(user -> {
            if (!user.getEmail().equals(userDto.getEmail())){
                if (existsByUsername(userDto.getEmail())){
                    json.put("message", "EL gmail ya exites, el email no se puede repetir entre usuarios.");
                    return ResponseEntity.badRequest().body(json);
                }
            }
            updateUserFields(user, userDto);
            user.setEnable(userDto.isEnable()); // activo
            userRepository.save(user);

            json.put("message", "user edit");
            return ResponseEntity.ok().body(json);
        }).orElseGet( () -> {
            json.put("message", "User with ID: " + id + " not exist");
            return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(json);
        });
    }

    
    private void updateUserFields(Users user, UserDto dto) {
        if (isNotBlank(dto.getName())) user.setName(dto.getName());
        if (isNotBlank(dto.getLastname())) user.setLastname(dto.getLastname());
        if (isNotBlank(dto.getNumberPhone())) user.setNumberPhone(dto.getNumberPhone());
        if (dto.getEntryDate() != null) user.setEntryDate(dto.getEntryDate());
        if (isNotBlank(dto.getEmail())) user.setEmail(dto.getEmail());
        if (isNotBlank(dto.getDescription())) user.setDescription(dto.getDescription());
        if (isNotBlank(dto.getPassword())) user.setPassword(passwordEncoder.encode(dto.getPassword())); // ⚠️ Idealmente encriptar aquí
        if (!dto.getRolDtoList().isEmpty())  editUserRol(user, dto);
    }
    private void editUserRol (Users user, UserDto dto) {
        // 1. Obtener roles actuales del usuario
        List<UserProjectRol> rolesActuales = userProjectRolRepository.findByUsers(user);

        // 2. Crear un Set con los nombres de roles nuevos desde el DTO
        Set<String> nuevosRolesNombres = dto.getRolDtoList().stream()
                .map(RolDto::getName)
                .collect(Collectors.toSet());

        // 3. Eliminar roles que ya no están en el DTO
        for (UserProjectRol upr : rolesActuales) {// solo si no está ligado a un proyecto
            String nombreRolActual = upr.getRol().getName();

            if (!nuevosRolesNombres.contains(nombreRolActual)) {
                if (upr.getProject() != null && upr.getRol().getName() != null && (upr.getRol().getName().startsWith("ROLE_LIDER") || upr.getRol().getName().startsWith("ROLE_ADMIN"))) {
                    UserProjectRoleHistory userProjectRoleHistory = getUserProjectRoleHistory(upr);
                    userProjectRoleHistoryRepository.save(userProjectRoleHistory);
                }

                userProjectRolRepository.delete(upr);
            }

        }
        // 4. Agregar roles nuevos que aún no tiene
        Set<String> rolesActualesNombres = rolesActuales.stream()
                .map(upr -> upr.getRol().getName())
                .collect(Collectors.toSet());

        for (String nombreNuevoRol : nuevosRolesNombres) {
            if (!rolesActualesNombres.contains(nombreNuevoRol)) {
                Rol rol = rolRepository.findByName(nombreNuevoRol)
                        .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + nombreNuevoRol));
                UserProjectRol nuevo = new UserProjectRol();
                nuevo.setUsers(user);
                nuevo.setRol(rol);
                userProjectRolRepository.save(nuevo);
            }
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

    private boolean isNotBlank(String value) {
        return value != null && !value.isBlank();
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByEmail(username);
    }

    public UserDtoResponse userByEmail (String email) {
        Users users = userRepository.findByEmail(email).orElseThrow();

        UserDtoResponse userDtoResponse = new UserDtoResponse();
        userDtoResponse.setId(users.getId());
        userDtoResponse.setName(users.getName());
        userDtoResponse.setLastname(users.getLastname());
        userDtoResponse.setEmail(users.getEmail());
        userDtoResponse.setDescription(users.getDescription());
        userDtoResponse.setEntryDate(users.getEntryDate());
        userDtoResponse.setNumberPhone(users.getNumberPhone());
        userDtoResponse.setActive(users.isEnable());
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
        return  userDtoResponse;
    }

    private void emailSender (UserDto userDto) throws MessagingException {
        Map<String, String> message = new HashMap<>();
        message.put("name", userDto.getName() + " " + userDto.getLastname());
        message.put("username", userDto.getEmail());
        message.put("password", userDto.getPassword());
        EmailDto emailDto = new EmailDto();
        emailDto.setTo(userDto.getEmail());
        emailDto.setSubject("Bienvenido a mescob ");
        emailDto.setMessage(message);

        emailServices.sendMail(emailDto);
    }
}
