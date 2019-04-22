package com.group4.server.model.containers;

import com.group4.server.model.entities.ChatRoom;
import com.group4.server.model.message.adapters.ChatInvitationsEnumAdapter;
import org.apache.log4j.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


public enum ChatInvitationsContainer {

    INSTANCE;

    private final Logger log = Logger.getLogger(ChatInvitationsContainer.class);
    private String marshallFilePath = "invitationsContainer.xml";
    private Map<String, Set<ChatRoom>> pendingChatInvitations = new ConcurrentHashMap<>();

    ChatInvitationsContainer() {
        unmarshallOnStart();
    }

    private void unmarshallOnStart() {
        ChatInvitationsEnumAdapter adapter;
        try (BufferedReader br = new BufferedReader(new FileReader(new File(marshallFilePath)))) {
            JAXBContext context = JAXBContext.newInstance(ChatInvitationsEnumAdapter.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            adapter = (ChatInvitationsEnumAdapter) unmarshaller.unmarshal(br);
            pendingChatInvitations = adapter.getPendingChatInvitations();
            log.info("Chat invitations successfully loaded from a file [" + marshallFilePath + "]");
        } catch (IOException | JAXBException ex) {
            log.error("Error while unmarshalling chat invitations container from file " + marshallFilePath + " " + ex);
        }
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

    public Map<String, Set<ChatRoom>> getContainer() {
        return new ConcurrentHashMap<>(pendingChatInvitations);
    }
}
