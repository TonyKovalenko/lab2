package com.group4.server.model.messageTypes;

import com.group4.server.model.entities.User;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "registrationRequest")
@XmlAccessorType(XmlAccessType.NONE)
public class RegistrationRequest implements TransmittableMessage {
    @XmlElement
    private String userNickname;
    @XmlElement
    private String password;
    @XmlElement
    private String fullName;

    public RegistrationRequest() {
    }

    public RegistrationRequest(User user) {
        this.userNickname = user.getNickname();
        this.password = user.getPassword();
        this.fullName = user.getFullName();
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
