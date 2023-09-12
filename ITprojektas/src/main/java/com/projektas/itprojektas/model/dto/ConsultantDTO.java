package com.projektas.itprojektas.model.dto;

import com.projektas.itprojektas.model.ConsultantStatus;
import com.projektas.itprojektas.model.Roles;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConsultantDTO {

    private int id;

    @NotEmpty
    private String name;

    @NotEmpty
    private String surname;

    @NotEmpty
    private String username;

    @NotEmpty
    private String password;
    
    private double credits;
    private ConsultantStatus status;
    private Roles role;
}
