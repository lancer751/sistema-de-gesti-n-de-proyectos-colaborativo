package com.gestionproyectoscolaborativos.backend.auth;

import com.gestionproyectoscolaborativos.backend.services.UserServices;
import com.gestionproyectoscolaborativos.backend.services.auth.AuthenticationServices;
import com.gestionproyectoscolaborativos.backend.services.dto.request.UserDto;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/dashboard")
public class Login {

    @Autowired
    private AuthenticationServices authenticationServices;

    @GetMapping("/validation")
    private ResponseEntity<?> validation () {
        return authenticationServices.validation();
    }
    @PostMapping("/logout")
    private ResponseEntity<?> logout (HttpServletResponse response) {
        return authenticationServices.logout(response);
    }
}
