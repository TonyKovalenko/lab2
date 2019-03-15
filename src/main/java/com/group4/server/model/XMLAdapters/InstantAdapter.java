package com.group4.server.model.XMLAdapters;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.Instant;

public class InstantAdapter extends XmlAdapter<String, Instant> {
    public Instant unmarshal(String v) throws Exception {
        return Instant.parse(v);
    }

    public String marshal(Instant v) throws Exception {
        return v.toString();
    }
}
