package com.group4.server.model.message.types;

import com.group4.server.model.entities.ChatRoom;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class RegistrationResponse implements TransmittableMessage {

    @XmlElement
    private boolean registrationState;


    public RegistrationResponse() {
    }

    public RegistrationResponse(boolean registrationState) {
        this.registrationState = registrationState;
    }


    public boolean isRegistrationSuccessful() {
        return registrationState;
    }

    public void setRegistrationState(boolean registrationState) {
        this.registrationState = registrationState;
    }
}
