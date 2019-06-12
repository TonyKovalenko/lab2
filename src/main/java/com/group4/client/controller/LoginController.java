package com.group4.client.controller;

import com.group4.client.view.LoginView;
import com.group4.server.model.message.wrappers.MessageWrapper;

public interface LoginController {
    /**
     * Sets view for controller
     *
     * @param view view for controller
     */
    void setView(LoginView view);

    /**
     * Event handler for "Login" button.
     * Sends login request if all information is filled correctly,
     * otherwise shows dialog window why action can't be done
     */
    void login();

    /**
     * Shows registration window
     */
    void register();

    /**
     * Processes incoming message. If authorization was confirmed,
     * then data form AuthorizationResponse is initialized and main page is shown.
     * Otherwise dialog window with explanation why action can't be done is shown
     *
     * @param responseMessage incoming message
     */
    void processMessage(MessageWrapper responseMessage);

    /**
     * Sends authorization request
     *
     * @param nickname nickname for authorization request
     * @param password password for authorization request
     */
    void sendAuthorizationRequest(String nickname, String password);
}
