package com.group4.server.model.entities;

import java.util.Date;

public class User {
    private int id;
    private String nickname;
    private String fullName;
    private Date registrationDate;

    public User() {
    }

    public User(int id, String nickname, String fullName, Date registrationDate) {
        this.id = id;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
