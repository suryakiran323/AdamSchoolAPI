package com.stu.app.security;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Data
public class SessionUser {

    private String username;

    private String password;

    private Date created;

    public boolean hasExpired() {
        if (created == null) {
            return true;
        }
        LocalDateTime localDateTime = created.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        localDateTime = localDateTime.plusHours(1);
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant()).before(new Date());
    }

}
