package com.gestionproyectoscolaborativos.backend.services.auth;

import com.gestionproyectoscolaborativos.backend.entitys.Users;
import com.gestionproyectoscolaborativos.backend.repository.UserRepository;
import com.gestionproyectoscolaborativos.backend.services.dto.request.RolDto;
import com.gestionproyectoscolaborativos.backend.services.dto.response.UserDtoResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthenticationServices {
    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<?> validation () {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Users  users = userRepository.findByEmail(authentication.getName()).orElseThrow();

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

            HashMap<String, Object> json = new HashMap<>();
            json.put("message", "correct access");
            json.put("user", userDtoResponse);
            return ResponseEntity.ok().body(json);
        }catch (Exception e) {
            HashMap<String, String> json = new HashMap<>();
            json.put("message", "no estas autenticado");
            return ResponseEntity.badRequest().body(json);
        }

    }


    public ResponseEntity<?> logout (HttpServletResponse response) {
        try {
            // Eliminar token-jwt
            Cookie jwtCookie = new Cookie("token-jwt", null);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setSecure(true);
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge(0); // Elimina la cookie

            // Eliminar refresh-token
            Cookie refreshCookie = new Cookie("refresh-token", null);
            refreshCookie.setHttpOnly(true);
            refreshCookie.setSecure(true);
            refreshCookie.setPath("/");
            refreshCookie.setMaxAge(0); // Elimina la cookie

            response.addCookie(jwtCookie);
            response.addCookie(refreshCookie);

            return ResponseEntity.ok().body(Map.of("message", "Logout exitoso"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Hay un error " + e.getMessage());
        }

    }
}
