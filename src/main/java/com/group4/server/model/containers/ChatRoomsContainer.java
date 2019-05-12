package com.group4.server.model.containers;

import com.group4.server.model.entities.ChatRoom;
import com.group4.server.model.entities.User;
import com.group4.server.model.message.adapters.ChatContainerEnumAdapter;
import com.group4.server.model.message.types.ChatMessage;
import org.apache.log4j.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * ChatRoomsContainer class is primary for holding chat rooms
 * and performing different operations with them.
 *
 * @author Anton Kovalenko
 * @since 05-06-18
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public enum ChatRoomsContainer {

    INSTANCE;

    private final Logger log = Logger.getLogger(ChatRoomsContainer.class);
    private AtomicLong id;

    private String marshallFilePath = "idToChatRoom.xml";
    @XmlElement
    private Map<Long, ChatRoom> idToChatRoom = new ConcurrentHashMap<>();

    /**
     * Constructor to create chat rooms container from {@link #marshallFilePath} file, if it is not the first launch
     * In case of first launch, the default main chat room is created.
     */
    ChatRoomsContainer() {
        unmarshallOnStart();
        int keySetSize = idToChatRoom.keySet().size();
        if (keySetSize == 0) {
            id = new AtomicLong(0);
            idToChatRoom.put(id.incrementAndGet(), new ChatRoom(id.longValue(), "mainChatRoom", new HashSet<>()));
            return;
        }
        id = new AtomicLong(keySetSize);
    }

    /**
     * Method to unmarshall the chat rooms container from the {@link #marshallFilePath} file
     * to a internal collection.
     */
    private void unmarshallOnStart() {
        ChatContainerEnumAdapter adapter;
        try (BufferedReader br = new BufferedReader(new FileReader(new File(marshallFilePath)))) {
            JAXBContext context = JAXBContext.newInstance(ChatContainerEnumAdapter.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            adapter = (ChatContainerEnumAdapter) unmarshaller.unmarshal(br);
            idToChatRoom = adapter.getIdToChatRoom();
            log.info("Chat rooms successfully loaded from a file [" + marshallFilePath + "]");
        } catch (IOException | JAXBException ex) {
            log.error("Error while unmarshalling chat room container from file " + marshallFilePath + " " + ex);
        }
    }


    public void putToInitialRoom(User user) {
        idToChatRoom.get(1L).addMember(user);
    }

    public ChatRoom getMainChatRoom() {
        return idToChatRoom.get(1L);
    }

    /**
     * Method for getting a chatrooms for specified user
     *
     * @param nickname user nickname, to get chatrooms for
     * @return Set of chatrooms with specified user
     */
    public Set<ChatRoom> getChatRoomsFor(String nickname) {
        return idToChatRoom.values().stream().filter(room -> room.isMemberPresent(nickname)).collect(Collectors.toSet());
    }

    /**
     * Method for deleting a specified user from all chatrooms, he is currently in
     *
     * @param nickname user nickname, to delete from chatrooms
     */
    public void deleteUserFromChatRooms(String nickname) {
        idToChatRoom.values()
                .stream()
                .parallel()
                .filter(chatRoom -> chatRoom.containsMember(nickname))
                .forEach(members -> members.removeMember(nickname));
    }

    /**
     * Method for inserting chatroom into container
     *
     * @param chatRoom chatroom to be added to container
     */
    public boolean createChatRoom(ChatRoom chatRoom) {
        if (idToChatRoom.values().contains(chatRoom)) {
            chatRoom.setId(-1);
            return false;
        }
        idToChatRoom.put(id.incrementAndGet(), chatRoom);
        chatRoom.setId(id.longValue());
        return true;
    }

    public ChatRoom getChatRoomById(long id) {
        ChatRoom room = idToChatRoom.get(id);
        if (room != null) {
            return room;
        }
        return new ChatRoom();
    }

    public void addMessageToChat(long id, ChatMessage message) {
        idToChatRoom.computeIfPresent(id, (k, v) -> v.addMessage(message));
    }

    public String getMarshallingFilePath() {
        return marshallFilePath;
    }

    public Map<Long, ChatRoom> getContainer() {
        return new ConcurrentHashMap<>(idToChatRoom);
    }
}
