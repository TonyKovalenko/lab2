package com.group4.client.model;

import java.util.Date;

public class User {
    private String nickname;
    private String fullName;
    private Date registrationDate;

    public User() {
    }

    public User(String nickname, String fullName, Date registrationDate) {
        this.nickname = nickname;
        this.fullName = fullName;
        this.registrationDate = registrationDate;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }
}
