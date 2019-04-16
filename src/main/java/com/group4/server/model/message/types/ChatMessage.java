package com.group4.server.model.message.types;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "chatMessage")
@XmlAccessorType(XmlAccessType.NONE)
public class ChatMessage implements TransmittableMessage {

    @XmlElement
    private long fromId;
    @XmlElement
    private long chatId;
    @XmlElement
    private String text;

    public ChatMessage() {
    }

    public ChatMessage(long fromId, int chatId, String text) {
        this.fromId = fromId;
        this.chatId = chatId;
        this.text = text;
    }

    public long getFromId() {
        return fromId;
    }

    public void setFromId(long fromId) {
        this.fromId = fromId;
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
