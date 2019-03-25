package com.group4.server.model.MessageTypes;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "authorizationMessage")
@XmlAccessorType(XmlAccessType.NONE)
public class AuthorizationMessage implements TransmittableMessage {

    @XmlElement
    private  String userNickname;
    @XmlElement
    private String password;

    public AuthorizationMessage() {
    }

    public AuthorizationMessage(String userNickname, String password) {
        this.userNickname = userNickname;
        this.password = password;
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
}
