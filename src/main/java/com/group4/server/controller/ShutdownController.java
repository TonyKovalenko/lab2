package com.group4.server.controller;

import com.group4.server.model.containers.ChatInvitationsContainer;
import com.group4.server.model.containers.ChatRoomsContainer;
import com.group4.server.model.entities.ChatRoom;
import com.group4.server.model.entities.User;
import com.group4.server.model.message.adapters.ChatContainerEnumAdapter;
import com.group4.server.model.message.adapters.ChatInvitationsEnumAdapter;
import com.group4.server.model.message.adapters.UserDataContainerAdapter;
import com.group4.server.model.message.handlers.RegistrationAuthorizationHandler;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.util.Map;
import java.util.Set;


public enum ShutdownController {

    INSTANCE;

    private Marshaller marshaller;

    private Class<?>[] clazzes = {
            User.class,
            ChatRoom.class,
            ChatInvitationsContainer.class,
            ChatRoomsContainer.class,
            RegistrationAuthorizationHandler.class
    };

    ShutdownController() {
    }

    public void init() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(clazzes);
        context.createMarshaller();
    }

    public Marshaller getMarshaller() {
        return marshaller;
    }

    public void saveData(Marshaller marshaller) {
        try {
            marshallChatInvitations(marshaller);
            marshallChatRooms(marshaller);
            marshallUserData(marshaller);
        } catch (JAXBException ex) {
            //log.error("JAXB marshalling of container were failed" + ex);
        }
    }

    private void marshallChatInvitations(Marshaller marshaller) throws JAXBException {
        String chatInvitationsContainerFile = ChatInvitationsContainer.INSTANCE.getMarshallingFilePath();
        Map<String, Set<ChatRoom>> chatInvitationsContainer = ChatInvitationsContainer.INSTANCE.getContainer();
        ChatInvitationsEnumAdapter chatInvitationsEnumAdapter = new ChatInvitationsEnumAdapter(chatInvitationsContainer);
        marshaller.marshal(chatInvitationsEnumAdapter, new File(chatInvitationsContainerFile));
    }

    private void marshallChatRooms(Marshaller marshaller) throws JAXBException {
        String chatRoomsContainerFile = ChatRoomsContainer.INSTANCE.getMarshallingFilePath();
        Map<Long, ChatRoom> chatInvitationsContainer = ChatRoomsContainer.INSTANCE.getContainer();
        ChatContainerEnumAdapter chatInvitationsEnumAdapter = new ChatContainerEnumAdapter(chatInvitationsContainer);
        marshaller.marshal(chatInvitationsEnumAdapter, new File(chatRoomsContainerFile));
    }

    private void marshallUserData(Marshaller marshaller) throws JAXBException {
        String userDataContainerFile = RegistrationAuthorizationHandler.INSTANCE.getMarshallingFilePath();
        Map<String, User> userDataContainer = RegistrationAuthorizationHandler.INSTANCE.getContainer();
        UserDataContainerAdapter chatInvitationsEnumAdapter = new UserDataContainerAdapter(userDataContainer);
        marshaller.marshal(chatInvitationsEnumAdapter, new File(userDataContainerFile));
    }
}
