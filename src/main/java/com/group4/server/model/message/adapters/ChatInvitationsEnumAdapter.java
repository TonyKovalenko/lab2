package com.group4.server.model.message.adapters;

import com.group4.server.model.entities.ChatRoom;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Map;
import java.util.Set;

@XmlRootElement(name = "pendingChatInvitations")
@XmlAccessorType(XmlAccessType.NONE)
public class ChatInvitationsEnumAdapter {

    @XmlElement
    @XmlJavaTypeAdapter(ChatInvitationsMapAdapter.class)
    private Map<String, Set<ChatRoom>> pendingChatInvitations;

    public Map<String, Set<ChatRoom>> getPendingChatInvitations() {
        return pendingChatInvitations;
    }

    public void setPendingChatInvitations(Map<String, Set<ChatRoom>> pendingChatInvitations) {
        this.pendingChatInvitations = pendingChatInvitations;
    }

    public ChatInvitationsEnumAdapter() {
    }

    public ChatInvitationsEnumAdapter(Map<String, Set<ChatRoom>> pendingChatInvitations) {
        this.pendingChatInvitations = pendingChatInvitations;
    }
}
