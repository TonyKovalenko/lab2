package com.group4.server.model.MessageTypes;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement(name = "registrationMessage")
@XmlAccessorType(XmlAccessType.NONE)
public class RegistrationMessage implements TransmittableMessage, Serializable {
    @XmlElement
    private  String userNickname;
    @XmlElement
    private String password;
    @XmlElement
    private String fullName;

    public RegistrationMessage() {
    }

    public RegistrationMessage(String userNickname, String password, String fullName) {
        this.userNickname = userNickname;
        this.password = password;
        this.fullName = fullName;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
