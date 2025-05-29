package com.gestionproyectoscolaborativos.backend.repository;

import com.gestionproyectoscolaborativos.backend.entitys.Project;
import com.gestionproyectoscolaborativos.backend.entitys.Rol;
import com.gestionproyectoscolaborativos.backend.entitys.Users;
import com.gestionproyectoscolaborativos.backend.entitys.tablesintermedate.UserProject;

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

}
