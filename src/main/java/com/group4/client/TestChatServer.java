package com.group4.client;

import com.group4.server.model.entities.ChatRoom;
import com.group4.server.model.entities.User;
import com.group4.server.model.message.types.*;
import com.group4.server.model.message.wrappers.MessageWrapper;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;


public class TestChatServer {

    private static final int PORT = 8888;
    private static int userCounter = 0;

    private static HashSet<String> names = new HashSet<String>();
    private static HashSet<PrintWriter> writers = new HashSet<PrintWriter>();


    public static void main(String[] args) throws Exception {
        System.out.println("The chat server is running.");

        try (ServerSocket listener = new ServerSocket(PORT)) {
            while (true) {
                Handler handler = new Handler(listener.accept());
                handler.start();
            }
        }
    }

    private static class Handler extends Thread {
        private Socket socket;
        public BufferedReader reader;
        public PrintWriter writer;

        private static Class<?>[] clazzes = {MessageWrapper.class, PingMessage.class,
                AuthorizationRequest.class, AuthorizationResponse.class,
                ChatMessage.class, NewGroupChatMessage.class, UsersInChatMessage.class,
                RegistrationRequest.class, RegistrationResponse.class,
                ChangeCredentialsRequest.class, ChangeCredentialsResponse.class,
                UpdateGroupChatMessage.class
        };
        public JAXBContext context;
        public Marshaller marshaller;
        public Unmarshaller unmarshaller;

        public List<ChatMessage> messages = new ArrayList<ChatMessage>() {{
            add(new ChatMessage(10001, 2, "The Library. \nSo big it doesn't need a name. Just a great big The."));
            add(new ChatMessage(10000, 3, "It's like a city. "));
            add(new ChatMessage(10001, 2, "It's a world. \nLiterally, a world. \nThe whole core of the planet is the index computer. Biggest hard drive ever. And up here, every book ever written. Whole continents of Jeffrey Archer, Bridget Jones, Monty Python's Big Red Book. Brand new editions, specially printed. "));
            add(new ChatMessage(10001, 2, "We're near the equator, so this must be biographies. I love biographies. "));
            add(new ChatMessage(10000, 3, "Yeah, very you. Always a death at the end. "));
            add(new ChatMessage(10001, 2, "You need a good death. Without death, there'd only be comedies. Dying gives us size. "));
            add(new ChatMessage(10001, 2, "Way-a. Spoilers. "));
            add(new ChatMessage(10000, 2, "What? "));
            add(new ChatMessage(10001, 3, "These books are from your future. \nYou don't want to read ahead. Spoil all the surprises. Like peeking at the end. "));
            add(new ChatMessage(10000, 2, "Isn't travelling with you one big spoiler? "));
            add(new ChatMessage(10001, 2, "I try to keep you away from major plot developments. Which, to be honest, I seem to be very bad at, because you know what? This is the biggest library in the universe. So where is everyone? It's silent."));
            add(new ChatMessage(10000, 2, "The library? "));
            add(new ChatMessage(10001, 2, "The planet. The whole planet. "));
            add(new ChatMessage(10000, 2, "Maybe it's a Sunday. "));
            add(new ChatMessage(10001, 2, "No, I never land on Sundays. Sundays are boring. "));
            add(new ChatMessage(10000, 2, "Well, maybe everyone's really, really quiet. "));
            add(new ChatMessage(10001, 2, "Yeah, maybe. But they'd still show up on the system. "));
            add(new ChatMessage(10000, 2, "Doctor, why are we here? Really, why? "));
            add(new ChatMessage(10001, 2, "Oh, you know, just passing. "));
            add(new ChatMessage(10000, 2, "No, seriously. It was all let's hit the beach, then suddenly we're in a library. Why? "));
            add(new ChatMessage(10001, 3, "Now that's interesting. "));
            add(new ChatMessage(10000, 3, "What? "));
            add(new ChatMessage(10001, 3, "Scanning for life forms. If I do a scan looking for your basic humanoids. You know, your book readers, few limbs and a face, apart from us, I get nothing. Zippo, nada. See? Nobody home. But if I widen the parameters to any kind of life. "));
            add(new ChatMessage(10001, 3, "A million, million. Gives up after that. A million, million. "));
            add(new ChatMessage(10000, 3, "But there's nothing here. There's no one. "));
            add(new ChatMessage(10001, 3, "And not a sound. A million. million life forms, and silence in the library. "));
            add(new ChatMessage(10000, 3, "But there's no one here. There's just books. I mean, it's not the books, is it? I mean, it can't be the books, can it? I mean, books can't be alive. \n"));
        }};


        public Handler(Socket socket) {
            this.socket = socket;
        }

        public void sendMessage(TransmittableMessage message, PrintWriter writer) {
            MessageWrapper messageWrapper = new MessageWrapper(message);
            StringWriter stringWriter = new StringWriter();
            try {
                this.context.createMarshaller().marshal(messageWrapper, stringWriter);
                writer.println(stringWriter.toString().replaceAll("\n", "<br />"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void run() {
            try {
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new PrintWriter(socket.getOutputStream(), true);
                context = JAXBContext.newInstance(clazzes);
                marshaller = context.createMarshaller();
                unmarshaller = context.createUnmarshaller();

                List<User> users = new ArrayList<>();
                User user1 = new User(10000, "donna", "Donna Noble", "1");
                User user2 = new User(10001, "doctor", "The Doctor", "1");
                users.add(user1);
                users.add(user2);
                user1.setAdmin(true);

                writers.add(writer);

                new Thread(() -> {
                    long i = 1;
                    Scanner scanner = new Scanner(System.in);
                    int j = 0;
                    while (true) {
                        switch (scanner.nextInt()) {
                            case 1:
                                UsersInChatMessage message0 = new UsersInChatMessage();
                                User user = new User();
                                user.setNickname("User#" + i);
                                user.setId(i);
                                users.add(user);
                                i++;
                                message0.setUsers(users);
                                for (PrintWriter writer : writers) {
                                    sendMessage(message0, writer);
                                }
                                break;
                            case 2:
                                for (PrintWriter writer : writers) {
                                    sendMessage(messages.get(j % messages.size()), writer);
                                }
                                //sendMessage(messages.get(j % messages.size()));
                                j++;
                                break;
                            default:
                        }
                    }
                }).start();
                int idCounter = 5;

                while (socket.isConnected()) {
                    if(reader.ready()) {
                        try (StringReader dataReader = new StringReader(reader.readLine().replaceAll("<br />", "\n"))) {
                            MessageWrapper message = (MessageWrapper) unmarshaller.unmarshal(dataReader);
                            System.out.println(message + " " + message.getMessageType() + " " + message.getMessageId());
                            switch (message.getMessageType()) {
                                case AUTHORIZATION_REQUEST:
                                    AuthorizationResponse authorizationResponse = new AuthorizationResponse();
                                    authorizationResponse.setConfirmed(true);
                                    if (userCounter % 2 == 0) {
                                        System.out.println("user1 login");
                                        authorizationResponse.setUser(user1);
                                    } else {
                                        System.out.println("user2 login");
                                        authorizationResponse.setUser(user2);
                                    }
                                    userCounter++;
                                    authorizationResponse.setChatRoomsWithUser(Collections.singletonList(new ChatRoom(3, "Whovians", users)));
                                    sendMessage(authorizationResponse, writer);

                                    UsersInChatMessage message0 = new UsersInChatMessage();
                                    message0.setUsers(users);
                                    sendMessage(message0, writer);
                                    break;
                                case REGISTRATION_REQUEST:
                                    RegistrationResponse registrationResponse = new RegistrationResponse(true, new ChatRoom());
                                    sendMessage(registrationResponse, writer);
                                    break;
                                case NEW_GROUPCHAT:
                                case NEW_PRIVATECHAT:
                                    NewGroupChatMessage newGroupChatMessage = (NewGroupChatMessage) message.getEncapsulatedMessage();
                                    newGroupChatMessage.getChatRoom().setId(idCounter++);
                                    for (PrintWriter writer : writers) {
                                        sendMessage(newGroupChatMessage, writer);
                                    }
                                    break;
                                case TO_CHAT:
                                    System.out.println(message.getEncapsulatedMessage());
                                    for (PrintWriter writer : writers) {
                                        sendMessage(message.getEncapsulatedMessage(), writer);
                                    }
                                    break;
                                case PING:
                                    PingMessage pingMessage = new PingMessage();
                                    sendMessage(pingMessage, writer);
                                    break;
                                case CHANGE_CREDENTIALS_REQUEST:
                                    ChangeCredentialsRequest request = (ChangeCredentialsRequest) message.getEncapsulatedMessage();
                                    Optional<User> optionalUser = users.stream()
                                            .filter(element -> request.getUserId() == element.getId())
                                            .findFirst();
                                    User user = optionalUser.orElse(new User());
                                    user.setPassword(request.getNewPassword());
                                    user.setFullName(request.getNewFullName());
                                    ChangeCredentialsResponse response = new ChangeCredentialsResponse(true, user);
                                    sendMessage(response, writer);
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                }

            } catch (IOException | JAXBException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
