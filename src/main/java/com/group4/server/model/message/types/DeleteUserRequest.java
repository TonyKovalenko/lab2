package com.group4.server.model.message.types;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "chatUpdateMessage")
@XmlAccessorType(XmlAccessType.NONE)
public class DeleteUserRequest implements TransmittableMessage {
    @XmlElement
    private String userNickname;

    public DeleteUserRequest() {
    }

    public DeleteUserRequest(String userNickname) {
        this.userNickname = userNickname;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }
}
