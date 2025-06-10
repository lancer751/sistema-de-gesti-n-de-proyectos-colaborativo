package com.gestionproyectoscolaborativos.backend.repository;

import com.gestionproyectoscolaborativos.backend.entitys.histories.UserProjectRoleHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserProjectRoleHistoryRepository extends JpaRepository<com.gestionproyectoscolaborativos.backend.entitys.histories.UserProjectRoleHistory, Integer> {
    List<UserProjectRoleHistory> findByRolName (String name);
}
