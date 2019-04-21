package com.group4.server.model.message.types;

import java.util.HashMap;
import java.util.Map;

public enum MessageType {
    AUTHORIZATION_REQUEST,
    AUTHORIZATION_RESPONSE,
    REGISTRATION_REQUEST,
    REGISTRATION_RESPONSE,
    NEW_CHATS,
    TO_CHAT,
    ONLINE_LIST,
    CHANGE_CREDENTIALS_REQUEST,
    CHANGE_CREDENTIALS_RESPONSE,
    CHAT_CREATION_REQUEST,
    CHAT_CREATION_RESPONSE,
    CHAT_UPDATE_REQUEST,
    CHAT_UPDATE_RESPONSE,
    ALL_USERS_REQUEST,
    ALL_USERS_RESPONSE,
    USER_LOGOUT,
    USER_DISCONNECT,
    PING,
    CHAT_SUSPENSION,
    SERVER_SHUTDOWN;

    private static Map<String , MessageType> stringToMessageType = new HashMap<String, MessageType>() {{
        put(RegistrationRequest.class.getSimpleName(), REGISTRATION_REQUEST);
        put(RegistrationResponse.class.getSimpleName(), REGISTRATION_RESPONSE);
        put(AuthorizationRequest.class.getSimpleName(), AUTHORIZATION_REQUEST);
        put(AuthorizationResponse.class.getSimpleName(), AUTHORIZATION_RESPONSE);
        put(ChatMessage.class.getSimpleName(), TO_CHAT);
        put(ChatInvitationMessage.class.getSimpleName(), NEW_CHATS);
        put(UsersInChatMessage.class.getSimpleName(), ONLINE_LIST);
        put(ChangeCredentialsRequest.class.getSimpleName(), CHANGE_CREDENTIALS_REQUEST);
        put(ChangeCredentialsResponse.class.getSimpleName(), CHANGE_CREDENTIALS_RESPONSE);
        put(ChatRoomCreationRequest.class.getSimpleName(), CHAT_CREATION_REQUEST);
        put(ChatRoomCreationResponse.class.getSimpleName(), CHAT_CREATION_RESPONSE);
        put(ChatUpdateMessageRequest.class.getSimpleName(), CHAT_UPDATE_REQUEST);
        put(ChatUpdateMessageResponse.class.getSimpleName(), CHAT_UPDATE_RESPONSE);
        put(GetAllUsersRequest.class.getSimpleName(), ALL_USERS_REQUEST);
        put(GetAllUsersResponse.class.getSimpleName(), ALL_USERS_RESPONSE);
        put(PingMessage.class.getSimpleName(), PING);
        put(UserLogoutMessage.class.getSimpleName(), USER_LOGOUT);
        put(UserDisconnectMessage.class.getSimpleName(), USER_DISCONNECT);
        put(ChatSuspensionMessage.class.getSimpleName(), CHAT_SUSPENSION);
    }};

    public static MessageType getMessageType(String type) throws IllegalArgumentException {
        return stringToMessageType.get(type);
    }
}
