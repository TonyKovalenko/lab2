package com.group4.server.model.message.types;

import com.group4.server.model.entities.ChatRoom;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.CRC32;

@XmlRootElement(name = "newGroupChatMessage")
@XmlAccessorType(XmlAccessType.NONE)
public class ChatInvitationMessage implements TransmittableMessage {
    @XmlElement
    private Set<ChatRoom> chatRooms;

    {
        chatRooms = new HashSet<>();
    }

    public ChatInvitationMessage() {
    }

    public ChatInvitationMessage(ChatRoom...chatRooms) {
        this.chatRooms.addAll(Arrays.asList(chatRooms));
    }

    public ChatInvitationMessage(Set<ChatRoom> chatRooms) {
        this.chatRooms = chatRooms;
    }

    public Set<ChatRoom> getChatRooms() {
        return chatRooms;
    }


    public void setChatRooms(Set<ChatRoom> chatRoom) {
        this.chatRooms = chatRoom;
    }
}
