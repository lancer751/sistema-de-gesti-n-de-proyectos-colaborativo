package com.gestionproyectoscolaborativos.backend.repository;

import com.gestionproyectoscolaborativos.backend.entitys.Project;
import com.gestionproyectoscolaborativos.backend.entitys.State;
import com.gestionproyectoscolaborativos.backend.entitys.enums.Priority;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer>, JpaSpecificationExecutor<Project> {

        @Query(value = """
        SELECT p.*
        FROM project p
        JOIN userproject up ON up.project_id = p.id
        JOIN users u ON u.id = up.user_id
        JOIN state s ON s.id = p.state_id
        WHERE 
          (:search IS NULL OR LOWER(p.name) LIKE %:search%)
          AND (:iduser IS NULL OR u.id = :iduser)
          AND (:state IS NULL OR s.name = :state)
          AND (:priority IS NULL OR p.priority = :priority)
          AND (:startp IS NULL OR p.date_start >= :startp)
          AND (:endp IS NULL OR p.date_deliver <= :endp)
        ORDER BY 
          CASE WHEN :sortDir = 'asc' THEN p.name END ASC,
          CASE WHEN :sortDir = 'desc' THEN p.name END DESC
        LIMIT :size OFFSET :offset
        """,
                nativeQuery = true)
        List<Project> searchProjectsAdmin(
                @Param("search") String search,
                @Param("iduser") Integer iduser,
                @Param("state") String state,
                @Param("priority") String priority,
                @Param("startp") LocalDateTime startp,
                @Param("endp") LocalDateTime endp,
                @Param("sortDir") String sortDir,
                @Param("size") int size,
                @Param("offset") int offset
        );

        List<Project> findByState (State state);
}




