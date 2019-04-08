package com.group4.server.model.entities;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@XmlRootElement
public class User {
    private int id;
    private String nickname;
    private String fullName;

    public String getPassword() {
        return password;
    }

    private String password;

    public User() {
    }

    public User(String nickname, String password, String fullName) {
        this.nickname = nickname;
        this.fullName = fullName;
        this.password = password;
    }

    public User(int id, String nickname, String fullName, String password) {
        this.id = id;
        this.nickname = nickname;
        this.fullName = fullName;
        this.password = password;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
