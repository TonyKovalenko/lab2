package com.group4.server.model.message.types;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class DeleteUserResponse implements TransmittableMessage {
    @XmlElement
    private String userNickname;
    @XmlElement
    private boolean isConfirmed;

    public DeleteUserResponse() {
    }

    public DeleteUserResponse(String userNickname, boolean isConfirmed) {
        this.userNickname = userNickname;
        this.isConfirmed = isConfirmed;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    public void setConfirmed(boolean confirmed) {
        isConfirmed = confirmed;
    }

    public boolean isConfirmed() {
        return isConfirmed;
    }
}
