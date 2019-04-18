package com.group4.server.model.message.types;

import java.util.HashMap;
import java.util.Map;

public enum MessageType {
    AUTHORIZATION_REQUEST,
    AUTHORIZATION_RESPONSE,
    REGISTRATION_REQUEST,
    REGISTRATION_RESPONSE,
    PING,
    NEW_GROUPCHAT,
    NEW_PRIVATECHAT,
    TO_CHAT,
    USERS_IN_CHAT,
    CHANGE_CREDENTIALS_REQUEST,
    CHANGE_CREDENTIALS_RESPONSE,
    UPDATE_GROUP_CHAT,
    SERVER_SHUTDOWN,
    USER_DISCONNECT;

    private static Map<String , MessageType> stringToMessageType = new HashMap<String, MessageType>() {{
        put(RegistrationRequest.class.getSimpleName(), REGISTRATION_REQUEST);
        put(RegistrationResponse.class.getSimpleName(), REGISTRATION_RESPONSE);
        put(AuthorizationRequest.class.getSimpleName(), AUTHORIZATION_REQUEST);
        put(AuthorizationResponse.class.getSimpleName(), AUTHORIZATION_RESPONSE);
        put(PingMessage.class.getSimpleName(), PING);
        put(ChatMessage.class.getSimpleName(), TO_CHAT);
        put(NewGroupChatMessage.class.getSimpleName(), NEW_GROUPCHAT);
        put(UsersInChatMessage.class.getSimpleName(), USERS_IN_CHAT);
        put(ChangeCredentialsRequest.class.getSimpleName(), CHANGE_CREDENTIALS_REQUEST);
        put(ChangeCredentialsResponse.class.getSimpleName(), CHANGE_CREDENTIALS_RESPONSE);
        put(UpdateGroupChatMessage.class.getSimpleName(), UPDATE_GROUP_CHAT);
       // put(UserDisconnectMessage.class.getSimpleName(), USER_DISCONNECT);
    }};

    public static MessageType getMessageType(String type) throws IllegalArgumentException {
        return stringToMessageType.get(type);
    }
}
