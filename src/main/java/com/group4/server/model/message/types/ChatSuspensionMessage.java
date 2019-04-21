package com.group4.server.model.message.types;

import com.group4.server.model.entities.ChatRoom;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class ChatSuspensionMessage implements TransmittableMessage {

    @XmlElement
    private long chatId;

    public ChatSuspensionMessage() {
    }

    public ChatSuspensionMessage(long chatId) {
        this.chatId = chatId;
    }

    public ChatSuspensionMessage(ChatRoom room) {
        this.chatId = room.getId();
    }

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }
}
