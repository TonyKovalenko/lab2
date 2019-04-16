package com.group4.server.model.message.handlers;

//import com.group4.server.model.containers.ChatContainer;
import com.group4.server.model.entities.User;
import com.group4.server.model.message.types.AuthorizationRequest;
import com.group4.server.model.message.types.AuthorizationResponse;
import com.group4.server.model.message.types.RegistrationRequest;
import com.group4.server.model.message.types.RegistrationResponse;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public enum RegistrationAuthorizationHandler {

    INSTANCE;

    private ConcurrentMap<Long, User> idToUser;
    private AtomicLong userId;

    RegistrationAuthorizationHandler() {
        idToUser = new ConcurrentHashMap<>();
        userId = new AtomicLong(0);
    }

    public User getUser(long id) {
        return idToUser.get(id);
    }

    public <T extends RegistrationRequest> RegistrationResponse handle(T registrationRequest) {
        User user = new User(registrationRequest.getUserNickname(), registrationRequest.getPassword(), registrationRequest.getFullName());
        if (idToUser.containsValue(user)) {
            return new RegistrationResponse(false, -1);
        } else {
            idToUser.put(userId.getAndIncrement(), user);
            return new RegistrationResponse(true, userId.longValue());
        }
    }

    public <T extends AuthorizationRequest> AuthorizationResponse handle(T authorizationRequest) {
        String authNickname = authorizationRequest.getUserNickname();
        String authPassword = authorizationRequest.getPassword();
        User user = idToUser.get(authorizationRequest.getGeneratedId());
        if(user.getNickname().equals(authNickname) && user.getPassword().equals(authPassword)) {
            return new AuthorizationResponse(true, user, /*ChatContainer.INSTANCE.getMainChatRoom()*/ null);
        } else {
            return new AuthorizationResponse(false);
        }
    }


}
