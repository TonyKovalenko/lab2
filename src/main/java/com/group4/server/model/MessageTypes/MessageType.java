package com.group4.server.model.MessageTypes;

import java.util.HashMap;
import java.util.Map;

public enum MessageType {
    ANSWER,
    AUTHORIZATION_REQUEST,
    AUTHORIZATION_RESPONSE,
    REGISTRATION_REQUEST,
    REGISTRATION_RESPONSE,
    PING,
    NEW_GROUPCHAT,
    NEW_PRIVATECHAT,
    TO_CHAT,
    USERS_IN_CHAT,
    CHANGE_CREDENTIALS,
    SERVER_SHUTDOWN;

    private static Map<String , MessageType> stringToMessageType = new HashMap<String, MessageType>() {{
        put(RegistrationRequest.class.getSimpleName(), REGISTRATION_REQUEST);
        put(RegistrationResponse.class.getSimpleName(), REGISTRATION_RESPONSE);
        put(AuthorizationRequest.class.getSimpleName(), AUTHORIZATION_REQUEST);
        put(AuthorizationResponse.class.getSimpleName(), AUTHORIZATION_RESPONSE);
        put(PingMessage.class.getSimpleName(), PING);
        put(ChatMessage.class.getSimpleName(), TO_CHAT);
        put(NewGroupChatMessage.class.getSimpleName(), NEW_GROUPCHAT);
        put(UsersInChatMessage.class.getSimpleName(), USERS_IN_CHAT);
    }};

    public static MessageType getMessageType(String type) throws IllegalArgumentException {
        return stringToMessageType.get(type);
    }
}
