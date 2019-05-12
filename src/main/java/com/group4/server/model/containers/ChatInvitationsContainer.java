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

/**
 * Container, that holds chat invitations
 * (chat rooms, to which the user was invited while being offline)
 */
public enum ChatInvitationsContainer {

    INSTANCE;

    private final Logger log = Logger.getLogger(ChatInvitationsContainer.class);
    private String marshallFilePath = "invitationsContainer.xml";
    private Map<String, Set<ChatRoom>> pendingChatInvitations = new ConcurrentHashMap<>();

    /**
     * Enum constructor, that populates internal collection
     * when the class is being accesses at first.
     */
    ChatInvitationsContainer() {
        unmarshallOnStart();
    }

    /**
     * Method to unmarshall chat invitations collection from an XML file
     * to ann internal collection.
     */
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

    /**
     * Method to marshall chat invitations collection to an XML file.
     */
    public void saveChatInvitation(String nickname, ChatRoom chatRoom) {
        pendingChatInvitations.computeIfAbsent(nickname, k -> new HashSet<>()).add(chatRoom);
    }

    /**
     * Method to get chat rooms for the specified user, to which he was invited
     * @param nickname nickname of a user, to return chat rooms for
     * @return Set&lt;ChatRoom&gt; with the needed chat rooms
     */
    public Set<ChatRoom> getChatInvitationsFor(String nickname) {
        Set<ChatRoom> chatRoomsWithUser = pendingChatInvitations.get(nickname);
        if (chatRoomsWithUser == null) {
            return new HashSet<>();
        }
        return chatRoomsWithUser;
    }

    /**
     * Method to remove chat rooms for the specified user, to which he was invited
     * @param nickname nickname of a user, to return chat rooms for
     */
    public void removeInvitations(String nickname) {
        pendingChatInvitations.remove(nickname);
    }

    /**
     * Method to get the filepath, where to save the data
     * @return path to file
     */
    public String getMarshallingFilePath() {
        return marshallFilePath;
    }

    /**
     * Method to get the container with the chat invitations
     * @return Map, that holds user with corresponding chatrooms, to which he was invited.
     */
    public Map<String, Set<ChatRoom>> getContainer() {
        return new ConcurrentHashMap<>(pendingChatInvitations);
    }
}
