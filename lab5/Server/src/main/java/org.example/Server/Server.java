package org.example.Server;

import org.example.Message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Server {
    private static final Logger log = LoggerFactory.getLogger(Server.class);

    private final int port = ConfigLoader.getPort();
    private int countUsers;
    public static final ArrayList<String> usersList = new ArrayList<>();
    public static final ArrayList<Message> messages = new ArrayList<>();
    public static final ArrayList<UserThread> userThreads = new ArrayList<>();

    public Server() {
        countUsers = 0;
    }

    public void onServer() throws IOException {
        try (ServerSocket server = new ServerSocket(port)) {
            while (true) {
                try {
                    log.info("Server is waiting for connection...");
                    Socket socket = server.accept();
                    byte[] modeBytes = new byte[4];
                    socket.getInputStream().readNBytes(modeBytes, 0, 4);
                    String mode = new String(modeBytes, StandardCharsets.UTF_8).trim();

                    UserThread userThread;
                    if ("XML".equalsIgnoreCase(mode)) {
                        userThread = new XmlUserThread(socket);
                    } else {
                        userThread = new ObjectUserThread(socket);
                    }

                    userThreads.add(userThread);
                    countUsers++;
                    log.info("Connection established ({} mode)", mode);
                    userThread.start();
                } catch (Exception e) {
                    log.error("Failed to connect to server", e);
                }
            }
        } catch (Exception e) {
            log.error("Failed to start server", e);
        }
    }
}