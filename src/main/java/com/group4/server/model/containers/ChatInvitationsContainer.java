package com.group4.server.model.containers;

import com.group4.server.model.entities.ChatRoom;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public enum ChatInvitationsContainer {

    INSTANCE;

    private Map<String, Set<ChatRoom>> pendingChatInvitations;

    private ChatInvitationsContainer() {
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
}
