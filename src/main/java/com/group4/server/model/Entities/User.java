package com.group4.server.model.Entities;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.Objects;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class User {
    @XmlElement
    private String nickname;
    @XmlElement
    private String password;
    @XmlElement
    private String fullName;

    public User() {
    }

    public User(String nickname, String password, String fullName) {
        this.nickname = nickname;
        this.password = password;
        this.fullName = fullName;
    }

    public String getNickname() {
        return nickname;
    }

    public String getPassword() {
        return password;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(nickname, user.nickname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nickname, password, fullName);
    }
}
