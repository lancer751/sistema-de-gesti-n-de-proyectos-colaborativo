package com.gestionproyectoscolaborativos.backend.services;

import com.gestionproyectoscolaborativos.backend.entitys.Rol;
import com.gestionproyectoscolaborativos.backend.entitys.Users;
import com.gestionproyectoscolaborativos.backend.repository.UserProjectRolRepository;
import com.gestionproyectoscolaborativos.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class JpaUserDetailsServices implements UserDetailsService {
    @Autowired
    private UserRepository repository;
    @Autowired
    private UserProjectRolRepository userProjectRolRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Users> users = repository.findByEmail(username);

        List<Rol> rols =  userProjectRolRepository.findRolesByUser(users.get());
        if(users.isEmpty()){
            throw new UsernameNotFoundException(String.format("username %s no existe en el sistema!", username ));
        }
        Users users1 = users.orElseThrow();

        // nos exige userdetails, para optener todos los roles
        List<GrantedAuthority> authorities = rols.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(users1.getEmail(),
                users1.getPassword(),
                users1.isEnable(),
                true,
                true,
                true,
                authorities);
    }
}
