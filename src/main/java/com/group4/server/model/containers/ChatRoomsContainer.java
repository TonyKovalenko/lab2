package com.group4.server.model.containers;

import com.group4.server.model.entities.ChatRoom;
import com.group4.server.model.entities.User;
import com.group4.server.model.message.types.ChatMessage;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public enum ChatRoomsContainer {

    INSTANCE;

    ChatRoomsContainer() {
        id = new AtomicLong(0);
        idToChatRoom.put(id.getAndIncrement(), new ChatRoom(id.longValue(), "mainChatRoom", new HashSet<>()));
    }

    private AtomicLong id;
    private String marshallFilePath = "idToChatRoom.xml";
    @XmlElement
    private Map<Long, ChatRoom> idToChatRoom = new ConcurrentHashMap<>();

    public void putToInitialRoom(User user) {
        idToChatRoom.get(0L).addMember(user);
    }

    public ChatRoom getMainChatRoom() {
        return idToChatRoom.get(0L);
    }

    public Set<ChatRoom> getChatRoomsFor(String nickname) {
        return idToChatRoom.values().stream().filter(room -> room.isMemberPresent(nickname)).collect(Collectors.toSet());
    }

    public boolean createChatRoom(ChatRoom chatRoom) {
        if (idToChatRoom.values().contains(chatRoom)) {
            chatRoom.setId(-1);
            return false;
        }
        idToChatRoom.put(id.getAndIncrement(), chatRoom);
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
