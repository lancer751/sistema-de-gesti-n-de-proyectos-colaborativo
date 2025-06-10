package com.gestionproyectoscolaborativos.backend.entitys;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gestionproyectoscolaborativos.backend.entitys.enums.Priority;
import com.gestionproyectoscolaborativos.backend.entitys.fields.AuditFields;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "activity")
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, length = 250)
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

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", length = 20)
    private Priority prioridad;
    @ManyToOne
    @JoinColumn(name = "state_id", nullable = false)
    private State state;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    // RELACIÓN RECURSIVA (ACTIVIDAD PADRE)
    @ManyToOne
    @JoinColumn(name = "activity_father")
    private Activity activityFather;

    @ManyToMany(mappedBy = "activities")
    @JsonIgnore
    private List<Users> users;

    @Embedded
    private AuditFields auditFields;
}
