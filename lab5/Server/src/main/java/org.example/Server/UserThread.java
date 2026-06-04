package org.example.Server;

import org.example.Message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.Objects;

public class UserThread extends Thread {
    private static final Logger log = LoggerFactory.getLogger(UserThread.class);

    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private boolean isWorking;
    private String username;

    public UserThread(Socket socket) throws Exception {
        this.socket = socket;
        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        in = new ObjectInputStream(socket.getInputStream());
    }

    private void sendListMessages() {
        try {
            out.writeObject(Server.messages);
        }
        catch (Exception e) {
            log.error("Failed to send list of messages", e);
        }
    }

    private void sendUserList() throws IOException {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < Server.usersList.size(); i++) {
            log.debug("User in list: {}", Server.usersList.get(i));
            str.append(Server.usersList.get(i));
            str.append("$");
        }
        out.writeObject("userlist");
        out.writeObject(str);
        out.flush();
        log.info("Sent user list to client");
    }

    private void broadcastUserList() {
        synchronized (Server.userThreads) {
            for (UserThread thread : Server.userThreads) {
                try {
                    if (thread != null) {
                        thread.sendUserList();
                    }
                } catch (Exception e) {
                    log.error("Failed to send user list", e);
                }
            }
        }
    }

    private void getNameUser() {
        try {
            username = (String) in.readObject();
            log.info("User {} connected", username);
            synchronized (Server.usersList) {
                Server.usersList.add(username);
            }
            broadcastUserList();
        } catch (Exception e) {
            log.error("Failed to get username", e);
        }
    }

    private void sendMessage(Message message) throws IOException {
        out.writeObject("message");
        out.writeObject(message);
        out.flush();
        log.info("User {} sent message: {}", message.getUsername(), message.getText());
    }

    private void broadcastMessage(Message message) {
        synchronized (Server.userThreads) {
            for (UserThread thread : Server.userThreads) {
                try {
                    if (thread != null) {
                        thread.sendMessage(message);
                    }
                } catch (Exception e) {
                    log.error("Failed to send message to client", e);
                }
            }
        }
    }

    private void stopWorking() {
        isWorking = false;
        try {
            if (socket != null) socket.close();
            if (in != null) in.close();
            if (out != null) out.close();
        }
        catch (IOException e) {
            log.error("Failed to close resources", e);
        }
        log.info("User {} disconnected", username);
        synchronized (Server.usersList) {
            Server.usersList.remove(username);
        }
        synchronized (Server.userThreads) {
            Server.userThreads.remove(this);
        }
        broadcastUserList();
    }

    @Override
    public void run() {
        isWorking = true;
        sendListMessages();
        getNameUser();
        Message message;
        try {
            while (isWorking) {
                Object received = in.readObject();
                if (received instanceof Message) {
                    message = (Message) received;
                    log.info("Received message from {}: {}", message.getUsername(), message.getText());
                    if (Objects.equals(message.getText(), "Disconnect") &&
                            Objects.equals(message.getUsername(), "System")) {
                        stopWorking();
                        break;
                    }
                    synchronized (Server.messages) {
                        Server.messages.add(message);
                    }
                    broadcastMessage(message);
                }
            }
        } catch (Exception e) {
            log.error("Client disconnected unexpectedly", e);
            stopWorking();
        }
    }
}