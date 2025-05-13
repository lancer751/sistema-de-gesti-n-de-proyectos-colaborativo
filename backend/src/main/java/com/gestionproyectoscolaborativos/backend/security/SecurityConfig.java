package com.gestionproyectoscolaborativos.backend.security;

import com.gestionproyectoscolaborativos.backend.security.filter.JwtAuthenticacionFilter;
import com.gestionproyectoscolaborativos.backend.security.filter.JwtValidationFilter;
import com.gestionproyectoscolaborativos.backend.services.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
public class SecurityConfig {

    @Autowired
    private AuthenticationConfiguration authenticationConfiguration;
    @Lazy
    @Autowired
    private  UserServices userServices;


    @Bean
    AuthenticationManager authenticationManager () throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // para poder inyectar la dependencia
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //darle seguridad al endpoint
    @Bean
    SecurityFilterChain filterChain (HttpSecurity http) throws Exception{
        return http.authorizeHttpRequests((authz) -> authz
                .requestMatchers(HttpMethod.POST, "/dashboardadmin/registeruser").hasAnyRole("ADMIN", "LIDERSISTEMAS", "LIDERSOFTWARE")
                        .requestMatchers(HttpMethod.PUT, "/dashboardadmin/edituser/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/dashboardadmin/project/restore").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/project/add").hasAnyRole("ADMIN", "LIDERSISTEMAS", "LIDERSOFTWARE")
                        .requestMatchers(HttpMethod.PUT, "/project/edit/{id}").hasAnyRole("ADMIN", "LIDERSISTEMAS", "LIDERSOFTWARE")
                        .requestMatchers(HttpMethod.DELETE, "/project/delete/{id}").hasAnyRole("ADMIN", "LIDERSISTEMAS", "LIDERSOFTWARE")
                        .requestMatchers(HttpMethod.GET, "/dashboardadmin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/project/").authenticated()
                        .requestMatchers(HttpMethod.POST, "/dashboard/logout").authenticated()
                        .requestMatchers(HttpMethod.GET, "/dashboard/validation").permitAll()
                        .anyRequest().authenticated())

                .addFilter(new JwtAuthenticacionFilter(authenticationConfiguration.getAuthenticationManager(), userServices))
                .addFilter(new JwtValidationFilter(authenticationConfiguration.getAuthenticationManager()))
                .csrf(config -> config.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(managment -> managment.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }
    @Bean
    CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration config= new CorsConfiguration();
        config.setAllowedOriginPatterns(Arrays.asList("*"));
        config.setAllowedMethods(Arrays.asList("GET","POST","DELETE","PUT"));
        config.setAllowedHeaders(Arrays.asList("Authorization","Content-type"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
    @Bean
    FilterRegistrationBean<CorsFilter> corsFilter () {
        FilterRegistrationBean<CorsFilter> corsBean = new FilterRegistrationBean<>(new CorsFilter(corsConfigurationSource()));

        corsBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return corsBean;
    }
}
