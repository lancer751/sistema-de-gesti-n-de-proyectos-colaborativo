package com.gestionproyectoscolaborativos.backend.repository;

import com.gestionproyectoscolaborativos.backend.entitys.Project;
import com.gestionproyectoscolaborativos.backend.entitys.Rol;
import com.gestionproyectoscolaborativos.backend.entitys.Users;
import com.gestionproyectoscolaborativos.backend.entitys.tablesintermedate.UserProject;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UserProjectRepository extends JpaRepository<UserProject, Long> {
    @Query("SELECT upr FROM UserProject upr WHERE upr.users = :user")
    List<UserProject> findByUsers(@Param("user") Users users);

    @Query("SELECT upr FROM UserProject upr WHERE upr.project = :project")
    List<UserProject> findByProject(@Param("project") Project project);

    void deleteByUsersAndProject(Users user, Project project);

    @Modifying
    @Transactional
    @Query("DELETE FROM UserProjectRol upr WHERE upr.project.id = :id")
    void deleteByProjectId(@Param("id") Integer id);



    @Query("SELECT up FROM UserProject up WHERE up.rolproject = :rol")
    List<UserProject> findByRolproject(@Param("rol") String rol);

    Page<UserProject> findByRolproject(String rolproject, Pageable pageable);


    @Query(
            value = """
        SELECT * FROM users 
        WHERE id IN (
          SELECT DISTINCT up.user_id 
          FROM userproject up 
          WHERE up.rolproject = 'Lider'
        )
        AND (
            LOWER(users.name) LIKE LOWER(CONCAT('%', :search, '%')) OR
            LOWER(users.lastname) LIKE LOWER(CONCAT('%', :search, '%'))
        )
        ORDER BY id
        """,
            countQuery = """
        SELECT COUNT(*) FROM users 
        WHERE id IN (
          SELECT DISTINCT up.user_id 
          FROM userproject up 
          WHERE up.rolproject = 'Lider'
        )
        AND (
            LOWER(users.name) LIKE LOWER(CONCAT('%', :search, '%')) OR
            LOWER(users.lastname) LIKE LOWER(CONCAT('%', :search, '%'))
        )
        """,
            nativeQuery = true
    )
    Page<Users> findDistinctLiderUsers(@Param("search") String search, Pageable pageable);




}
