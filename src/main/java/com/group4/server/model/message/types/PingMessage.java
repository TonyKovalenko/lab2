package com.group4.server.model.message.types;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "pingMessage")
@XmlAccessorType(XmlAccessType.NONE)
public class PingMessage implements TransmittableMessage {
    @XmlElement
    private String userNickname;
    @XmlElement
    private boolean isAlive;

    public PingMessage() {
    }

    public PingMessage(String userNickname, boolean isAlive) {
        this.userNickname = userNickname;
        this.isAlive = isAlive;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserID(String userNickname) {
        this.userNickname = userNickname;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }
}
