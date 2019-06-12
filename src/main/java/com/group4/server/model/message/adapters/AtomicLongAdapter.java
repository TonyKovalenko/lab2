package com.group4.server.model.message.adapters;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.concurrent.atomic.AtomicLong;

public class AtomicLongAdapter extends XmlAdapter<Long, AtomicLong> {

    public AtomicLong unmarshal(Long v) {
        return new AtomicLong(v);
    }

    public Long marshal(AtomicLong v) {
        return v.longValue();
    }
}
