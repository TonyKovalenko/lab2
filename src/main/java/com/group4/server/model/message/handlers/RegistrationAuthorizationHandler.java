package com.group4.server.model.message.handlers;

import com.group4.server.model.containers.ChatInvitationsContainer;
import com.group4.server.model.containers.ChatRoomsContainer;
import com.group4.server.model.containers.UserStreamContainer;
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
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * RegistrationAuthorizationHandler class is used to
 * handle user requests, regarding their personal data
 */
public enum RegistrationAuthorizationHandler {

    INSTANCE;

    private final Logger log = Logger.getLogger(RegistrationAuthorizationHandler.class);
    private String marshallFilePath = "nicknameToUser.xml";

    private Map<String, User> nicknameToUser = new ConcurrentHashMap<>();

    /**
     * Constructor to create user data container from {@link #marshallFilePath} file.
     */
    RegistrationAuthorizationHandler() {
        unmarshallOnStart();
    }

    /**
     * Method to unmarshall the user data container from the {@link #marshallFilePath} file
     * to a internal collection.
     * Also default admin creation is done in this method.
     */
    private void unmarshallOnStart() {
        UserDataContainerAdapter adapter;
        try (BufferedReader br = new BufferedReader(new FileReader(new File(marshallFilePath)))) {
            JAXBContext context = JAXBContext.newInstance(UserDataContainerAdapter.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            adapter = (UserDataContainerAdapter) unmarshaller.unmarshal(br);
            nicknameToUser = adapter.getNicknameToUser();
            log.info("User data container loaded from a file [" + marshallFilePath + "]");
        } catch (IOException | JAXBException ex) {
            log.error("Error while unmarshalling user data container from file " + marshallFilePath + " " + ex);
        }
        User defaultAdmin = new User("admin", "admin", "admin");
        defaultAdmin.setAdmin(true);
        nicknameToUser.put(defaultAdmin.getNickname(), defaultAdmin);
    }

    public User getUser(String nickname) {
        return nicknameToUser.get(nickname);
    }

    public Set<User> getAllUsers() {
        return new HashSet<>(nicknameToUser.values());
    }

    /**
     * Method is used to handle user credential request
     *
     * @param changeCredentialsRequest request on changing the personal data
     * @return confirmation state of the credential change
     */
    public <T extends ChangeCredentialsRequest> ChangeCredentialsResponse handle(T changeCredentialsRequest) {
        String userNickname = changeCredentialsRequest.getUserNickname();
        String newFullName = changeCredentialsRequest.getNewFullName();
        String newPassword = changeCredentialsRequest.getNewPassword();


        if (!nicknameToUser.containsKey(userNickname)) {
            return new ChangeCredentialsResponse(false);
        }
        if (newPassword.isEmpty()) {
            nicknameToUser.computeIfPresent(userNickname, (k, v) -> v.setFullName(newFullName));
        } else {
            nicknameToUser.computeIfPresent(userNickname, (k, v) -> v.setPassword(newPassword).setFullName(newFullName));
        }
        return new ChangeCredentialsResponse(true, nicknameToUser.get(userNickname));
    }

    /**
     * Method is used to handle user registration request
     *
     * @param registrationRequest request on registering in the system
     * @return confirmation state of the registration
     */
    public <T extends RegistrationRequest> RegistrationResponse handle(T registrationRequest) {
        User user = new User(registrationRequest.getUserNickname(), registrationRequest.getFullName(), registrationRequest.getPassword());
        if (nicknameToUser.containsValue(user)) {
            return new RegistrationResponse(false);
        } else {
            nicknameToUser.put(user.getNickname(), user);
            return new RegistrationResponse(true);
        }
    }

    /**
     * Method is used to handle user authorization request
     *
     * @param authorizationRequest request on authorization in the system
     * @return confirmation state of the authorization
     */
    public <T extends AuthorizationRequest> AuthorizationResponse handle(T authorizationRequest) {
        String authNickname = authorizationRequest.getUserNickname();
        String authPassword = authorizationRequest.getPassword();
        User user = getUser(authNickname);

        if (UserStreamContainer.INSTANCE.userIsOnline(authNickname)) {
            return new AuthorizationResponse(false);
        }

        if (user != null && authNickname.equals(user.getNickname()) && authPassword.equals(user.getPassword())) {
            ChatRoomsContainer.INSTANCE.putToInitialRoom(user);
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

    public boolean deleteUser(String nickname) {
        return nicknameToUser.remove(nickname) != null;
    }

}
