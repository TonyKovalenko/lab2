package com.group4.server.model.message.types;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "changeCredentialsRequest")
@XmlAccessorType(XmlAccessType.NONE)
public class ChangeCredentialsRequest implements TransmittableMessage {

    @XmlElement
    private String userNickname;
    @XmlElement
    private String newFullName;
    @XmlElement
    private String newPassword;

    public ChangeCredentialsRequest() {
    }

    public ChangeCredentialsRequest(String userNickname, String newFullName, String newPassword) {
        this.userNickname = userNickname;
        this.newFullName = newFullName;
        this.newPassword = newPassword;
    }

    public String getNewFullName() {
        return newFullName;
    }

    public void setNewFullName(String newFullName) {
        this.newFullName = newFullName;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }
}
