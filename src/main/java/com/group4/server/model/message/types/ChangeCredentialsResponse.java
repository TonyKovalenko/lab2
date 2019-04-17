package com.group4.server.model.message.types;

import com.group4.server.model.entities.User;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "changeCredentialsResponse")
@XmlAccessorType(XmlAccessType.NONE)
public class ChangeCredentialsResponse implements TransmittableMessage {
    @XmlElement
    private boolean isConfirmed;
    @XmlElement
    private User user;

    public ChangeCredentialsResponse() {
    }

    public ChangeCredentialsResponse(boolean isConfirmed, User user) {
        this.isConfirmed = isConfirmed;
        this.user = user;
    }

    public boolean isConfirmed() {
        return isConfirmed;
    }

    public void setConfirmed(boolean confirmed) {
        isConfirmed = confirmed;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
