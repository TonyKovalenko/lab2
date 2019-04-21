package com.group4.server.controller;

import com.group4.server.model.containers.ChatRoomsContainer;
import com.group4.server.model.containers.UserStreamContainer;
import com.group4.server.model.containers.ChatInvitationsContainer;
import com.group4.server.model.entities.ChatRoom;
import com.group4.server.model.entities.User;
import com.group4.server.model.message.handlers.ChatRoomProcessorHandler;
import com.group4.server.model.message.handlers.RegistrationAuthorizationHandler;
import com.group4.server.model.message.types.*;
import com.group4.server.model.message.wrappers.MessageWrapper;
import org.apache.log4j.Logger;

import javax.xml.bind.*;
import java.io.*;
import java.net.Socket;
import java.util.Set;

class MessageController {

    private static final Logger log = Logger.getLogger(MessageController.class);
    private static Class<?>[] clazzes = {
            User.class,
            ChatRoom.class,
            GetAllUsersRequest.class,
            GetAllUsersResponse.class,
            AuthorizationRequest.class,
            AuthorizationResponse.class,
            ChangeCredentialsRequest.class,
            ChangeCredentialsResponse.class,
            ChatInvitationMessage.class,
            ChatMessage.class,
            ChatRoomCreationRequest.class,
            ChatRoomCreationResponse.class,
            ChatUpdateMessageRequest.class,
            ChatUpdateMessageResponse.class,
            OnlineListMessage.class,
            PingMessage.class,
            RegistrationRequest.class,
            RegistrationResponse.class,
            UserLogoutMessage.class,
            UserDisconnectMessage.class,
            MessageWrapper.class
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

    private void sendResponse(TransmittableMessage message, PrintWriter out, StringWriter stringWriter) {
        try {
            marshaller.marshal(new MessageWrapper(message), stringWriter);
        } catch (JAXBException ex) {
            log.error("Exception happened while sending a response", ex);
        }
        out.println(stringWriter.toString());
        stringWriter.getBuffer().setLength(0);
    }

    private void broadcastToOnlineUsers(TransmittableMessage message, StringWriter stringWriter) {
        Set<PrintWriter> userStreams = UserStreamContainer.INSTANCE.getCurrentUserStreams();
        try {
            marshaller.marshal(new MessageWrapper(message), stringWriter);
        } catch (JAXBException ex) {
            log.error("Exception happened while broadcasting a message", ex);
            return;
        }
        for (PrintWriter out : userStreams) {
            out.println(stringWriter.toString());
        }
        log.info("Broadcast message sent.");
        stringWriter.getBuffer().setLength(0);
    }

    void handle() {
        BufferedReader in;
        PrintWriter out;
        StringWriter stringWriter;
        StringReader stringReader;
        MessageWrapper requestMessage;
        boolean isConnected = true;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            stringWriter = new StringWriter();
        } catch (IOException ex) {
            log.error("Exception happened while creating I/O streams during request handling", ex);
            ex.printStackTrace();
            return;
        }

        while (isConnected) {
            try {
                stringReader = new StringReader(in.readLine());
                requestMessage = (MessageWrapper) unmarshaller.unmarshal(stringReader);
            } catch (IOException | JAXBException ex) {
                log.error("Exception happened while reading user request message", ex);
                continue;
            }
            switch (requestMessage.getMessageType()) {
                case REGISTRATION_REQUEST:
                    RegistrationRequest registrationRequest = (RegistrationRequest) requestMessage.getEncapsulatedMessage();
                    RegistrationResponse registrationResponse = RegistrationAuthorizationHandler.INSTANCE.handle(registrationRequest);
                    log.info("New user registration from:[" + registrationRequest.getUserNickname() + "]");
                    if (registrationResponse.isRegistrationSuccessful()) {
                        User user = new User(registrationRequest.getUserNickname(), registrationRequest.getPassword(), registrationRequest.getFullName());
                        UserStreamContainer.INSTANCE.putStream(user.getNickname(), out);
                        ChatRoomsContainer.INSTANCE.putToInitialRoom(user);
                        log.info("Confirmed user registration from:[" + user.getNickname() + "]");
                    } else {
                        log.info("Denied user registration");
                    }
                    sendResponse(registrationResponse, out, stringWriter);
                    break;
                case AUTHORIZATION_REQUEST:
                    AuthorizationRequest authorizationRequest = (AuthorizationRequest) requestMessage.getEncapsulatedMessage();
                    log.info("New user authorization from:[" + authorizationRequest.getUserNickname() + "]");
                    AuthorizationResponse authorizationResponse = RegistrationAuthorizationHandler.INSTANCE.handle(authorizationRequest);
                    if (authorizationResponse.isConfirmed()) {
                        User user = RegistrationAuthorizationHandler.INSTANCE.getUser(authorizationRequest.getUserNickname());
                        UserStreamContainer.INSTANCE.putStream(user.getNickname(), out);
                        ChatRoomsContainer.INSTANCE.putToInitialRoom(user);
                        Set<User> onlineUsers = UserStreamContainer.INSTANCE.getCurrentUsers();
                        TransmittableMessage onlineList = new OnlineListMessage(onlineUsers);
                        StringWriter sw = new StringWriter();
                        sendResponse(authorizationResponse, out, stringWriter);
                        broadcastToOnlineUsers(onlineList, sw);
                        log.info("Successful user authorization from:[" + authorizationRequest.getUserNickname() + "]");
                        break;
                    } else {
                        log.info("Denied user authorization");
                    }
                    sendResponse(authorizationResponse, out, stringWriter);
                    break;
                case CHAT_CREATION_REQUEST:
                    ChatRoomCreationRequest chatRoomCreationRequest = (ChatRoomCreationRequest) requestMessage.getEncapsulatedMessage();
                    log.info("New chat creation request");
                    ChatRoomCreationResponse chatRoomCreationResponse = ChatRoomProcessorHandler.INSTANCE.handle(chatRoomCreationRequest);
                    if (chatRoomCreationResponse.isSuccessful()) {
                        Set<User> members = chatRoomCreationResponse.getChatRoom().getMembers();
                        ChatRoom newChatRoom = chatRoomCreationResponse.getChatRoom();
                        for (User user : members) {
                            TransmittableMessage chatInvitation = new ChatInvitationMessage(newChatRoom);
                            PrintWriter userStream = UserStreamContainer.INSTANCE.getStream(user.getNickname());
                            if (userStream != null) {
                                sendResponse(chatInvitation, userStream, stringWriter);
                            } else {
                                ChatInvitationsContainer.INSTANCE.saveChatInvitation(user.getNickname(), newChatRoom);
                            }
                        }
                        log.info("Chat created");
                    }
                    sendResponse(chatRoomCreationResponse, out, stringWriter);
                    break;
                case TO_CHAT:
                    ChatMessage chatMessage = (ChatMessage) requestMessage.getEncapsulatedMessage();
                    log.info("New chat message  chatId:[" + chatMessage.getChatId() + "]");
                    ChatRoom targetRoom = ChatRoomsContainer.INSTANCE.getChatRoomById(chatMessage.getChatId());
                    if (targetRoom.isEmpty()) {
                        log.info("Room is empty");
                        break;
                    } else {
                        ChatRoomsContainer.INSTANCE.addMessageToChat(chatMessage.getChatId(), chatMessage);
                        for (User user : targetRoom.getMembers()) {
                            PrintWriter memberStream = UserStreamContainer.INSTANCE.getStream(user.getNickname());
                            if (memberStream != null) {
                                sendResponse(chatMessage, memberStream, stringWriter);
                            }
                        }
                    }
                    break;
                case ALL_USERS_REQUEST:
                    TransmittableMessage allUsersResponse = new GetAllUsersResponse(RegistrationAuthorizationHandler.INSTANCE.getAllUsers());
                    log.info("User list requested");
                    sendResponse(allUsersResponse, out, stringWriter);
                    break;
                case CHAT_UPDATE_REQUEST:
                    ChatUpdateMessageRequest chatUpdateRequest = (ChatUpdateMessageRequest) requestMessage.getEncapsulatedMessage();
                    log.info("Chat update requested  chatId:[" + chatUpdateRequest.getChatRoomId() + "]");
                    TransmittableMessage chatUpdateResponse = ChatRoomProcessorHandler.INSTANCE.handle(chatUpdateRequest, marshaller);
                    sendResponse(chatUpdateResponse, out, stringWriter);
                case PING:
                    PingMessage pingRequest = (PingMessage) requestMessage.getEncapsulatedMessage();
                    TransmittableMessage pingMessageResponse = new PingMessage(pingRequest.getUserNickname(), true);
                    sendResponse(pingMessageResponse, out, stringWriter);
                    break;
                case CHANGE_CREDENTIALS_REQUEST:
                    ChangeCredentialsRequest changeCredentialsRequest = (ChangeCredentialsRequest) requestMessage.getEncapsulatedMessage();
                    log.info("Change credentials requested from:[" + changeCredentialsRequest.getUserNickname() + "]");
                    ChangeCredentialsResponse changeCredentialsResponse = RegistrationAuthorizationHandler.INSTANCE.handle(changeCredentialsRequest);
                    sendResponse(changeCredentialsResponse, out, stringWriter);
                    break;
                case USER_DISCONNECT:
                    isConnected = false;
                    log.info("User disconnected");
                    break;
                case USER_LOGOUT:
                    UserLogoutMessage logoutMessage = (UserLogoutMessage) requestMessage.getEncapsulatedMessage();
                    log.info("User logout from:[" + logoutMessage.getNickname()+ "]");
                    UserStreamContainer.INSTANCE.deleteUser(logoutMessage.getNickname());
                    Set<User> onlineUsers = UserStreamContainer.INSTANCE.getCurrentUsers();
                    TransmittableMessage onlineList = new OnlineListMessage(onlineUsers);
                    StringWriter sw = new StringWriter();
                    broadcastToOnlineUsers(onlineList, sw);
                    break;
            }
        }
    }
}