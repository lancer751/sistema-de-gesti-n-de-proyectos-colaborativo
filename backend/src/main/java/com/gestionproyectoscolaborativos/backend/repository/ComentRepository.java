package com.gestionproyectoscolaborativos.backend.repository;

import com.gestionproyectoscolaborativos.backend.entitys.Coment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComentRepository extends JpaRepository<Coment, Integer> {
}
