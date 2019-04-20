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
    USERS_IN_CHAT,
    CHANGE_CREDENTIALS_REQUEST,
    CHANGE_CREDENTIALS_RESPONSE,
    UPDATE_GROUP_CHAT,
    CHAT_CREATION_REQUEST,
    CHAT_CREATION_RESPONSE,
    ALL_USERS_REQUEST,
    ALL_USERS_RESPONSE,
    USER_DISCONNECT,
    PING,
    SERVER_SHUTDOWN;

    private static Map<String , MessageType> stringToMessageType = new HashMap<String, MessageType>() {{
        put(RegistrationRequest.class.getSimpleName(), REGISTRATION_REQUEST);
        put(RegistrationResponse.class.getSimpleName(), REGISTRATION_RESPONSE);
        put(AuthorizationRequest.class.getSimpleName(), AUTHORIZATION_REQUEST);
        put(AuthorizationResponse.class.getSimpleName(), AUTHORIZATION_RESPONSE);
        put(ChatMessage.class.getSimpleName(), TO_CHAT);
        put(ChatInvitationMessage.class.getSimpleName(), NEW_CHATS);
        put(UpdateChatMessage.class.getSimpleName(), UPDATE_GROUP_CHAT);
        put(UsersInChatMessage.class.getSimpleName(), USERS_IN_CHAT);
        put(ChangeCredentialsRequest.class.getSimpleName(), CHANGE_CREDENTIALS_REQUEST);
        put(ChangeCredentialsResponse.class.getSimpleName(), CHANGE_CREDENTIALS_RESPONSE);
        put(ChatRoomCreationRequest.class.getSimpleName(), CHAT_CREATION_REQUEST);
        put(ChatRoomCreationResponse.class.getSimpleName(), CHAT_CREATION_RESPONSE);
        put(GetAllUsersRequest.class.getSimpleName(), ALL_USERS_REQUEST);
        put(GetAllUsersResponse.class.getSimpleName(), ALL_USERS_RESPONSE);
        put(PingMessage.class.getSimpleName(), PING);
        put(UserDisconnectMessage.class.getSimpleName(), USER_DISCONNECT);
    }};

    public static MessageType getMessageType(String type) throws IllegalArgumentException {
        return stringToMessageType.get(type);
    }
}
