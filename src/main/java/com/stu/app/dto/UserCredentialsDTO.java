package com.stu.app.dto;

import lombok.Data;

import org.springframework.stereotype.Component;


@Component
@Data
public class UserCredentialsDTO {
  String username;
  String password;
  String timeZone;
  String token;
  boolean emailLogin;
  String phoneNumber;

/*  public String getPassword() {
    return AesUtil.decrypt(password);
  }

  public String getToken() {
    return AesUtil.decrypt(token);
  }*/
}

