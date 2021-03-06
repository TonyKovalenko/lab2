package com.group4.server.model.managers;

import com.group4.server.controller.MessageController;
import com.group4.server.controller.ServerController;
import com.group4.server.model.containers.ChatInvitationsContainer;
import com.group4.server.model.containers.ChatRoomsContainer;
import com.group4.server.model.containers.UserStreamContainer;
import com.group4.server.model.entities.ChatRoom;
import com.group4.server.model.entities.User;
import com.group4.server.model.message.adapters.ChatContainerEnumAdapter;
import com.group4.server.model.message.adapters.ChatInvitationsEnumAdapter;
import com.group4.server.model.message.adapters.UserDataContainerAdapter;
import com.group4.server.model.message.handlers.RegistrationAuthorizationHandler;
import com.group4.server.model.message.types.ServerRestartMessage;
import com.group4.server.model.message.types.ServerShutdownMessage;
import org.apache.log4j.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.*;
import java.util.Map;
import java.util.Set;

/**
 * ServerStartupShutdownManager is primary for controlling the console
 * interface of server side, allows to stop, start and restart the server
 *
 * @author Anton Kovalenko
 * @see ServerController
 * @since 05-06.19
 */
public class ServerStartupShutdownManager {

    private static final Logger log = Logger.getLogger(ServerStartupShutdownManager.class);
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

    /**
     * Constructor of ServerStartupShutdownManager class,
     * it initializes the JAXB context, needed for saving and retrieving user data from storage.
     */
    private ServerStartupShutdownManager() {
        initContext();
    }

    /**
     * Method to initialize internal JAXB context
     */
    private void initContext() {
        try {
            JAXBContext context = JAXBContext.newInstance(clazzes);
            marshaller = context.createMarshaller();
        } catch (JAXBException ex) {
            log.error("Context for marshalling was not initialized" + ex);
        }
    }

    public static void main(String... args) {
        ServerStartupShutdownManager serverStartupShutdownManager = new ServerStartupShutdownManager();
        serverStartupShutdownManager.processActions();
        log.info("Server side console closed.");
    }

    /**
     * Method, that prints the available actions
     * that could be performed with the server
     * to it's console by using {@link ServerStartupShutdownManager#showMenu(String...)}
     */
    private void showActions() {
        System.out.println("\n - Please choose a further action by typing it's number in following menu \n");
        String[] menuItems = new String[]{
                "Start server.",
                "Stop server.",
                "Restart server.",
                "Exit."
        };
        showMenu(menuItems);
    }

    /**
     * Method that prints input arguments in for of a menu to a console.
     *
     * @param items items, needed to be printed
     */
    private void showMenu(String... items) {
        String menuFormat = "#%d\t%s%n";
        for (int i = 0; i < items.length; ++i) {
            System.out.printf(menuFormat, i + 1, items[i]);
        }
    }

    /**
     * Method to get {@link ServerStartupShutdownManager#getTrimmedInput()} and process users input as a desired action,
     * that should be performed with a server.
     */
    private void processActions() {
        String inputChoice;
        boolean exitAction = false;
        do {
            showActions();
            inputChoice = getTrimmedInput();
            switch (inputChoice) {
                case "1": //start server
                    if (serverController.isAlive()) {
                        System.out.println("Server is already running");
                    } else {
                        serverController = new ServerController();
                        serverController.start();
                        System.out.println("SERVER STARTED.");
                    }
                    break;
                case "2": //stop server
                    if (!serverController.isAlive()) {
                        System.out.println("Server is already stopped.");
                    } else {
                        new MessageController().broadcastToOnlineUsers(new ServerShutdownMessage(), new StringWriter());
                        saveData(marshaller);
                        serverController.interrupt();
                        UserStreamContainer.INSTANCE.clearUsers();
                        System.out.println("SERVER WAS STOPPED");
                    }
                    break;
                case "3": //restart server
                    if (!serverController.isAlive()) {
                        System.out.println("Server is stopped.");
                    } else {
                        new MessageController().broadcastToOnlineUsers(new ServerRestartMessage(), new StringWriter());
                        saveData(marshaller);
                        serverController.interrupt();
                        UserStreamContainer.INSTANCE.clearUsers();
                        serverController = new ServerController();
                        serverController.start();
                        System.out.println("Server restarted");
                    }
                    break;
                case "4": //exit
                    exitAction = true;
                    if (serverController.isAlive()) {
                        saveData(marshaller);
                        serverController.interrupt();
                    }
                    break;
                default:
                    System.out.print("Incorrect input, please retry.");
            }
        } while (!exitAction);
        System.out.println("EXITED FROM SERVER CONSOLE");
    }

    /**
     * Method to get users input from console.
     *
     * @return trimmed user's input
     */
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

    /**
     * Method to return internal JAXB marshaller of class
     *
     * @return marshallerof a class
     */
    public Marshaller getMarshaller() {
        return marshaller;
    }

    /**
     * Method to save user data containers upon server shutdown.
     *
     * @param marshaller marshaller, to use while saving the data
     */
    private void saveData(Marshaller marshaller) {
        marshallChatInvitations(marshaller);
        marshallChatRooms(marshaller);
        marshallUserData(marshaller);
    }

    /**
     * Method to save chat invitations to a file
     *
     * @param marshaller marshaller, to use for saving the data
     * @see ChatInvitationsContainer
     */
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

    /**
     * Method to save chat rooms to a file
     *
     * @param marshaller marshaller, to use for saving the data
     * @see ChatRoomsContainer
     */
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

    /**
     * Method to save user data to a file
     *
     * @param marshaller marshaller, to use for saving the data
     * @see RegistrationAuthorizationHandler
     */
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
