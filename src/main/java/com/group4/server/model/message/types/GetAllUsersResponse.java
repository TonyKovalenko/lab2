package com.group4.server.model.message.types;

import com.group4.server.model.entities.User;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.Set;

@XmlRootElement(name = "allUsersResponse")
@XmlAccessorType(XmlAccessType.NONE)
public class GetAllUsersResponse implements TransmittableMessage {
    @XmlElement
    private Set<User> users;

    public GetAllUsersResponse() {
    }

    public GetAllUsersResponse(Set<User> users) {
        this.users = users;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }
}
