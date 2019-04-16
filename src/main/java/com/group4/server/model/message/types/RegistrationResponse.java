package com.group4.server.model.message.types;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RegistrationResponse implements TransmittableMessage {

    @XmlElement
    private boolean registrationState;
    @XmlElement
    private long generatedId;

    public RegistrationResponse() {
    }

    public RegistrationResponse(boolean state, long generatedId) {
        this.registrationState = state;
        this.generatedId = generatedId;
    }

    public boolean isRegistrationSuccessful() {
        return registrationState;
    }
}
