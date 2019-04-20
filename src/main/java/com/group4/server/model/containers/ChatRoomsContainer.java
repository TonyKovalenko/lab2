package com.group4.server.model.containers;

import com.group4.server.model.entities.ChatRoom;
import com.group4.server.model.entities.User;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public enum ChatRoomsContainer {

    INSTANCE;

    ChatRoomsContainer() {
        id = new AtomicLong(0);
        idToChatRoom.put(id.getAndIncrement(), new ChatRoom(id.longValue(), "mainChatRoom", new ArrayList<>()));
    }

    private AtomicLong id;
    private Map<Long, ChatRoom> idToChatRoom = new ConcurrentHashMap<>();

    public void putToInitialRoom(User user) {
        idToChatRoom.get(0L).addMember(user);
    }

    public ChatRoom getMainChatRoom() {
        return idToChatRoom.get(0);
    }

    public Set<ChatRoom> getChatRoomsFor(String nickname) {
        return idToChatRoom.values().stream().filter(room -> room.isMemberPresent(nickname)).collect(Collectors.toSet());
    }

    public boolean createChatRoom(ChatRoom chatRoom) {
        if(idToChatRoom.values().contains(chatRoom)) {
            chatRoom.setId(-1);
            return false;
        }
        idToChatRoom.put(id.getAndIncrement(), chatRoom);
        chatRoom.setId(id.longValue());
        return true;
    }
}
