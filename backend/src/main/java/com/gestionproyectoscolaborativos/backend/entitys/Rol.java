package com.gestionproyectoscolaborativos.backend.entitys;

import com.gestionproyectoscolaborativos.backend.entitys.fields.AuditFields;
import com.gestionproyectoscolaborativos.backend.entitys.tablesintermedate.UserProjectRol;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "rol")
public class Rol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Embedded
    private AuditFields auditFields;

    @OneToMany(mappedBy = "rol")
    private List<UserProjectRol> rolUserProject;
}
