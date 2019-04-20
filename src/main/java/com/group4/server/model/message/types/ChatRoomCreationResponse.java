package com.group4.server.model.message.types;

import com.group4.server.model.entities.ChatRoom;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class ChatRoomCreationResponse implements TransmittableMessage{

    @XmlElement
    private boolean successful;
    @XmlElement
    private ChatRoom chatRoom;

    public ChatRoomCreationResponse() {
    }

    public ChatRoomCreationResponse(boolean successful) {
        this.successful = successful;
    }

    public ChatRoomCreationResponse(boolean successful, ChatRoom chatRoom) {
        this.successful = successful;
        this.chatRoom = chatRoom;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }



    public ChatRoom getChatRoom() {
        return chatRoom;
    }

    public void setChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }
}
