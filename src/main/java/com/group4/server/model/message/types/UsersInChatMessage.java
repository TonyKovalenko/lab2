package com.group4.server.model.message.types;

import com.group4.server.model.entities.User;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashMap;

@XmlRootElement(name = "usersInChatMessage")
@XmlAccessorType(XmlAccessType.NONE)
public class UsersInChatMessage implements TransmittableMessage {
    @XmlElement
    private HashMap<Long, User> users;

    public UsersInChatMessage() {
    }

    public UsersInChatMessage(HashMap<Long, User> users) {
        this.users = users;
    }

    public HashMap<Long, User> getUsers() {
        return users;
    }

    public void setUsers(HashMap<Long, User> users) {
        this.users = users;
    }
}
