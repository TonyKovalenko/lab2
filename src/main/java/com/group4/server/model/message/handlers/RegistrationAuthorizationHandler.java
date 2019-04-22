package com.group4.server.model.message.handlers;

import com.group4.server.model.containers.ChatInvitationsContainer;
import com.group4.server.model.containers.ChatRoomsContainer;
import com.group4.server.model.entities.ChatRoom;
import com.group4.server.model.entities.User;
import com.group4.server.model.message.adapters.UserDataContainerAdapter;
import com.group4.server.model.message.types.*;
import org.apache.log4j.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public enum RegistrationAuthorizationHandler {

    INSTANCE;

    private final Logger log = Logger.getLogger(RegistrationAuthorizationHandler.class);
    private String marshallFilePath = "nicknameToUser.xml";

    private Map<String, User> nicknameToUser = new ConcurrentHashMap<>();

    RegistrationAuthorizationHandler() {
        unmarshallOnStart();
    }

    private void unmarshallOnStart() {
        UserDataContainerAdapter adapter;
        try (BufferedReader br = new BufferedReader(new FileReader(new File(marshallFilePath)))) {
            JAXBContext context = JAXBContext.newInstance(UserDataContainerAdapter.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            adapter = (UserDataContainerAdapter) unmarshaller.unmarshal(br);
            nicknameToUser = adapter.getNicknameToUser();
            nicknameToUser.forEach((k, v) -> System.out.println(k + " - " + v));
            log.info("User data container loaded from a file [" + marshallFilePath + "]");
        } catch (IOException | JAXBException ex) {
            log.error("Error while unmarshalling user data container from file " + marshallFilePath + " " + ex);
        }
    }

    public User getUser(String nickname) {
        return nicknameToUser.get(nickname);
    }

    public Set<User> getAllUsers() {
        return new HashSet<>(nicknameToUser.values());
    }

    public <T extends ChangeCredentialsRequest> ChangeCredentialsResponse handle(T changeCredentialsRequest) {
        String userNickname = changeCredentialsRequest.getUserNickname();
        String newFullName = changeCredentialsRequest.getNewFullName();
        String newPassword = changeCredentialsRequest.getNewPassword();
        if (!nicknameToUser.containsKey(userNickname)) {
            return new ChangeCredentialsResponse(false);
        }
        if (newPassword == null) {
            nicknameToUser.computeIfPresent(userNickname, (k, v) -> v.setFullName(newFullName));
        } else {
            nicknameToUser.computeIfPresent(userNickname, (k, v) -> v.setPassword(newPassword).setFullName(newFullName));
        }
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
        if (user != null && authNickname.equals(user.getNickname()) && authPassword.equals(user.getPassword())) {
            Set<ChatRoom> chatRoomsWithUser = ChatRoomsContainer.INSTANCE.getChatRoomsFor(authNickname);
            chatRoomsWithUser.addAll(ChatInvitationsContainer.INSTANCE.getChatInvitationsFor(authNickname));
            ChatInvitationsContainer.INSTANCE.removeInvitations(authNickname);
            return new AuthorizationResponse(true, user, chatRoomsWithUser);
        } else {
            return new AuthorizationResponse(false);
        }
    }

    public String getMarshallingFilePath() {
        return marshallFilePath;
    }

    public Map<String, User> getContainer() {
        return new ConcurrentHashMap<>(nicknameToUser);
    }

}
