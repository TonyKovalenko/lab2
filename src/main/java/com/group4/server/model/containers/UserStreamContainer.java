package com.group4.server.model.containers;

import com.group4.server.model.entities.User;
import com.group4.server.model.message.handlers.RegistrationAuthorizationHandler;

import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public enum UserStreamContainer {

    INSTANCE;

    private Map<String, PrintWriter> userToStream = new ConcurrentHashMap<>();

    public void putStream(String nickname, PrintWriter stream) {
        userToStream.put(nickname, stream);
    }

    public void removeStream(String nickname) {
        userToStream.remove(nickname);
    }

    public PrintWriter getStream(String nickname) {
        return userToStream.get(nickname);
    }

    public void deleteUser(String nickname) {
        userToStream.remove(nickname);
    }

    public boolean userIsOnline(String nickname) {
        PrintWriter pw = getStream(nickname);
        return  pw != null;
    }

    public Set<User> getCurrentUsers() {
        Set<User> currentUserSet = new HashSet<>();
        Set<String> currentUserNicknames = userToStream.keySet();
        for (String nickname : currentUserNicknames) {
            currentUserSet.add(RegistrationAuthorizationHandler.INSTANCE.getUser(nickname));
        }
        return currentUserSet;
    }

    public Set<PrintWriter> getCurrentUserStreams() {
        return new HashSet<>(userToStream.values());
    }
}
