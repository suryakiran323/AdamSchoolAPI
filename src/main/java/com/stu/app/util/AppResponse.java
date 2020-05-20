package com.stu.app.util;

import lombok.Getter;
import lombok.Setter;

import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class AppResponse {
    public static final String SUCCESS = "success";
    public static final String FAIL = "fail";
    private String status;
    private Object message;

    public AppResponse() {

    }

    public AppResponse(String status, Object message) {
        this.status = status;
        this.message = message;
    }

    public AppResponse(String status) {
        this.status = status;
    }

}
