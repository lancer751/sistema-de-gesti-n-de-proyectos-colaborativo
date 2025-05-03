package com.gestionproyectoscolaborativos.backend.entitys;

import com.gestionproyectoscolaborativos.backend.entitys.fields.AuditFields;
import com.gestionproyectoscolaborativos.backend.entitys.tablesintermedate.UserProjectRol;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name", nullable = false, length = 50)
    @NotBlank(message = "name should´nt null")
    @NotEmpty(message = "name should´nt empty")
    private String name;

    @Column(name = "lastname", nullable = false, length = 50)
    @NotBlank(message = "lastname should´nt null")
    @NotEmpty(message = "lastname should´nt empty")
    private String lastname;

    @Column(name = "email", nullable = false, unique = true)
    @Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}", flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "emil incorrect")
    private String email;

    @Column(name = "description",  length = 255)
    private String description;

    @Column(name = "phone", length = 60)
    private String numberPhone;

    @Column(name="entrydate")
    private Date entryDate;

    @Column(name = "enabled", columnDefinition = "BOOLEAN DEFAULT true", nullable = false)
    private boolean enable;

    @Column(name = "password", nullable = false, length = 250)
    private String password;

    @Embedded
    private AuditFields auditFields;

    @OneToMany(mappedBy = "users")
    private List<UserProjectRol> userProjectRols;

    @ManyToMany
    @JoinTable(name = "user_activities",
    joinColumns = @JoinColumn(name = "user_id"),
    inverseJoinColumns = @JoinColumn(name = "activity_id"),
            uniqueConstraints = { @UniqueConstraint(columnNames = {"user_id", "activity_id"})})
    private List<Activity> activities;
}
