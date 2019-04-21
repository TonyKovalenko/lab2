package com.group4.server.model.containers;

import com.group4.server.model.entities.ChatRoom;
import com.group4.server.model.message.adapters.ChatInvitationsAdapter;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public enum ChatInvitationsContainer {

    INSTANCE;

    private String marshallFilePath = "invitationsContainer.xml";
    @XmlElement
    @XmlJavaTypeAdapter(ChatInvitationsAdapter.class)
    private Map<String, Set<ChatRoom>> pendingChatInvitations;

    ChatInvitationsContainer() {
        pendingChatInvitations = new ConcurrentHashMap<>();
    }

    public void saveChatInvitation(String nickname, ChatRoom chatRoom) {
        pendingChatInvitations.computeIfAbsent(nickname, k -> new HashSet<>()).add(chatRoom);
    }

    public Set<ChatRoom> getChatInvitationsFor(String nickname) {
        Set<ChatRoom> chatRoomsWithUser = pendingChatInvitations.get(nickname);
        if (chatRoomsWithUser == null) {
            return new HashSet<>();
        }
        return chatRoomsWithUser;
    }

    public void removeInvitations(String nickname) {
        pendingChatInvitations.remove(nickname);
    }

    public String getMarshallingFilePath() {
        return marshallFilePath;
    }

    public Map<String, Set<ChatRoom>> getPendingChatInvitations() {
        return new ConcurrentHashMap<>(pendingChatInvitations);
    }
}
