package com.group4.server.model.MessageTypes;

import java.util.HashMap;
import java.util.Map;

public enum MessageType {
    ANSWER,
    AUTHORIZE,
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
        put(AuthorizationMessage.class.getSimpleName(), AUTHORIZE);
        put(PingMessage.class.getSimpleName(), PING);
    }};

    public static MessageType getMessageType(String type) throws IllegalArgumentException {
        return stringToMessageType.get(type);
    }
}
