package com.group4.server.model.message.types;

import com.group4.server.model.entities.ChatRoom;
import com.group4.server.model.entities.User;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashSet;
import java.util.Set;

@XmlRootElement(name = "authorizationResponse")
@XmlAccessorType(XmlAccessType.NONE)
public class AuthorizationResponse implements TransmittableMessage {
    @XmlElement
    private boolean isConfirmed;
    @XmlElement
    private User user;
    @XmlElement
    private Set<ChatRoom> chatRoomsWithUser;

    {
        user = new User();
        chatRoomsWithUser = new HashSet<>();
    }

    public AuthorizationResponse() {
    }

    public AuthorizationResponse(boolean isConfirmed) {
        this.isConfirmed = isConfirmed;
    }

    public AuthorizationResponse(boolean isConfirmed, User user, Set<ChatRoom> chatRoomsWithUser) {
        this.isConfirmed = isConfirmed;
        this.user = user;
        this.chatRoomsWithUser = chatRoomsWithUser;
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

    public Set<ChatRoom> getChatRoomsWithUser() {
        return chatRoomsWithUser;
    }

    public void setChatRoomsWithUser(Set<ChatRoom> chatRoomsWithUser) {
        this.chatRoomsWithUser = chatRoomsWithUser;
    }
}
