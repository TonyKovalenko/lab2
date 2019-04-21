package com.group4.server.model.message.adapters;

import com.group4.server.model.entities.ChatRoom;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ChatInvitationsMapAdapter extends XmlAdapter<ListOfEntries, Map<String, Set<ChatRoom>>> {

    @Override
    public Map<String, Set<ChatRoom>> unmarshal(ListOfEntries listOfEntries) {
        Map<String, Set<ChatRoom>> map = new HashMap<>();
        for (MyEntry entry : listOfEntries.getList()) {
            map.put(entry.getKey(), entry.getSet());
        }
        return map;
    }

    @Override
    public ListOfEntries marshal(Map<String, Set<ChatRoom>> map) {
        ListOfEntries listOfEntries = new ListOfEntries();
        for (Map.Entry<String, Set<ChatRoom>> mapEntry : map.entrySet()) {
            MyEntry myEntry = new MyEntry();
            myEntry.setKey(mapEntry.getKey());
            myEntry.getSet().addAll(mapEntry.getValue());
            listOfEntries.getList().add(myEntry);
        }
        return listOfEntries;
    }
}
