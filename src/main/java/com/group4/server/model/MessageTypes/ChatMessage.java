package com.group4.server.model.MessageTypes;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "chatMessage")
@XmlAccessorType(XmlAccessType.NONE)
public class ChatMessage implements TransmittableMessage {

    @XmlElement
    private int fromId;
    @XmlElement
    private int chatId;
    @XmlElement
    private String text;

    public ChatMessage() {
    }

    public ChatMessage(int fromId, int chatId, String text) {
        this.fromId = fromId;
        this.chatId = chatId;
        this.text = text;
    }

    public int getFromId() {
        return fromId;
    }

    public void setFromId(int fromId) {
        this.fromId = fromId;
    }

    public int getChatId() {
        return chatId;
    }

    public void setChatId(int chatId) {
        this.chatId = chatId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
