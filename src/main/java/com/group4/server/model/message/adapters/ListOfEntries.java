package com.group4.server.model.message.adapters;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.NONE)
public class ListOfEntries {

    @XmlElement
    private List<MyEntry> list = new ArrayList<>();

    public List<MyEntry> getList() {
        return list;
    }

}
