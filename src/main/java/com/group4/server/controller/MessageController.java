package com.group4.server.controller;

import com.group4.server.model.containers.ChatInvitationsContainer;
import com.group4.server.model.containers.ChatRoomsContainer;
import com.group4.server.model.containers.UserStreamContainer;
import com.group4.server.model.entities.ChatRoom;
import com.group4.server.model.entities.User;
import com.group4.server.model.message.handlers.ChatRoomProcessorHandler;
import com.group4.server.model.message.handlers.RegistrationAuthorizationHandler;
import com.group4.server.model.message.types.*;
import com.group4.server.model.message.wrappers.MessageWrapper;
import org.apache.log4j.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.net.Socket;
import java.util.Optional;
import java.util.Set;

/**
 * MessageController class is primary for handling the connections to a server,
 * extracting sent messages from the I/O streams and sending a response back.
 * <p>
 * Messages are contained as XML formatted text, and the main processing is done
 * with the help of JAXB classes and methods.
 *
 * @author Nadia Volyk, Anton Kovalenko
 * @see ServerController
 * @since 05-06-19
 */
public class MessageController implements MessageControllable {

    private static final Logger log = Logger.getLogger(MessageController.class);

    //variable that holds classes for creating a context by JAXB marshaller or unmarshaller
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
            ChatSuspensionMessage.class,
            ChatUpdateMessage.class,
            ChatUpdateMessageResponse.class,
            DeleteUserRequest.class,
            DeleteUserResponse.class,
            OnlineListMessage.class,
            PingMessage.class,
            RegistrationRequest.class,
            RegistrationResponse.class,
            SetBanStatusMessage.class,
            UserLogoutMessage.class,
            UserDisconnectMessage.class,
            MessageWrapper.class,
            ServerRestartMessage.class,
            ServerShutdownMessage.class
    };

    private JAXBContext context;
    private Marshaller marshaller;
    private Unmarshaller unmarshaller;
    private Socket socket;

    public MessageController() {
        try {
            this.context = JAXBContext.newInstance(clazzes);
            this.marshaller = context.createMarshaller();
            this.unmarshaller = context.createUnmarshaller();
        } catch (JAXBException ex) {
            log.error("Message controller context was not initialized correctly" + ex);
        }
    }

    /**
     * Constructor, for creating an instance of the MessageController and further
     * processing of the message, contained inside of the accepted socket I/O stream.
     *
     * @param socket, which was accepted in {@link ServerController#run()} method.
     */
    MessageController(Socket socket) {
        this();
        this.socket = socket;
    }

    /**
     * Method to send a TransmittableMessage instance to specified I/O stream.
     *
     * @param message      message to send
     * @param out          I/O stream, to send the response to
     * @param stringWriter stream, to wrap the XML string of response message
     * @see TransmittableMessage
     * @see MessageWrapper
     */
    public void sendResponse(TransmittableMessage message, PrintWriter out, StringWriter stringWriter) {
        try {
            marshaller.marshal(new MessageWrapper(message), stringWriter);
        } catch (JAXBException ex) {
            log.error("Exception happened while sending a response", ex);
        }
        out.println(stringWriter.toString());
        stringWriter.getBuffer().setLength(0);
    }


    /**
     * Method to broadcast a TransmittableMessage instance to all currently online users.
     *
     * @param message      message to send
     * @param stringWriter stream, to wrap the XML string of response message
     * @see TransmittableMessage
     * @see MessageWrapper
     */
    public void broadcastToOnlineUsers(TransmittableMessage message, StringWriter stringWriter) {
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

    /**
     * Method that does the major handling of the received message:
     * <p>
     * - extracting message from the I/O stream
     * - resolving the encapsulated message type
     * - handling it in a proper way
     * - forming and sending a response (if needed)
     * <p>
     * As one instance of {@link MessageController} is associated with single user
     * this method is running in an infinite loop, and serves the single user connection
     * until user decides to disconnect.
     */
    public void handle() {
        BufferedReader in;
        PrintWriter out;
        StringWriter stringWriter;
        StringReader stringReader = new StringReader("");
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
                break;
            }
            switch (requestMessage.getMessageType()) {
                case REGISTRATION_REQUEST:
                    RegistrationRequest registrationRequest = (RegistrationRequest) requestMessage.getEncapsulatedMessage();
                    RegistrationResponse registrationResponse = RegistrationAuthorizationHandler.INSTANCE.handle(registrationRequest);
                    log.info("New user registration from:[" + registrationRequest.getUserNickname() + "]");
                    if (registrationResponse.isRegistrationSuccessful()) {
                        User user = new User(registrationRequest.getUserNickname(), registrationRequest.getPassword(), registrationRequest.getFullName());
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
                        sendResponse(authorizationResponse, out, stringWriter);
                        broadcastToOnlineUsers(onlineList, new StringWriter());
                        log.info("Successful user authorization from:[" + authorizationRequest.getUserNickname() + "]");
                        break;
                    } else {
                        log.info("Denied user authorization");
                    }
                    sendResponse(authorizationResponse, out, stringWriter);
                    break;
                case CHAT_CREATION_RESPONSE:
                    ChatRoomCreationResponse privateChatRoomCreationResponse = (ChatRoomCreationResponse) requestMessage.getEncapsulatedMessage();
                    if (privateChatRoomCreationResponse.isSuccessful()) {
                        ChatRoomCreationResponse privateChatRoomCreationFinalResponse = ChatRoomProcessorHandler.INSTANCE.handle(privateChatRoomCreationResponse);
                        if (privateChatRoomCreationFinalResponse.isSuccessful()) {
                            Set<User> privateMembers = privateChatRoomCreationFinalResponse.getChatRoom().getMembers();
                            ChatRoom newChatRoom = privateChatRoomCreationFinalResponse.getChatRoom();
                            for (User user : privateMembers) {
                                PrintWriter userStream = UserStreamContainer.INSTANCE.getStream(user.getNickname());
                                TransmittableMessage chatInvitation = new ChatInvitationMessage(newChatRoom);
                                if (userStream != null) {
                                    sendResponse(chatInvitation, userStream, stringWriter);
                                } else {
                                    ChatInvitationsContainer.INSTANCE.saveChatInvitation(user.getNickname(), newChatRoom);
                                }
                            }
                            log.info("Private chat created");
                        }
                    }
                    break;
                case CHAT_CREATION_REQUEST:
                    ChatRoomCreationRequest chatRoomCreationRequest = (ChatRoomCreationRequest) requestMessage.getEncapsulatedMessage();
                    log.info("New chat creation request");
                    if (chatRoomCreationRequest.getChatRoom().isPrivate()) {
                        String otherUserNickname = "";
                        ChatRoom privateRoom = chatRoomCreationRequest.getChatRoom();
                        Optional<User> otherUser = privateRoom.getMembers().stream().filter(e -> !e.getNickname().equals(privateRoom.getAdminNickname())).findFirst();
                        if (otherUser.isPresent()) {
                            otherUserNickname = otherUser.get().getNickname();
                        }
                        PrintWriter otherUserStream = UserStreamContainer.INSTANCE.getStream(otherUserNickname);
                        if (otherUserStream != null) {
                            sendResponse(chatRoomCreationRequest, otherUserStream, stringWriter);
                            break;
                        } else {
                            String adminNickname = privateRoom.getAdminNickname();
                            PrintWriter chatAdminStream = UserStreamContainer.INSTANCE.getStream(adminNickname);
                            if (chatAdminStream != null) {
                                sendResponse(new ChatRoomCreationResponse(false, privateRoom), chatAdminStream, stringWriter);
                            }
                        }
                        break;
                    }
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

                    boolean senderBanned = RegistrationAuthorizationHandler.INSTANCE.getUser(chatMessage.getSender()).isBanned();
                    if (targetRoom.getId() == 0 && senderBanned) {
                        break;
                    }

                    if (targetRoom.isEmpty()) {
                        log.info("Message sent to an empty room:[ " + chatMessage.getChatId() + "]");
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
                    log.info("User list was requested");
                    sendResponse(allUsersResponse, out, stringWriter);
                    break;
                case CHAT_UPDATE:
                    ChatUpdateMessage chatUpdate = (ChatUpdateMessage) requestMessage.getEncapsulatedMessage();
                    ChatRoom updatedChatRoom = ChatRoomsContainer.INSTANCE.getChatRoomById(chatUpdate.getChatRoomId());
                    ChatRoomProcessorHandler.INSTANCE.handle(chatUpdate, marshaller);
                    log.info("Chat update requested  chatId:[" + chatUpdate.getChatRoomId() + "]");
                    Set<User> members = updatedChatRoom.getMembers();
                    for (User user : members) {
                        PrintWriter userStream = UserStreamContainer.INSTANCE.getStream(user.getNickname());
                        if (userStream != null) {
                            sendResponse(chatUpdate, userStream, stringWriter);
                        }
                    }
                    break;
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
                case SET_BAN_STATUS:
                    SetBanStatusMessage setBanStatus = (SetBanStatusMessage) requestMessage.getEncapsulatedMessage();
                    log.info("User ban message for [" + setBanStatus.getUserNickname() + "]");
                    RegistrationAuthorizationHandler.INSTANCE.getUser(setBanStatus.getUserNickname()).setBanned(setBanStatus.isBanned());
                    PrintWriter adminStream = UserStreamContainer.INSTANCE.getStream("admin");
                    if (adminStream != null) {
                        sendResponse(setBanStatus, adminStream, stringWriter);
                    }
                    PrintWriter bannedUserStream = UserStreamContainer.INSTANCE.getStream(setBanStatus.getUserNickname());
                    if (bannedUserStream != null) {
                        sendResponse(setBanStatus, bannedUserStream, stringWriter);
                    }
                    break;
                case DELETE_USER_REQUEST:
                    DeleteUserRequest userToDelete = (DeleteUserRequest) requestMessage.getEncapsulatedMessage();
                    ChatInvitationsContainer.INSTANCE.removeInvitations(userToDelete.getUserNickname());
                    ChatRoomsContainer.INSTANCE.deleteUserFromChatRooms(userToDelete.getUserNickname());
                    boolean wasDeleted = RegistrationAuthorizationHandler.INSTANCE.deleteUser(userToDelete.getUserNickname());
                    adminStream = UserStreamContainer.INSTANCE.getStream("admin");
                    TransmittableMessage deleteUserResponse = new DeleteUserResponse(userToDelete.getUserNickname(), wasDeleted);
                    if (wasDeleted) {
                        PrintWriter userStream = UserStreamContainer.INSTANCE.getStream(userToDelete.getUserNickname());
                        if (userStream != null) {
                            sendResponse(deleteUserResponse, userStream, stringWriter);
                        }
                        UserStreamContainer.INSTANCE.deleteUser(userToDelete.getUserNickname());
                    }
                    if (adminStream != null) {
                        sendResponse(deleteUserResponse, adminStream, stringWriter);
                    }
                    break;
                case USER_DISCONNECT:
                    isConnected = false;
                    log.info("User disconnected");
                    break;
                case USER_LOGOUT:
                    UserLogoutMessage logoutMessage = (UserLogoutMessage) requestMessage.getEncapsulatedMessage();
                    log.info("User logout from:[" + logoutMessage.getNickname() + "]");
                    UserStreamContainer.INSTANCE.deleteUser(logoutMessage.getNickname());
                    Set<User> onlineUsers = UserStreamContainer.INSTANCE.getCurrentUsers();
                    TransmittableMessage onlineList = new OnlineListMessage(onlineUsers);
                    broadcastToOnlineUsers(onlineList, new StringWriter());
                    break;
            }
        }
        try {
            in.close();
            out.close();
            stringReader.close();
            stringWriter.close();
        } catch (
                IOException ex) {
            log.error("MessageController resources were not closed properly. " + ex.getMessage());
        }
    }
}