package client.util;

import Server.AdminView;
import Server.ChatMessServer;
import client.ChatMessengerApp;
import client.model.Model;
import domain.Message;
import domain.xml.MessageParser;
import lombok.extern.slf4j.Slf4j;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.awt.*;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static Server.ServerThread.*;


@Slf4j
public class Utility {
    public static <T extends Container> T findParent(Component comp, Class<T> clazz) {
        if (comp == null) {
            return null;
        }
        if (clazz.isInstance(comp)) {
            return (clazz.cast(comp));
        } else
            return findParent(comp.getParent(), clazz);
    }

    public static void usersUpdate(ChatMessengerApp app) {
        InetAddress addr;
        try {
            addr = InetAddress.getByName(app.getModel().getServerIPAddress());
            try (Socket socket = new Socket(addr, ChatMessServer.PORT);
                 PrintWriter out = new PrintWriter(
                         new BufferedWriter(
                                 new OutputStreamWriter(
                                         socket.getOutputStream()
                                 )
                         ), true
                 );
                 BufferedReader in = new BufferedReader(
                         new InputStreamReader(
                                 socket.getInputStream()
                         )
                 );) {
                Model model = app.getModel();
                out.println(METHOD_GET);
                out.println(METHOD_GET_USERS);
                out.println(model.getCurrentUser());
                out.flush();

                String responseline = in.readLine();
                String names = "";

                while (!END_LINE_MESSAGE.equals(responseline)) {
                    names = responseline;
                    responseline = in.readLine();
                }
                if (!names.equals("")) {
                    model.addUsers(Arrays.asList(names.toString().split(",")));
                }
            } catch (IOException e) {
                log.error("Socket error: " + e.getMessage());
            }

        } catch (UnknownHostException e) {
            log.error("Unknown host address" + e.getMessage());
        }
    }

    public static void messagesUpdate(ChatMessengerApp app) {
        InetAddress addr;
        try {
            addr = InetAddress.getByName(app.getModel().getServerIPAddress());
            try (Socket socket = new Socket(addr, ChatMessServer.PORT);
                 PrintWriter out = new PrintWriter(
                         new BufferedWriter(
                                 new OutputStreamWriter(
                                         socket.getOutputStream()
                                 )
                         ), true
                 );
                 BufferedReader in = new BufferedReader(
                         new InputStreamReader(
                                 socket.getInputStream()
                         )
                 );) {
                Model model = app.getModel();
                out.println(METHOD_GET);
                out.println(METHOD_GET_MESSAGES);
                out.println(model.getLastMessageId());
                out.println(model.getCurrentUser());
                out.println(model.getReceiver());
                out.flush();

                String responeLine = in.readLine();
                StringBuilder mesStr = new StringBuilder();

                while (!END_LINE_MESSAGE.equals(responeLine)) {
                    mesStr.append(responeLine);
                    responeLine = in.readLine();
                }

                SAXParserFactory parserFactory = SAXParserFactory.newInstance();
                SAXParser parser = parserFactory.newSAXParser();

                List<Message> messages = new ArrayList<Message>() {
                    @Override
                    public String toString() {
                        return this.stream()
                                .map(Message::toString)
                                .collect(Collectors.joining("\n"));

                    }
                };
                AtomicInteger id = new AtomicInteger(0);
                MessageParser saxp = new MessageParser(id, messages);
                parser.parse(new ByteArrayInputStream(mesStr.toString().getBytes()), saxp);


                if (messages.size() > 0) {
                        model.addMessages(messages);
                    model.setLastMessageId(id.longValue());
                    log.trace("List of new messages: " + messages.toString());
                }

            } catch (IOException e) {
                log.error("Socket error: " + e.getMessage());
            } catch (SAXException | ParserConfigurationException e) {
                e.printStackTrace();
                log.error("Parse exception: " + e.getMessage());
            }

        } catch (UnknownHostException e) {
            log.error("Unknown host address" + e.getMessage());
        }
    }

    public static void deleteUser(ChatMessengerApp app) {
        InetAddress addr;
        String result;
        try {
            addr = InetAddress.getByName(app.getModel().getServerIPAddress());
            try (Socket socket = new Socket(addr, ChatMessServer.PORT);
                 PrintWriter out = new PrintWriter(
                         new BufferedWriter(
                                 new OutputStreamWriter(
                                         socket.getOutputStream()
                                 )
                         ), true
                 );
                 BufferedReader in = new BufferedReader(
                         new InputStreamReader(
                                 socket.getInputStream()
                         )
                 );) {
                do {
                    Model model = app.getModel();
                    out.println(METHOD_DELETE);
                    out.println(model.getLoggedUser());

                    out.flush();
                    result = in.readLine();
                } while (!"OK".equals(result));

            } catch (IOException e) {
                log.error("Socket error: " + e.getMessage());
            }
        } catch (UnknownHostException e) {
            log.error("Unknown host address" + e.getMessage());
        }
    }
    
}
