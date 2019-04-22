package com.group4.server.controller;

import com.group4.server.model.containers.ChatInvitationsContainer;
import com.group4.server.model.containers.ChatRoomsContainer;
import com.group4.server.model.entities.ChatRoom;
import com.group4.server.model.entities.User;
import com.group4.server.model.message.adapters.ChatContainerEnumAdapter;
import com.group4.server.model.message.adapters.ChatInvitationsEnumAdapter;
import com.group4.server.model.message.adapters.UserDataContainerAdapter;
import com.group4.server.model.message.handlers.RegistrationAuthorizationHandler;
import org.apache.log4j.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Set;


public class StartupShutdownController {

    private static final Logger log = Logger.getLogger(StartupShutdownController.class);
    private Marshaller marshaller;
    private ServerController serverController = new ServerController();
    private BufferedReader buff;

    private Class<?>[] clazzes = {
            User.class,
            ChatRoom.class,
            ChatInvitationsContainer.class,
            ChatInvitationsEnumAdapter.class,
            ChatRoomsContainer.class,
            ChatContainerEnumAdapter.class,
            UserDataContainerAdapter.class,
            RegistrationAuthorizationHandler.class
    };

    private StartupShutdownController() {
        initContext();
    }

    private void initContext() {
        try {
            JAXBContext context = JAXBContext.newInstance(clazzes);
            marshaller = context.createMarshaller();
        } catch (JAXBException ex) {
            log.error("Context for marshalling was not initialized" + ex);
        }
    }

    public static void main(String... args) {
        StartupShutdownController startupShutdownController = new StartupShutdownController();
        startupShutdownController.processActions();
        log.info("Server side console closed.");
    }


    private void showActions() {
        System.out.println("\n - Please choose a further action by typing it's number in following menu \n");
        String[] menuItems = new String[]{
                "Start server.",
                "Stop server.",
                "Exit."
        };

        showMenu(menuItems);
    }

    private void showMenu(String... items) {
        String menuFormat = "#%d\t%s%n";
        for (int i = 0; i < items.length; ++i) {
            System.out.printf(menuFormat, i + 1, items[i]);
        }
    }

    private void processActions() {
        String inputChoice;
        boolean exitAction = false;
        do {
            showActions();
            inputChoice = getTrimmedInput();
            switch (inputChoice) {
                case "1":
                    if (serverController.isRunning()) {
                        System.out.println("Server is already running");
                    } else {
                        serverController = new ServerController();
                        serverController.start();
                        serverController.setRunning(true);
                        System.out.println("SERVER STARTED.");
                    }
                    break;
                case "2":
                    if (!serverController.isRunning()) {
                        System.out.println("Server is already stopped.");
                    } else {
                        saveData(marshaller);
                        serverController.setRunning(false);
                        serverController.interrupt();
                        System.out.println("SERVER WAS STOPPED");
                    }
                    break;
                case "3":
                    exitAction = true;
                    if (serverController.isRunning()) {
                        serverController.setRunning(false);
                        saveData(marshaller);
                        serverController.interrupt();
                    }
                    System.out.println("EXITED FROM SERVER CONSOLE");
                    break;
                default:
                    System.out.print("Incorrect input, please retry.");
            }
        } while (!exitAction);
    }

    private String getTrimmedInput() {
        System.out.print("\n>>> Your input: ");
        String input = "";
        buff = new BufferedReader(new InputStreamReader(System.in));
        do {
            try {
                input = buff.readLine();
            } catch (IOException e) {
                log.error("NULL returned from user's input stream. ", e);
                System.out.print("! Cannot read input because of internal error, please retry the input.");
            }
        } while (input == null);
        return input.trim();
    }

    public Marshaller getMarshaller() {
        return marshaller;
    }

    private void saveData(Marshaller marshaller) {
        marshallChatInvitations(marshaller);
        marshallChatRooms(marshaller);
        marshallUserData(marshaller);
    }

    private void marshallChatInvitations(Marshaller marshaller) {
        String chatInvitationsContainerFile = ChatInvitationsContainer.INSTANCE.getMarshallingFilePath();
        Map<String, Set<ChatRoom>> chatInvitationsContainer = ChatInvitationsContainer.INSTANCE.getContainer();
        ChatInvitationsEnumAdapter chatInvitationsEnumAdapter = new ChatInvitationsEnumAdapter(chatInvitationsContainer);
        try {
            marshaller.marshal(chatInvitationsEnumAdapter, new File(chatInvitationsContainerFile));
        } catch (JAXBException ex) {
            log.error("Chat invitations container was not marshalled" + ex);
        }
    }

    private void marshallChatRooms(Marshaller marshaller) {
        String chatRoomsContainerFile = ChatRoomsContainer.INSTANCE.getMarshallingFilePath();
        Map<Long, ChatRoom> chatInvitationsContainer = ChatRoomsContainer.INSTANCE.getContainer();
        ChatContainerEnumAdapter chatInvitationsEnumAdapter = new ChatContainerEnumAdapter(chatInvitationsContainer);
        try {
            marshaller.marshal(chatInvitationsEnumAdapter, new File(chatRoomsContainerFile));
        } catch (JAXBException ex) {
            log.error("Chat rooms were not marshalled" + ex);
        }
    }

    private void marshallUserData(Marshaller marshaller) {
        String userDataContainerFile = RegistrationAuthorizationHandler.INSTANCE.getMarshallingFilePath();
        Map<String, User> userDataContainer = RegistrationAuthorizationHandler.INSTANCE.getContainer();
        UserDataContainerAdapter chatInvitationsEnumAdapter = new UserDataContainerAdapter(userDataContainer);
        try {
            marshaller.marshal(chatInvitationsEnumAdapter, new File(userDataContainerFile));
        } catch (JAXBException ex) {
            log.error("User data was not marshalled" + ex);
        }
    }
}
