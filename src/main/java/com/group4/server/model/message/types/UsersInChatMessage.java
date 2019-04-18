package com.group4.server.model.message.types;

import com.group4.server.model.entities.User;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashMap;
import java.util.List;

@XmlRootElement(name = "usersInChatMessage")
@XmlAccessorType(XmlAccessType.NONE)
public class UsersInChatMessage implements TransmittableMessage {
    @XmlElement
    private List<User> users;

    public UsersInChatMessage() {
    }

    public UsersInChatMessage(List<User> users) {
        this.users = users;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
