package com.jeeweel.syl.jwtask.business.main.module.basic;

import com.jeeweel.syl.jcloudlib.db.annotation.sqlite.Table;

/**
 * Created by asusa on 2015/11/26.
 */
@Table(name="users")
public class UsersItem {
    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
