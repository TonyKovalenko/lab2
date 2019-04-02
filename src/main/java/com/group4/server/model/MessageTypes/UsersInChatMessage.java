package com.group4.server.model.MessageTypes;

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
    private HashMap<Integer, User> users;

    public UsersInChatMessage() {
    }

    public UsersInChatMessage(HashMap<Integer, User> users) {
        this.users = users;
    }

    public HashMap<Integer, User> getUsers() {
        return users;
    }

    public void setUsers(HashMap<Integer, User> users) {
        this.users = users;
    }
}
