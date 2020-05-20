package com.stu.app.dto;

import javax.validation.constraints.NotNull;

import lombok.Data;

import org.springframework.stereotype.Component;

@Component
@Data
public class SignInResponseDTO {

    @NotNull
    String accessToken;
    @NotNull
    String tokenType;
    String userType;
    
}
