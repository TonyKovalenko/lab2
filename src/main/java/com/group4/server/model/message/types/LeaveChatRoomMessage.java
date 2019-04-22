package com.group4.server.model.message.types;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class LeaveChatRoomMessage implements TransmittableMessage {
    @XmlElement
    private long chatRoomId;

    @XmlElement
    private String userNickname;

    public LeaveChatRoomMessage() {
    }

    public LeaveChatRoomMessage(long chatRoomId, String userNickname) {
        this.chatRoomId = chatRoomId;
        this.userNickname = userNickname;
    }

    public long getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(long chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }
}
