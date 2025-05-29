package com.gestionproyectoscolaborativos.backend.entitys;

import com.gestionproyectoscolaborativos.backend.entitys.enums.Priority;
import com.gestionproyectoscolaborativos.backend.entitys.fields.AuditFields;
import com.gestionproyectoscolaborativos.backend.entitys.tablesintermedate.UserProject;
import com.gestionproyectoscolaborativos.backend.entitys.tablesintermedate.UserProjectRol;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "project")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, length = 50)
    @NotBlank(message = "name should´nt null")
    @NotEmpty(message = "name should´nt empty")
    private String name;

    @Column(name = "description", nullable = true, length = 250)
    @NotBlank(message = "description should´nt null")
    @NotEmpty(message = "description should´nt empty")
    private String description;

    @Column(name = "date_start", nullable = false)
    private LocalDateTime dateStart;

    @Column(name = "date_deliver", nullable = false)
    private LocalDateTime dateDeliver; // fecha entrega

    @Column(name = "createdby")
    private String createdBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", length = 20)
    private Priority priority;

    @Column(name = "active")
    private boolean active;


    @Embedded
    private AuditFields auditFields;

    @ManyToOne
    @JoinColumn(name = "state_id", nullable = false)
    private State state;

    @OneToMany(mappedBy = "project")
    private List<UserProjectRol> projectUserRol;

    //creado recien
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserProject> userProjects = new ArrayList<>();
}
