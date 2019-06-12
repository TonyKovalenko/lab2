package com.group4.client.controller;

import com.group4.client.view.RegistrationView;
import com.group4.server.model.message.wrappers.MessageWrapper;

public interface RegistrationController {
    /**
     * Sets view for controller
     *
     * @param view view for controller
     */
    void setView(RegistrationView view);

    /**
     * Cancels registration and returns to login page
     */
    void cancel();

    /**
     * Sends registration request if all information is filled correctly,
     * otherwise shows dialog window why action can't be done
     */
    void register();

    /**
     * Processes incoming message. If registration was confirmed, then login page is shown.
     * Otherwise dialog window with explanation why action can't be done is shown
     *
     * @param responseMessage incoming message
     */
    void processMessage(MessageWrapper responseMessage);
}
