package com.group4.server.model.entities;

import com.group4.server.model.MessageTypes.ChatMessage;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "chatRoom")
@XmlAccessorType(XmlAccessType.NONE)
public class ChatRoom {
    @XmlElement
    private int id;
    @XmlElement
    private boolean isPrivate;
    @XmlElement
    private String name;
    @XmlElement
    private List<User> members;
    @XmlElement
    private List<ChatMessage> messages;

    public ChatRoom() {
    }

    public ChatRoom(int id, boolean isPrivate, List<User> members) {
        this.id = id;
        this.isPrivate = isPrivate;
        this.members = members;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public List<User> getMembers() {
        return members;
    }

    public void setMembers(List<User> members) {
        this.members = members;
    }

    public List<ChatMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<ChatMessage> messages) {
        this.messages = messages;
    }

    public void addMessage(ChatMessage message) {
        messages.add(message);
    }

    @Override
    public String toString() {
        return "ChatRoom{" +
                "id=" + id +
                ", isPrivate=" + isPrivate +
                ", name='" + name + '\'' +
                ", members=" + members +
                ", messages=" + messages +
                '}';
    }
}
