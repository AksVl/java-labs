package org.example.Server;

import org.example.Message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.Objects;

public class ObjectUserThread extends UserThread {
  private static final Logger log = LoggerFactory.getLogger(ObjectUserThread.class);

  private ObjectInputStream in;
  private ObjectOutputStream out;

  public ObjectUserThread(Socket socket) throws Exception {
    super(socket);
    out = new ObjectOutputStream(socket.getOutputStream());
    out.flush();
    in = new ObjectInputStream(socket.getInputStream());
  }

  @Override
  protected void performLogin() throws Exception {
    username = (String) in.readObject();
  }

  @Override
  protected Message readNextMessage() throws Exception {
    Object received = in.readObject();
    if (received instanceof Message) {
      Message msg = (Message) received;
      if (Objects.equals(msg.getText(), "Disconnect") &&
              Objects.equals(msg.getUsername(), "System")) {
        return null;
      }
      return msg;
    }
    return null;
  }

  @Override
  protected void sendUserListToClient() throws Exception {
    StringBuilder str = new StringBuilder();
    for (String u : Server.usersList) str.append(u).append("$");
    out.writeObject("userlist");
    out.writeObject(str);
    out.flush();
    log.debug("Sent user list to {} (Object)", username);
  }

  @Override
  protected void sendMessageListToClient() throws Exception {
    out.writeObject(Server.messages);
    out.flush();
  }

  @Override
  protected void sendMessageToClient(Message msg) throws Exception {
    out.writeObject("message");
    out.writeObject(msg);
    out.flush();
    log.info("Sent message to {} (Object): {}", username, msg.getText());
  }
}