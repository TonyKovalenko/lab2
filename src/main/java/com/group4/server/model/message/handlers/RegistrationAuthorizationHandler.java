package com.group4.server.model.message.handlers;

import com.group4.server.model.containers.ChatInvitationsContainer;
import com.group4.server.model.containers.ChatRoomsContainer;
import com.group4.server.model.entities.ChatRoom;
import com.group4.server.model.entities.User;
import com.group4.server.model.message.types.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public enum RegistrationAuthorizationHandler {

    INSTANCE;

    private ConcurrentMap<String, User> nicknameToUser;

    RegistrationAuthorizationHandler() {
        nicknameToUser = new ConcurrentHashMap<>();
    }

    public User getUser(String nickname) {
        return nicknameToUser.get(nickname);
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(nicknameToUser.values());
    }

    public <T extends ChangeCredentialsRequest> ChangeCredentialsResponse handle(T changeCredentialsRequest) {
        String userNickname = changeCredentialsRequest.getUserNickname();
        String newFullName = changeCredentialsRequest.getNewFullName();
        String newPassword = changeCredentialsRequest.getNewPassword();
        if (!nicknameToUser.containsKey(userNickname)) {
            return new ChangeCredentialsResponse(false);
        }
        nicknameToUser.computeIfPresent(userNickname, (k, v) -> v.setPassword(newPassword).setFullName(newFullName));
        return new ChangeCredentialsResponse(true, nicknameToUser.get(userNickname));
    }

    public <T extends RegistrationRequest> RegistrationResponse handle(T registrationRequest) {
        User user = new User(registrationRequest.getUserNickname(), registrationRequest.getFullName(), registrationRequest.getPassword());
        if (nicknameToUser.containsValue(user)) {
            return new RegistrationResponse(false);
        } else {
            nicknameToUser.put(user.getNickname(), user);
            return new RegistrationResponse(true);
        }
    }

    public <T extends AuthorizationRequest> AuthorizationResponse handle(T authorizationRequest) {
        String authNickname = authorizationRequest.getUserNickname();
        String authPassword = authorizationRequest.getPassword();
        User user = getUser(authNickname);
        if(user != null && authNickname.equals(user.getNickname()) && authPassword.equals(user.getPassword())) {
            Set<ChatRoom> chatRoomsWithUser = ChatRoomsContainer.INSTANCE.getChatRoomsFor(authNickname);
            chatRoomsWithUser.addAll(ChatInvitationsContainer.INSTANCE.getChatInvitationsFor(authNickname));
            ChatInvitationsContainer.INSTANCE.removeInvitations(authNickname);
            return new AuthorizationResponse(true, user, chatRoomsWithUser);
        } else {
            return new AuthorizationResponse(false);
        }
    }


}
