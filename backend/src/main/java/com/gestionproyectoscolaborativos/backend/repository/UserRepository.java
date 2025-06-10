package com.gestionproyectoscolaborativos.backend.repository;

import com.gestionproyectoscolaborativos.backend.entitys.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Integer> {
    boolean existsByEmail (String email);

    @EntityGraph(attributePaths = {"userProjectRols", "userProjectRols.rol"}) // para que se cargue cuando realmente se tiene que usar
    Optional<Users> findByEmail (String email);

    Page<Users> findByEnable (boolean enable, Pageable page);

    // Filtra usuarios por Rol.name (ignorando mayúsculas) y devuelve Page<Users>
    Page<Users> findDistinctByUserProjectRols_Rol_NameIgnoreCase(
            String roleName,
            Pageable pageable
    );

    Page<Users> findDistinctByEnableAndUserProjectRols_Rol_NameIgnoreCase(boolean enable, String role, Pageable pageable);

    Page<Users> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Users> findByNameContainingIgnoreCaseAndEnableAndUserProjectRols_Rol_NameIgnoreCase(
            String name, boolean enable, String roleName, Pageable pageable
    );
    Page<Users> findByNameContainingIgnoreCaseAndUserProjectRols_Rol_NameIgnoreCase(
            String name, String roleName, Pageable pageable
    );
    Page<Users> findByNameContainingIgnoreCaseAndEnable(
            String name, boolean enable, Pageable pageable
    );





}
