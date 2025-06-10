package com.gestionproyectoscolaborativos.backend.entitys;

import com.gestionproyectoscolaborativos.backend.entitys.fields.AuditFields;
import com.gestionproyectoscolaborativos.backend.entitys.tablesintermedate.UserProjectRol;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "report")
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="id_user_project_rol", nullable = false)
    private UserProjectRol userProjectRol;

    @Embedded
    private AuditFields auditFields;
}
