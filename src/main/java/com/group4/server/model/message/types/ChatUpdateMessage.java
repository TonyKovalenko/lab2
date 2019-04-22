package com.group4.server.model.message.types;

import com.group4.server.model.entities.User;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "chatUpdateMessage")
@XmlAccessorType(XmlAccessType.NONE)
public class ChatUpdateMessage implements TransmittableMessage {
    @XmlElement
    private long chatRoomId;
    @XmlElement
    private String newName;
    @XmlElement
    private List<User> membersToAdd;
    @XmlElement
    private List<User> membersToDelete;

    public ChatUpdateMessage() {
    }

    public ChatUpdateMessage(long chatRoomId, String newName, List<User> membersToAdd, List<User> membersToDelete) {
        this.chatRoomId = chatRoomId;
        this.newName = newName;
        this.membersToAdd = membersToAdd;
        this.membersToDelete = membersToDelete;
    }

    public long getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(long chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }

    public List<User> getMembersToAdd() {
        return membersToAdd;
    }

    public void setMembersToAdd(List<User> membersToAdd) {
        this.membersToAdd = membersToAdd;
    }

    public List<User> getMembersToDelete() {
        return membersToDelete;
    }

    public void setMembersToDelete(List<User> membersToDelete) {
        this.membersToDelete = membersToDelete;
    }
}
