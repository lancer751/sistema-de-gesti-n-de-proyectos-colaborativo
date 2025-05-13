package com.gestionproyectoscolaborativos.backend.services.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gestionproyectoscolaborativos.backend.services.dto.request.RolDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDtoResponse {

    private Integer id;

    private String name;


    private String lastname;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "America/Lima")
    private Date entryDate;
    private String email;

    private String numberPhone;
    private String description;
    private boolean isActive;
    private List<RolDto> rolDtoList;
}
