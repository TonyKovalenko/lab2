package com.group4.server.model.message.adapters;

import com.group4.server.model.entities.ChatRoom;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.HashSet;
import java.util.Set;

@XmlAccessorType(XmlAccessType.FIELD)
public class MyEntry {

    @XmlElement
    private String key;
    @XmlElement
    private Set<ChatRoom> set = new HashSet<>();

    String getKey() {
        return key;
    }

    void setKey(String value) {
        key = value;
    }

    public Set<ChatRoom> getSet() {
        return set;
    }
}
