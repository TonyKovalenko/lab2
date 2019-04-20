package com.group4.server.model.containers;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

    public Set<String> getCurrentUsers() {
        return new HashSet<>(userToStream.keySet());
    }
}
