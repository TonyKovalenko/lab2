package com.group4.server.model.containers;

import java.io.Writer;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public enum UserStreamContainer {

    INSTANCE;

    private Map<String, Writer> userToStream = new ConcurrentHashMap<>();

    public void putStream(String nickname, Writer stream) {
        userToStream.put(nickname, stream);
    }

    public void removeStream(String nickname) {
        userToStream.remove(nickname);
    }

    public Writer getStream(String nickname) {
        return userToStream.get(nickname);
    }

    public void deleteUser(String nickname) {
        userToStream.remove(nickname);
    }

    public Set<String> getCurrentUsers() {
        return new HashSet<>(userToStream.keySet());
    }
}
