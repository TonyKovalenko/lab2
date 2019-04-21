package com.group4.server.model.message.types;


import com.group4.server.model.entities.User;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashSet;
import java.util.Set;

@XmlRootElement(name = "usersInChatMessage")
@XmlAccessorType(XmlAccessType.NONE)
public class OnlineListMessage implements TransmittableMessage {
    @XmlElement
    private Set<User> users = new HashSet<>();

    public OnlineListMessage() {
    }

    public OnlineListMessage(Set<User> users) {
        this.users = users;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }
}
