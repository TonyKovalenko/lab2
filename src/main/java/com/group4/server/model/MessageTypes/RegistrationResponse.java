package com.group4.server.model.MessageTypes;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RegistrationResponse implements TransmittableMessage {

    @XmlElement
    private boolean registrationState;

    public RegistrationResponse() {
    }

    public RegistrationResponse(boolean state) {
        this.registrationState = state;
    }

    public boolean isRegistrationSuccessful() {
        return registrationState;
    }
}
