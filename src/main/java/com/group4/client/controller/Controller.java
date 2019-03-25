package com.group4.client.controller;

import com.group4.client.view.LoginView;
import com.group4.client.view.MainView;
import com.group4.client.view.View;
import com.group4.server.model.MessageWrappers.MessageWrapper;
import javafx.application.Application;
import javafx.stage.Stage;

public class Controller extends Application {
    private Stage stage;
    private MainView mainView;
    private MessageThread thread;
    private static Controller instance;

    public static Controller getInstance() {
        return instance;
    }

    public Stage getStage() {
        return stage;
    }

    public MessageThread getThread() {
        return thread;
    }

    public void setView(MainView view) {
        mainView = view;
    }

    /**
     * The main entry point for all JavaFX applications.
     * The start method is called after the init method has returned,
     * and after the system is ready for the application to begin running.
     *
     * <p>
     * NOTE: This method is called on the JavaFX Application Thread.
     * </p>
     *
     * @param primaryStage the primary stage for this application, onto which
     *                     the application scene can be set. The primary stage will be embedded in
     *                     the browser if the application was launched as an applet.
     *                     Applications may create other stages, if needed, but they will not be
     *                     primary stages and will not be embedded in the browser.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        instance = this;
        stage = primaryStage;
        /*thread = new MessageThread();
        thread.start();*/

        LoginView.getInstance().showStage();
    }

    public void processMessage(MessageWrapper requestMessage, MessageWrapper responseMessage) {

    }
}
