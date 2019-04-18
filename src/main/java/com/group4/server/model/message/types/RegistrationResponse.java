package com.group4.server.model.message.types;

import com.group4.server.model.entities.ChatRoom;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RegistrationResponse implements TransmittableMessage {

    @XmlElement
    private boolean registrationState;
    @XmlElement
    private ChatRoom mainChatRoom;

    public RegistrationResponse() {
    }

    public RegistrationResponse(boolean registrationState) {
        this.registrationState = registrationState;
    }

    public RegistrationResponse(boolean state, ChatRoom mainChatRoom) {
        this(state);
        this.mainChatRoom = mainChatRoom;
    }

    public boolean isRegistrationSuccessful() {
        return registrationState;
    }

    public void setRegistrationState(boolean registrationState) {
        this.registrationState = registrationState;
    }

    public ChatRoom getMainChatRoom() {
        return mainChatRoom;
    }

    public void setMainChatRoom(ChatRoom mainChatRoom) {
        this.mainChatRoom = mainChatRoom;
    }
}
