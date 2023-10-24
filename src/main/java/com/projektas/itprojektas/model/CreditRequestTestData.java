package com.projektas.itprojektas.model;

import com.projektas.itprojektas.model.dto.CreditRequestDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreditRequestTestData {
    CreditRequestDTO creditRequestDTO;
    CreditRequest creditRequest;

    public CreditRequestTestData(User user, double credits) {
        creditRequestDTO = new CreditRequestDTO();
        creditRequestDTO.setUser(user);
        creditRequestDTO.setCredits(credits);

        creditRequest = new CreditRequest();
        creditRequest.setUser(user);
        creditRequest.setCredits(credits);
    }
}
