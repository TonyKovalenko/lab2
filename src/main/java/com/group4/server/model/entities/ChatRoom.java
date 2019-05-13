package com.group4.server.model.entities;

import com.group4.server.model.message.handlers.RegistrationAuthorizationHandler;
import com.group4.server.model.message.types.ChatMessage;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.*;

@XmlRootElement(name = "chatRoom")
@XmlAccessorType(XmlAccessType.NONE)
public class ChatRoom {
    @XmlElement
    private long id;
    @XmlElement
    private boolean isPrivate;
    @XmlElement
    private String name = "default";
    @XmlElement
    private Set<User> members = new HashSet<>();
    @XmlElement
    private List<ChatMessage> messages = new ArrayList<>();
    @XmlElement
    private String adminNickname;

    public ChatRoom() {
    }

    public ChatRoom(User user1, User user2) {
        members = new HashSet<>();
        members.add(user1);
        members.add(user2);
        this.isPrivate = true;
    }

    public ChatRoom(String name, Set<User> members) {
        this.isPrivate = false;
        this.members = members;
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ChatRoom(long id, User user1, User user2) {
        this(user1, user2);
        this.id = id;
    }

    public ChatRoom(long id, String name, Set<User> members) {
        this(name, members);
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        if (!isPrivate) {
            return name;
        }
        throw new UnsupportedOperationException("Operation can't be used for private chat");
    }

    public User getOtherMember(User user) {
        if (isPrivate) {
            Optional<User> otherMember = members.stream().filter(e -> !e.equals(user)).findFirst();
            if (otherMember.isPresent()) {
                return otherMember.get();
            } else {
                throw new NoSuchElementException("User was not found.");
            }
        }
        throw new UnsupportedOperationException("Operation can't be used for group chat.");
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public Set<User> getMembers() {
        return members;
    }

    public boolean containsMember(String nickname) {
        return members.contains(RegistrationAuthorizationHandler.INSTANCE.getUser(nickname));
    }

    public void setMembers(Set<User> members) {
        if (!isPrivate) {
            this.members = members;
        }
    }

    public void addMember(User newMember) {
        if (!isPrivate) {
            members.add(newMember);
        }
    }

    public void removeMember(String username) {
        if (!isPrivate) {
            members.removeIf(user -> user.getNickname().equals(username));
        }
    }

    public List<ChatMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<ChatMessage> messages) {
        this.messages = messages;
    }

    public ChatRoom addMessage(ChatMessage message) {
        messages.add(message);
        return this;
    }

    public boolean isMemberPresent(String nickname) {
        Optional<?> optionalUser = members.stream().filter(user -> user.getNickname().equals(nickname)).findFirst();
        return optionalUser.isPresent();
    }

    public boolean isEmpty() {
        return this.equals(new ChatRoom());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatRoom chatRoom = (ChatRoom) o;
        return id == chatRoom.id &&
                isPrivate == chatRoom.isPrivate &&
                name.equals(chatRoom.name) &&
                members.equals(chatRoom.members);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, isPrivate, name, members);
    }

    @Override
    public String toString() {
        return "ChatRoom{" +
                "id=" + id +
                ", isPrivate=" + isPrivate +
                ", name='" + name + '\'' +
                ", adminNickname='" + adminNickname + '\'' +
                ", members=" + members +
                ", messages=" + messages +
                '}';
    }

    public String getAdminNickname() {
        return adminNickname;
    }

    public void setAdminNickname(String adminNickname) {
        this.adminNickname = adminNickname;
    }
}
