package com.gestionproyectoscolaborativos.backend.services.dto.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private String name;


    private String lastname;


    private String email;
    private String numberPhone;
    private String description;

    private String password;

    private List<RolDto> rolDtoList;
}
