package com.group4.server.model.MessageTypes;

import java.util.HashMap;
import java.util.Map;

public enum MessageType {
    AUTHORIZE,
    REGISTER,
    PING,
    NEWGROUPCHAT,
    NEWPRIVATECHAT,
    TOCHAT,
    USERSINCHAT,
    CHANGECREDENTIALS,
    SERVERSHUTDOWN,
    ANSWER;

    private static Map<String , MessageType> stringToMessageType = new HashMap<String, MessageType>() {{
        put(AuthorizationMessage.class.getSimpleName(), AUTHORIZE);
        put(PingMessage.class.getSimpleName(), PING);
        put(RegistrationMessage.class.getSimpleName(), REGISTER);
        put(AnswerMessage.class.getSimpleName(), ANSWER);
    }};

    public static MessageType getMessageType(String type) throws IllegalArgumentException {
        return stringToMessageType.get(type);
    }
}
