package com.group4.server.model.message.handlers;

import com.group4.server.model.containers.ChatInvitationsContainer;
import com.group4.server.model.containers.ChatRoomsContainer;
import com.group4.server.model.containers.UserStreamContainer;
import com.group4.server.model.entities.ChatRoom;
import com.group4.server.model.entities.User;
import com.group4.server.model.message.types.*;
import com.group4.server.model.message.wrappers.MessageWrapper;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

public enum ChatRoomProcessorHandler {

    INSTANCE;

    public <T extends ChatRoomCreationRequest> ChatRoomCreationResponse handle(T chatCreationRequest) {
        ChatRoom newChatRoom = chatCreationRequest.getChatRoom();
        boolean creationIsSuccessful = ChatRoomsContainer.INSTANCE.createChatRoom(newChatRoom);
        return new ChatRoomCreationResponse(creationIsSuccessful, newChatRoom);
    }

    public <T extends ChatUpdateMessage> ChatUpdateMessageResponse handle(T chatUpdateMessage, Marshaller marshaller) {
        ChatRoom roomToUpdate = ChatRoomsContainer.INSTANCE.getChatRoomById(chatUpdateMessage.getChatRoomId());
        long roomId = chatUpdateMessage.getChatRoomId();
        if (roomToUpdate == null) {
            return new ChatUpdateMessageResponse(roomId, false);
        }
        List<User> usersToAdd = chatUpdateMessage.getMembersToAdd();
        List<User> usersToDelete = chatUpdateMessage.getMembersToDelete();
        String newName = chatUpdateMessage.getNewName();

        boolean completed = addNewUsersToChat(roomToUpdate, usersToAdd, marshaller);
        if (!completed) {
            return new ChatUpdateMessageResponse(roomId, false);
        }
        completed = deleteUsersFromChat(roomToUpdate, usersToDelete, marshaller);
        if (!completed) {
            return  new ChatUpdateMessageResponse(roomId, false);
        }
        completed = updateChatName(roomToUpdate, newName);
        if (!completed) {
            return new ChatUpdateMessageResponse(roomId, false);
        }
        return new ChatUpdateMessageResponse(roomId, true);
    }

    private boolean addNewUsersToChat(ChatRoom room, List<User> users, Marshaller marshaller) {
        if (users == null) return true;
        StringWriter sw = new StringWriter();
        room.getMembers().addAll(users);
        for (User user : users) {
            TransmittableMessage chatInvitation = new ChatInvitationMessage(room);
            PrintWriter userStream = UserStreamContainer.INSTANCE.getStream(user.getNickname());
            if (userStream != null) {
                try {
                    marshaller.marshal(new MessageWrapper(chatInvitation), sw);
                } catch (JAXBException ex) {
                    //log.error("Exception happened", ex);
                    return false;
                }
                userStream.println(sw.toString());
            } else {
                ChatInvitationsContainer.INSTANCE.saveChatInvitation(user.getNickname(), room);
            }
            sw.getBuffer().setLength(0);
        }
        return true;
    }

    private boolean deleteUsersFromChat(ChatRoom room, List<User> users, Marshaller marshaller) {
        if (users == null) return true;
        StringWriter sw = new StringWriter();
        room.getMembers().removeAll(users);
        for (User user : users) {
            TransmittableMessage chatSuspension = new ChatSuspensionMessage(room);
            PrintWriter userStream = UserStreamContainer.INSTANCE.getStream(user.getNickname());
            if (userStream != null) {
                try {
                    marshaller.marshal(new MessageWrapper(chatSuspension), sw);
                } catch (JAXBException ex) {
                    //log.error("Exception happened", ex);
                    return false;
                }
                userStream.println(sw.toString());
            }
            sw.getBuffer().setLength(0);
        }
        return true;
    }

    private boolean updateChatName(ChatRoom room, String name) {
        if (name == null) return true;
        if ("".equals(name)) return false;
        room.setName(name);
        return true;
    }
}
