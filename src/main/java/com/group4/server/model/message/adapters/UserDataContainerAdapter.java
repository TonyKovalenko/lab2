package com.group4.server.model.message.adapters;

import com.group4.server.model.entities.User;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@XmlRootElement(name = "userDataContainer")
@XmlAccessorType(XmlAccessType.NONE)
public class UserDataContainerAdapter {

    @XmlElement
    private Map<String, User> nicknameToUser;

    public UserDataContainerAdapter(Map<String, User> nicknameToUser) {
        this.nicknameToUser = nicknameToUser;
    }

    public UserDataContainerAdapter() {
    }

    public Map<String, User> getNicknameToUser() {
        return new ConcurrentHashMap<>(nicknameToUser);
    }

    public void setNicknameToUser(Map<String, User> nicknameToUser) {
        this.nicknameToUser = nicknameToUser;
    }
}
