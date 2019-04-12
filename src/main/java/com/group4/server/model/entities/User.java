package com.group4.server.model.entities;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Objects;

@XmlRootElement
public class User {
    private int id;
    private String nickname;
    private String fullName;
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

    public String getPassword() {
        return password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id &&
                Objects.equals(nickname, user.nickname) &&
                Objects.equals(fullName, user.fullName) &&
                Objects.equals(password, user.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nickname, fullName, password);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", nickname='" + nickname + '\'' +
                ", fullName='" + fullName + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
