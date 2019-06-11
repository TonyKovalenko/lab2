package com.group4.server.model.message.types;

import java.util.HashMap;
import java.util.Map;

public enum MessageType {
    ALL_USERS_REQUEST,
    ALL_USERS_RESPONSE,
    AUTHORIZATION_REQUEST,
    AUTHORIZATION_RESPONSE,
    CHANGE_CREDENTIALS_REQUEST,
    CHANGE_CREDENTIALS_RESPONSE,
    CHAT_CREATION_REQUEST,
    CHAT_CREATION_RESPONSE,
    CHAT_UPDATE,
    CHAT_UPDATE_RESPONSE,
    CHAT_SUSPENSION,
    DELETE_USER_REQUEST,
    DELETE_USER_RESPONSE,
    NEW_CHATS,
    ONLINE_LIST,
    PING,
    REGISTRATION_REQUEST,
    REGISTRATION_RESPONSE,
    SERVER_RESTART,
    SERVER_SHUTDOWN,
    SET_BAN_STATUS,
    TO_CHAT,
    USER_LOGOUT,
    USER_DISCONNECT;

    private static Map<String , MessageType> stringToMessageType = new HashMap<String, MessageType>() {{
        put(AuthorizationRequest.class.getSimpleName(), AUTHORIZATION_REQUEST);
        put(AuthorizationResponse.class.getSimpleName(), AUTHORIZATION_RESPONSE);
        put(ChatMessage.class.getSimpleName(), TO_CHAT);
        put(ChangeCredentialsRequest.class.getSimpleName(), CHANGE_CREDENTIALS_REQUEST);
        put(ChangeCredentialsResponse.class.getSimpleName(), CHANGE_CREDENTIALS_RESPONSE);
        put(ChatInvitationMessage.class.getSimpleName(), NEW_CHATS);
        put(ChatRoomCreationRequest.class.getSimpleName(), CHAT_CREATION_REQUEST);
        put(ChatRoomCreationResponse.class.getSimpleName(), CHAT_CREATION_RESPONSE);
        put(ChatSuspensionMessage.class.getSimpleName(), CHAT_SUSPENSION);
        put(ChatUpdateMessage.class.getSimpleName(), CHAT_UPDATE);
        put(ChatUpdateMessageResponse.class.getSimpleName(), CHAT_UPDATE_RESPONSE);
        put(DeleteUserRequest.class.getSimpleName(), DELETE_USER_REQUEST);
        put(DeleteUserResponse.class.getSimpleName(), DELETE_USER_RESPONSE);
        put(GetAllUsersRequest.class.getSimpleName(), ALL_USERS_REQUEST);
        put(GetAllUsersResponse.class.getSimpleName(), ALL_USERS_RESPONSE);
        put(OnlineListMessage.class.getSimpleName(), ONLINE_LIST);
        put(PingMessage.class.getSimpleName(), PING);
        put(RegistrationResponse.class.getSimpleName(), REGISTRATION_RESPONSE);
        put(RegistrationRequest.class.getSimpleName(), REGISTRATION_REQUEST);
        put(ServerRestartMessage.class.getSimpleName(), SERVER_RESTART);
        put(ServerShutdownMessage.class.getSimpleName(), SERVER_SHUTDOWN);
        put(SetBanStatusMessage.class.getSimpleName(), SET_BAN_STATUS);
        put(UserLogoutMessage.class.getSimpleName(), USER_LOGOUT);
        put(UserDisconnectMessage.class.getSimpleName(), USER_DISCONNECT);
    }};

    public static MessageType getMessageType(String type) throws IllegalArgumentException {
        return stringToMessageType.get(type);
    }
}
