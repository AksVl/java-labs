package org.example.Server;

import org.example.Message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

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
          if (t != null) t.sendUserListToClient();
        } catch (Exception e) {
          log.error("Failed to send user list", e);
        }
      }
    }
  }

  protected void broadcastMessage(Message msg) {
    synchronized (Server.userThreads) {
      for (UserThread t : Server.userThreads) {
        try {
          if (t != null) t.sendMessageToClient(msg);
        } catch (Exception e) {
          log.error("Failed to send message", e);
        }
      }
    }
  }

  protected void stopWorking() {
    isWorking = false;
    try {
      socket.close();
    } catch (IOException e) {
      log.error("Failed to close socket", e);
    }

    log.info("User {} disconnected", username);

    synchronized (Server.usersList) {
      Server.usersList.remove(username);
    }
    broadcastUserList();
    synchronized (Server.userThreads) {
      Server.userThreads.remove(this);
    }
  }

  @Override
  public void run() {
    isWorking = true;
    try {
      sendMessageListToClient();
      performLogin();

      // === ДОБАВЛЯЕМ пользователя в список ПЕРЕД рассылкой ===
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
    } catch (Exception e) {
      log.error("Error in client thread ({})", username, e);
    } finally {
      stopWorking();
    }
  }
}