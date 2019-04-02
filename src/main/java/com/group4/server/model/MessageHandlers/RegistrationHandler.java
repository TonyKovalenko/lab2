package com.group4.server.model.MessageHandlers;

import com.group4.server.model.entities.User;
import com.group4.server.model.MessageTypes.RegistrationRequest;
import com.group4.server.model.MessageTypes.RegistrationResponse;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public enum RegistrationHandler {

    INSTANCE;

    private ConcurrentMap<Long, User> idToUser;
    private AtomicLong userId;

    RegistrationHandler() {
        idToUser = new ConcurrentHashMap<>();
        userId = new AtomicLong(0);
    }

    public <T extends RegistrationRequest> RegistrationResponse handle(T registrationRequest) {
        User user = new User(registrationRequest.getUserNickname(), registrationRequest.getPassword(), registrationRequest.getFullName());
        if (idToUser.containsValue(user)) {
            return new RegistrationResponse(false);
        } else {
            idToUser.put(userId.incrementAndGet(), user);
            return new RegistrationResponse(true);
        }
    }
}
