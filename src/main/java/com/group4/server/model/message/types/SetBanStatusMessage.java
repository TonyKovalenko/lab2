package com.group4.server.model.message.types;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class SetBanStatusMessage implements TransmittableMessage {
    @XmlElement
    private String userNickname;
    @XmlElement
    private boolean isBanned;

    public SetBanStatusMessage() {
    }

    public SetBanStatusMessage(String userNickname, boolean isBanned) {
        this.userNickname = userNickname;
        this.isBanned = isBanned;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    public boolean isBanned() {
        return isBanned;
    }

    public void setBanned(boolean banned) {
        isBanned = banned;
    }
}
