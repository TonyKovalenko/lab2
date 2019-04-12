package com.group4.server.model.message.types;

import com.group4.server.model.entities.ChatRoom;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "newGroupChatMessage")
@XmlAccessorType(XmlAccessType.NONE)
public class NewGroupChatMessage implements TransmittableMessage {
    @XmlElement
    private ChatRoom chatRoom;

    public NewGroupChatMessage() {
    }

    public NewGroupChatMessage(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }

    public ChatRoom getChatRoom() {
        return chatRoom;
    }

    public void setChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }
}
