package com.group4.server.model.message.types;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "logoutMessage")
@XmlAccessorType(XmlAccessType.NONE)
public class UserLogoutMessage implements TransmittableMessage {
    @XmlElement
    private String nickname;

    public UserLogoutMessage() {
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public UserLogoutMessage(String nickname) {
        this.nickname = nickname;
    }
}
