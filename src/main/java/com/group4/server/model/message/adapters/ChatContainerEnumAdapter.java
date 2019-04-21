package com.group4.server.model.message.adapters;

import com.group4.server.model.entities.ChatRoom;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Map;

@XmlRootElement(name = "chatRoomContainer")
@XmlAccessorType(XmlAccessType.NONE)
public class ChatContainerEnumAdapter {
    @XmlElement
    private Map<Long, ChatRoom> idToChatRoom;

    public ChatContainerEnumAdapter() {
    }

    public ChatContainerEnumAdapter(Map<Long, ChatRoom> idToChatRoom) {
        this.idToChatRoom = idToChatRoom;
    }

    public Map<Long, ChatRoom> getIdToChatRoom() {
        return idToChatRoom;
    }

    public void setIdToChatRoom(Map<Long, ChatRoom> idToChatRoom) {
        this.idToChatRoom = idToChatRoom;
    }
}
