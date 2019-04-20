package com.group4.server.model.message.types;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "chatMessage")
@XmlAccessorType(XmlAccessType.NONE)
public class ChatMessage implements TransmittableMessage {

    @XmlElement
    private String sender;
    @XmlElement
    private long chatId;
    @XmlElement
    private String text;

    public ChatMessage() {
    }

    public ChatMessage(String sender, int chatId, String text) {
        this.sender = sender;
        this.chatId = chatId;
        this.text = text;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
