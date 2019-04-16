package com.group4.server.model.entities;

import com.group4.server.model.message.types.ChatMessage;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private Map<Integer, User> members;
    @XmlElement
    private List<ChatMessage> messages = new ArrayList<>();

    public ChatRoom() {}

    public ChatRoom(User user1, User user2) {
        members = new HashMap<>();
        members.put(user1.getId(), user1);
        members.put(user2.getId(), user2);
        this.isPrivate = true;
    }

    public ChatRoom(String name, Map<Integer, User> members) {
        this.isPrivate = false;
        this.members = members;
        this.name = name;
    }

    public ChatRoom(int id, User user1, User user2) {
        this(user1, user2);
        this.id = id;
    }

    public ChatRoom(int id, String name, Map<Integer, User> members) {
        this(name, members);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        if (!isPrivate) {
            return name;
        }
        throw new RuntimeException("Operation can't be used for group chat");
    }

    public User getOtherMember(User user) {
        if (isPrivate) {
            List<User> membersList= new ArrayList<>(members.values());
            if (membersList.get(0).equals(user)) {
                return membersList.get(1);
            } else if (membersList.get(1).equals(user)) {
                return membersList.get(0);
            }
            System.out.println("Current user: " + user);
            System.out.println("Users in chat: " + getMembers());
            throw new RuntimeException("User was not found.");
        }
        throw new RuntimeException("Operation can't be used for private chat.");
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public Map<Integer, User> getMembers() {
        return members;
    }

    public void setMembers(Map<Integer, User> members) {
        if (!isPrivate) {
            this.members = members;
        }
    }

    public void addMember(User newMember) {
        if (!isPrivate) {
            members.put(newMember.getId(), newMember);
        }
    }

    public void removeMember(User member) {
        if (!isPrivate) {
            members.remove(member.getId());
        }
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
