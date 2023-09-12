package com.projektas.itprojektas.model.dto;

import com.projektas.itprojektas.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreditRequestDTO {

    private int id;

    @NotNull
    private double credits;

    private User user;
}
