package com.gestionproyectoscolaborativos.backend.entitys.histories;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_project_role_hisotry")
public class UserProjectRoleHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "id_project", nullable = false)
    private Integer idProject;
    @Column(name = "name_project", nullable = false)
    private String  nameProject;
    @Column(name = "id_rol", nullable = false)
    private Integer idRol;
    @Column(name = "rol_name", nullable = false)
    private String rolName;
    @Column(name = "id_user", nullable = false)
    private Integer idUser;
    @Column(name = "user_name", nullable = false)
    private String userName;
    @CreationTimestamp
    @Column(name = "date_history", nullable = false, updatable = false)
    private LocalDateTime  dateHistory;
}
