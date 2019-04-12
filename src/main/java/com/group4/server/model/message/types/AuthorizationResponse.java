package com.group4.server.model.message.types;

import com.group4.server.model.entities.ChatRoom;
import com.group4.server.model.entities.User;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "authorizationResponse")
@XmlAccessorType(XmlAccessType.NONE)
public class AuthorizationResponse implements TransmittableMessage {
    @XmlElement
    private boolean isConfirmed;
    @XmlElement
    private User user;
    @XmlElement
    private ChatRoom mainChatRoom;

    public AuthorizationResponse() {
    }

    public AuthorizationResponse(boolean isConfirmed, User user, ChatRoom mainChatRoom) {
        this.isConfirmed = isConfirmed;
        this.user = user;
        this.mainChatRoom = mainChatRoom;
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

    public ChatRoom getMainChatRoom() {
        return mainChatRoom;
    }

    public void setMainChatRoom(ChatRoom mainChatRoom) {
        this.mainChatRoom = mainChatRoom;
    }
}
