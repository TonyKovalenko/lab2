package com.group4.server.model.message.handlers;

import com.group4.server.model.containers.ChatRoomsContainer;
import com.group4.server.model.entities.ChatRoom;
import com.group4.server.model.message.types.ChatRoomCreationRequest;
import com.group4.server.model.message.types.ChatRoomCreationResponse;

public enum ChatRoomCreationHandler {

    INSTANCE;

    public <T extends ChatRoomCreationRequest> ChatRoomCreationResponse handle(T chatCreationRequest) {
        ChatRoom newChatRoom = chatCreationRequest.getChatRoom();
        boolean creationIsSuccessful = ChatRoomsContainer.INSTANCE.createChatRoom(newChatRoom);
        return new ChatRoomCreationResponse(creationIsSuccessful);
    }

}
