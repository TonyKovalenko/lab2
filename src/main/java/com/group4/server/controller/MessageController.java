package com.group4.server.controller;

//import com.group4.server.model.containers.ChatRoomsContainer;
//import com.group4.server.model.containers.UserStreamContainer;
import com.group4.server.model.containers.ChatInvitationsContainer;
import com.group4.server.model.containers.ChatRoomsContainer;
import com.group4.server.model.containers.UserStreamContainer;
import com.group4.server.model.entities.ChatRoom;
import com.group4.server.model.entities.User;
import com.group4.server.model.message.handlers.ChatRoomCreationHandler;
import com.group4.server.model.message.handlers.RegistrationAuthorizationHandler;
import com.group4.server.model.message.types.*;
import com.group4.server.model.message.wrappers.MessageWrapper;

import javax.xml.bind.*;
import java.io.*;
import java.net.Socket;
import java.util.List;

public class MessageController {

    private static Class<?>[] clazzes = {
            MessageWrapper.class,
            PingMessage.class,
            AuthorizationRequest.class,
            User.class,
            RegistrationRequest.class,
            RegistrationResponse.class,
            ChatRoomCreationRequest.class
    };

    private JAXBContext context;
    private Marshaller marshaller;
    private Unmarshaller unmarshaller;
    private Socket socket;

    MessageController(Socket socket) throws JAXBException {
        this.socket = socket;
        this.context = JAXBContext.newInstance(clazzes);
        this.marshaller = context.createMarshaller();
        this.unmarshaller = context.createUnmarshaller();
    }

    void sendResponse(TransmittableMessage message, PrintWriter out) {
        StringWriter stringWriter = new StringWriter();
        try {
            marshaller.marshal(new MessageWrapper(message), stringWriter);
        } catch (JAXBException ex) {
            //log.error("Exception happened", ex);
        }
        out.println(stringWriter.toString());
    }

    void handle() {
        BufferedReader in;
        PrintWriter out;
        StringReader stringReader;
        MessageWrapper requestMessage;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            stringReader = new StringReader(in.readLine());
            requestMessage = (MessageWrapper) unmarshaller.unmarshal(stringReader);
        } catch (IOException | JAXBException ex) {
            //log.error("Exception happened", ex);
            ex.printStackTrace();
            return;
        }

        switch (requestMessage.getMessageType()) {
            case REGISTRATION_REQUEST:
                RegistrationRequest registrationRequest = (RegistrationRequest) requestMessage.getEncapsulatedMessage();
                RegistrationResponse registrationResponse = RegistrationAuthorizationHandler.INSTANCE.handle(registrationRequest);
                if (registrationResponse.isRegistrationSuccessful()) {
                    User user = new User(registrationRequest.getUserNickname(), registrationRequest.getPassword(), registrationRequest.getFullName());
                    UserStreamContainer.INSTANCE.putStream(user.getNickname(), out);
                    ChatRoomsContainer.INSTANCE.putToInitialRoom(user);
                }
                sendResponse(registrationResponse, out);
                break;
            case AUTHORIZATION_REQUEST:
                AuthorizationRequest authorizationRequest = (AuthorizationRequest) requestMessage.getEncapsulatedMessage();
                AuthorizationResponse authorizationResponse = RegistrationAuthorizationHandler.INSTANCE.handle(authorizationRequest);
                if (authorizationResponse.isConfirmed()) {
                    User user = RegistrationAuthorizationHandler.INSTANCE.getUser(authorizationRequest.getUserNickname());
                    UserStreamContainer.INSTANCE.putStream(user.getNickname(), out);
                    ChatRoomsContainer.INSTANCE.putToInitialRoom(user);
                }
                sendResponse(authorizationResponse, out);
                break;
            case CHAT_CREATION_REQUEST:
                ChatRoomCreationRequest chatRoomCreationRequest = (ChatRoomCreationRequest) requestMessage.getEncapsulatedMessage();
                ChatRoomCreationResponse chatRoomCreationResponse = ChatRoomCreationHandler.INSTANCE.handle(chatRoomCreationRequest);
                if (chatRoomCreationResponse.isSuccessful()) {
                    List<User> members = chatRoomCreationResponse.getChatRoom().getMembers();
                    ChatRoom newChatRoom = chatRoomCreationResponse.getChatRoom();
                    for (User user : members) {
                        TransmittableMessage chatInvitation = new ChatInvitationMessage(newChatRoom);
                        PrintWriter userStream = UserStreamContainer.INSTANCE.getStream(user.getNickname());
                        if (userStream != null) {
                            sendResponse(chatInvitation, userStream);
                        } else {
                            ChatInvitationsContainer.INSTANCE.saveChatInvitation(user.getNickname(), newChatRoom);
                        }
                    }
                }
                sendResponse(chatRoomCreationResponse, out);
            case PING:
                break;
            case USER_DISCONNECT:
//                UserDisconnectMessage disconnectMessage = (UserDisconnectMessage) requestMessage.getEncapsulatedMessage();

        }
    }


}