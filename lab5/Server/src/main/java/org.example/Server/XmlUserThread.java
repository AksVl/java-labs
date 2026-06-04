package org.example.Server;

import org.example.Message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.Socket;

public class XmlUserThread extends UserThread {
  private static final Logger log = LoggerFactory.getLogger(XmlUserThread.class);

  private DataInputStream in;
  private DataOutputStream out;

  public XmlUserThread(Socket socket) throws Exception {
    super(socket);
    out = new DataOutputStream(socket.getOutputStream());
    out.flush();
    in = new DataInputStream(socket.getInputStream());
  }

  private void sendXml(String xml) throws IOException {
    byte[] data = xml.getBytes("UTF-8");
    out.writeInt(data.length);
    out.write(data);
    out.flush();
  }

  private String readXml() throws IOException {
    int length = in.readInt();
    byte[] buf = new byte[length];
    in.readFully(buf);
    return new String(buf, "UTF-8");
  }

  private Document parseXml(String xml) throws Exception {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    return builder.parse(new InputSource(new StringReader(xml)));
  }

  @Override
  protected void performLogin() throws Exception {
    Document doc = parseXml(readXml());
    Element cmd = doc.getDocumentElement();
    if (!"login".equals(cmd.getAttribute("name")))
      throw new IllegalStateException("Expected <command name=\"login\">");
    username = cmd.getElementsByTagName("username").item(0).getTextContent();
  }

  @Override
  protected Message readNextMessage() throws Exception {
    Document doc = parseXml(readXml());
    Element cmd = doc.getDocumentElement();
    String name = cmd.getAttribute("name");
    if ("disconnect".equals(name)) return null;
    if ("message".equals(name)) {
      return new Message(
              cmd.getElementsByTagName("username").item(0).getTextContent(),
              cmd.getElementsByTagName("text").item(0).getTextContent(),
              cmd.getElementsByTagName("time").item(0).getTextContent());
    }
    log.warn("Unknown command from {}: {}", username, name);
    return null;
  }

  @Override
  protected void sendUserListToClient() throws Exception {
    StringBuilder sb = new StringBuilder("<command name=\"userlist\">");
    synchronized (Server.usersList) {
      for (String u : Server.usersList) sb.append("<user>").append(u).append("</user>");
    }
    sb.append("</command>");
    sendXml(sb.toString());
    log.debug("Sent user list to {} (XML)", username);
  }

  @Override
  protected void sendMessageListToClient() throws Exception {
    StringBuilder sb = new StringBuilder("<command name=\"messagelist\">");
    synchronized (Server.messages) {
      for (Message m : Server.messages) {
        sb.append("<text>").append(m.getText()).append("</text>")
                .append("<username>").append(m.getUsername()).append("</username>")
                .append("<time>").append(m.getSendingTime()).append("</time>");
      }
    }
    sb.append("</command>");
    sendXml(sb.toString());
  }

  @Override
  protected void sendMessageToClient(Message msg) throws Exception {
    StringBuilder sb = new StringBuilder("<command name=\"message\">")
            .append("<username>").append(msg.getUsername()).append("</username>")
            .append("<text>").append(msg.getText()).append("</text>")
            .append("<time>").append(msg.getSendingTime()).append("</time>")
            .append("</command>");
    sendXml(sb.toString());
    log.info("Sent message to {} (XML): {}", username, msg.getText());
  }
}