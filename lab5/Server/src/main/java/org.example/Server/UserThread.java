package org.example.Server;

import org.example.Message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public abstract class UserThread extends Thread {
  protected static final Logger log = LoggerFactory.getLogger(UserThread.class);

  protected Socket socket;
  protected String username;
  protected boolean isWorking;

  public UserThread(Socket socket) {
    this.socket = socket;
  }

  protected abstract void performLogin() throws Exception;
  protected abstract Message readNextMessage() throws Exception;
  protected abstract void sendUserListToClient() throws Exception;
  protected abstract void sendMessageListToClient() throws Exception;
  protected abstract void sendMessageToClient(Message msg) throws Exception;

  protected void broadcastUserList() {
    synchronized (Server.userThreads) {
      for (UserThread t : Server.userThreads) {
        try {
          if (t != null && t.isWorking) t.sendUserListToClient();
        } catch (SocketException e) {
          log.debug("Client {} socket closed during user list broadcast", t.username);
        } catch (Exception e) {
          log.error("Failed to send user list to {}", t.username, e);
        }
      }
    }
  }

  protected void broadcastMessage(Message msg) {
    synchronized (Server.userThreads) {
      for (UserThread t : Server.userThreads) {
        try {
          if (t != null && t.isWorking) t.sendMessageToClient(msg);
        } catch (SocketException e) {
          log.debug("Client {} socket closed during message broadcast", t.username);
        } catch (Exception e) {
          log.error("Failed to send message to {}", t.username, e);
        }
      }
    }
  }

  protected void stopWorking() {
    isWorking = false;

    log.info("User {} disconnected", username);
    synchronized (Server.usersList) {
      Server.usersList.remove(username);
    }
    synchronized (Server.userThreads) {
      Server.userThreads.remove(this);
    }
    try {
      socket.close();
    } catch (IOException e) {
      log.debug("Socket already closed for {}", username);
    }
    broadcastUserList();
  }

  @Override
  public void run() {
    isWorking = true;
    try {
      sendMessageListToClient();
      performLogin();

      synchronized (Server.usersList) {
        Server.usersList.add(username);
      }

      log.info("User {} logged in", username);
      broadcastUserList();

      while (isWorking) {
        Message msg = readNextMessage();
        if (msg == null) {
          isWorking = false;
          break;
        }

        synchronized (Server.messages) {
          Server.messages.add(msg);
        }
        broadcastMessage(msg);
        log.debug("Current message history size: {}", Server.messages.size());
      }
    } catch (SocketException e) {
      log.debug("Client {} disconnected abruptly", username);
    } catch (Exception e) {
      log.error("Error in client thread ({})", username, e);
    } finally {
      stopWorking();
    }
  }
}