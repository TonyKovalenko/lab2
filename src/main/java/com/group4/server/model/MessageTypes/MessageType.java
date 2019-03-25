package com.group4.server.model.MessageTypes;

public enum MessageType {
    AUTHORIZE(AuthorizationMessage.class.getSimpleName()),
    REGISTER(RegistrationMessage.class.getSimpleName()),
    PING(PingMessage.class.getSimpleName()),
    NEWGROUPCHAT(""),
    NEWPRIVATECHAT(""),
    TOCHAT(""),
    USERSINCHAT(""),
    CHANGECREDENTIALS(""),
    SERVERSHUTDOWN(""),
    ANSWER(AnswerMessage.class.getSimpleName());


    private String value;

    MessageType(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
}
