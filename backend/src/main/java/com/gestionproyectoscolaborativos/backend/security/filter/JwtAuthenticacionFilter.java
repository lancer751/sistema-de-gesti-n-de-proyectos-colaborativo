package com.gestionproyectoscolaborativos.backend.security.filter;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestionproyectoscolaborativos.backend.entitys.Users;
import com.gestionproyectoscolaborativos.backend.repository.UserRepository;
import com.gestionproyectoscolaborativos.backend.services.UserServices;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.gestionproyectoscolaborativos.backend.security.TokenJwtConfig.*;

public class JwtAuthenticacionFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;
    private UserServices userServices;
    public JwtAuthenticacionFilter(AuthenticationManager authenticationManager, UserServices userServices){
        this.authenticationManager = authenticationManager;
        this.userServices = userServices;

    }

    //logeo
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        Users users = null;
        String email = null;
        String password = null;
        try {
            users = new ObjectMapper().readValue(request.getInputStream(), Users.class);
            email = users.getEmail();
            password = users.getPassword();
        } catch (StreamReadException e) {
            e.printStackTrace();
        } catch (DatabindException e) {
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);
        return authenticationManager.authenticate(authenticationToken);
    }

    //logeo correcto
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) authResult.getPrincipal();

        String email = user.getUsername();

        Collection<? extends GrantedAuthority> roles = authResult.getAuthorities();
        Claims claims = Jwts.claims()
                .add("authorities", new ObjectMapper().writeValueAsString(roles))
                        .add("username", email)
                                .build();

        System.out.println(user.getUsername()); // trae el username

        //token access
        String token = Jwts.builder()
                .subject(email)
                .claims(claims)
                .expiration(new Date(System.currentTimeMillis() + 3600000))
                .issuedAt(new Date())
                .signWith(SECRET_KEY)
                .compact(); // token generado

        //refreshToken
        String refreshToken = generateRefreshToken(user);

        // cookie token
       /* Cookie firtsToken = new Cookie("token-jwt", token);
        firtsToken.setHttpOnly(true);
        firtsToken.setSecure(true); // impide q js
        firtsToken.setPath("/");
        firtsToken.setMaxAge(60 * 60);
        // 1 hora
        response.addCookie(firtsToken);*/
        // Agrega SameSite manualmente:
        response.setHeader("Set-Cookie",
                "token-jwt=" + token + "; HttpOnly; Secure; SameSite=None; Path=/; Max-Age=86400");


        System.out.println("Refresh token = " + refreshToken);
       /* Cookie cookieRefresh = new Cookie("refresh-token", refreshToken);
        cookieRefresh.setHttpOnly(false); // impide q JS acceda a la cookie
        cookieRefresh.setSecure(false);
        cookieRefresh.setPath("/"); // para todo el backend
        cookieRefresh.setMaxAge(86400); // 1 hora

        response.addCookie(cookieRefresh);*/
        // Cookie del refresh token
        response.addHeader("Set-Cookie",
                String.format("refresh-token=%s; Max-Age=86400; Path=/; HttpOnly; Secure; SameSite=None", refreshToken)
        );


        //response.addHeader(HEADER_AUTHORIZATION, PREFIX_TOKEN + token);
        Map<String, Object> body = new HashMap<>();
        body.put("user", userServices.userByEmail(email));
        body.put("message", String.format("Hola %s has iniciado sesion con exito! ", email));
        response.getWriter().write(new ObjectMapper().writeValueAsString(body));
        response.setContentType(CONTENT_TYPE);
        response.setStatus(200);
    }

    //logeo incorrecto
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        Map<String, String> body = new HashMap<>();
        body.put("message", "Error en la autenticacion email o password incorrectos");
        body.put("error", failed.getMessage());
        response.getWriter().write(new ObjectMapper().writeValueAsString(body));
        response.setStatus(401);
        response.setContentType(CONTENT_TYPE);
    }

    //refresh token
    public String generateRefreshToken(UserDetails userDetails) {
        return Jwts.builder().subject(userDetails.getUsername())
                .claim("type", "refresh")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 86400000 ))
                .signWith(SECRET_KEY)
                .compact();
    }
}
