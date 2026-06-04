package org.example.Client;

import org.example.Message.Message;
import org.example.View.ClientWindow;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StringReader;
import java.util.ArrayList;

public class ReaderThread extends Thread {
    private DataInputStream xmlIn;
    private ObjectInputStream objIn;
    private ClientWindow window;
    private boolean isXmlMode;
    private boolean isWorking;

    public ReaderThread(DataInputStream xmlIn, ClientWindow window) {
        this.xmlIn = xmlIn;
        this.window = window;
        this.isXmlMode = true;
    }

    public ReaderThread(ObjectInputStream objIn, ClientWindow window) {
        this.objIn = objIn;
        this.window = window;
        this.isXmlMode = false;
    }

    public void stopWorking() { isWorking = false; }

    private String readXmlString() throws IOException {
        int length = xmlIn.readInt();
        byte[] buf = new byte[length];
        xmlIn.readFully(buf);
        return new String(buf, "UTF-8");
    }

    private Document parseXml(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new InputSource(new StringReader(xml)));
    }

    private void processXmlMessage(Document doc) {
        Element cmd = doc.getDocumentElement();
        String name = cmd.getAttribute("name");
        if ("message".equals(name)) {
            String text = cmd.getElementsByTagName("text").item(0).getTextContent();
            String time = cmd.getElementsByTagName("time").item(0).getTextContent();
            String user = cmd.getElementsByTagName("username").item(0).getTextContent();
            window.addMessage(new Message(user, text, time));
        } else if ("userlist".equals(name)) {
            ArrayList<String> users = new ArrayList<>();
            var nodes = cmd.getElementsByTagName("user");
            for (int i = 0; i < nodes.getLength(); i++) {
                String u = nodes.item(i).getTextContent();
                if (u != null && !u.isEmpty()) users.add(u);
            }
            window.updateUserList(users);
        } else if ("messagelist".equals(name)) {
            var texts = cmd.getElementsByTagName("text");
            var users = cmd.getElementsByTagName("username");
            var times = cmd.getElementsByTagName("time");
            for (int i = 0; i < texts.getLength(); i++) {
                window.addMessage(new Message(
                        users.item(i).getTextContent(),
                        texts.item(i).getTextContent(),
                        times.item(i).getTextContent()));
            }
        }
    }

    private void processObjectMessage() throws Exception {
        String answer = (String) objIn.readObject();
        if ("message".equals(answer)) {
            Message msg = (Message) objIn.readObject();
            window.addMessage(msg);
        } else if ("userlist".equals(answer)) {
            StringBuilder str = (StringBuilder) objIn.readObject();
            ArrayList<String> users = new ArrayList<>();
            StringBuilder word = new StringBuilder();
            for (int i = 0; i < str.length(); i++) {
                char c = str.charAt(i);
                if (c == '$') {
                    users.add(word.toString());
                    word.setLength(0);
                } else {
                    word.append(c);
                }
            }
            window.updateUserList(users);
        }
    }

    @Override
    public void run() {
        isWorking = true;
        while (isWorking) {
            try {
                if (isXmlMode) {
                    processXmlMessage(parseXml(readXmlString()));
                } else {
                    processObjectMessage();
                }
            } catch (Exception e) {
                if (isWorking) throw new RuntimeException(e);
            }
        }
    }
}