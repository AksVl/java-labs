package org.example.Client;

import org.example.Message.Message;
import org.example.View.ClientWindow;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ClientThread extends Thread {
    private Socket socketClient;
    private final String host = ConfigLoader.getHost();
    private final int port = ConfigLoader.getPort();
    private final boolean isXmlMode = ConfigLoader.isXmlMode();

    private DataOutputStream xmlOut;
    private ObjectOutputStream objOut;

    private final String username;
    private ClientWindow window;
    private boolean isWorking;
    private ReaderThread readerThread;

    public ClientThread(String username, ClientWindow window) {
        this.username = username;
        this.window = window;
    }

    private String calcTimeCurrent() {
        ZonedDateTime time = ZonedDateTime.now(ZoneId.of("Asia/Novosibirsk"));
        return time.format(DateTimeFormatter.ofPattern("dd.MM HH:mm"));
    }

    private void sendXmlCommand(String xml) throws IOException {
        byte[] data = xml.getBytes(StandardCharsets.UTF_8);
        xmlOut.writeInt(data.length);
        xmlOut.write(data);
        xmlOut.flush();
    }

    @Override
    public void run() {
        isWorking = true;
        System.out.println("Thread started");
        try {
            socketClient = new Socket(host, port);
            System.out.println("Connected in " + (isXmlMode ? "XML" : "Object") + " mode");
            String modeStr = isXmlMode ? "XML\n" : "OBJ\n";
            socketClient.getOutputStream().write(modeStr.getBytes(StandardCharsets.UTF_8));
            socketClient.getOutputStream().flush();

            if (isXmlMode) {
                xmlOut = new DataOutputStream(socketClient.getOutputStream());
                DataInputStream xmlIn = new DataInputStream(socketClient.getInputStream());

                sendXmlCommand("<command name=\"login\"><username>" + username + "</username></command>");
                readerThread = new ReaderThread(xmlIn, window);
            } else {
                objOut = new ObjectOutputStream(socketClient.getOutputStream());
                objOut.flush();
                ObjectInputStream objIn = new ObjectInputStream(socketClient.getInputStream());
                try {
                    @SuppressWarnings("unchecked")
                    ArrayList<Message> messages = (ArrayList<Message>) objIn.readObject();
                    for (Message message : messages) {
                        if (message == null || message.getText() == null || message.getText().isEmpty()) {
                            continue;
                        }
                        if (message.getText().charAt(0) == '/') {
                            continue;
                        }
                        window.addMessage(message);
                    }
                } catch (Exception e) {
                    System.err.println("Failed to get array messages");
                }
                objOut.writeObject(username);
                objOut.flush();
                readerThread = new ReaderThread(objIn, window);
            }

            readerThread.start();
            window.setCurrentUsername(username);
            String time = calcTimeCurrent();
            if (isXmlMode) {
                sendXmlCommand("<command name=\"message\"><username> </username>"
                        + "<text>" + username + " connected</text><time>" + time + "</time></command>");
            } else {
                objOut.writeObject(new Message(" ", username + " connected", time));
                objOut.flush();
            }
        } catch (Exception e) {
            throw new RuntimeException(e + "Client socket create failed");
        }
    }
    public void sendMessage(String text) {
        String time = calcTimeCurrent();
        try {
            if (isXmlMode) {
                sendXmlCommand("<command name=\"message\"><username>" + username + "</username>"
                        + "<text>" + text + "</text><time>" + time + "</time></command>");
            } else {
                objOut.writeObject(new Message(username, text, time));
                objOut.flush();
            }
        } catch (Exception e) {
            System.err.println("Failed to send message");
        }
    }

    public void stopWorking() {
        isWorking = false;
        readerThread.stopWorking();
        try {
            String time = calcTimeCurrent();
            if (isXmlMode) {
                sendXmlCommand("<command name=\"message\"><username>System</username>"
                        + "<text>" + username + " disconnected</text><time>" + time + "</time></command>");
                sendXmlCommand("<command name=\"disconnect\"></command>");
            } else {
                objOut.writeObject(new Message("", username + " disconnected", time));
                objOut.writeObject(new Message("System", "Disconnect", time));
                objOut.flush();
            }
            socketClient.close();
        } catch (Exception e) {
            System.err.println("Failed to finish thread");
        }
    }
}