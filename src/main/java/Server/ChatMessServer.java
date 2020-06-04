package Server;


import domain.Message;
import domain.xml.MessageBuilder;
import domain.xml.MessageParser;

import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;


import javax.xml.parsers.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class ChatMessServer {
    public static final int PORT = 7080;
    private static final int SERVER_TIMEOUT = 500;
    private static final String XML_FILE_NAME = "messages.xml";//?
    private static volatile boolean stop = false;
    private static AtomicInteger id = new AtomicInteger(0);
    private static Map<Long, Message> messagesList =
            Collections.synchronizedSortedMap(new TreeMap<Long, Message>());

    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
         ListOfClients clients = ListOfClients.getInstance();
        // Load xml files with prev messages
        loadMessageXMLFile();

        // Run thread with quit command handler
        quitCommandThread();

        // Create new Socket Server
        ServerSocket serverSocket = new ServerSocket(PORT);
        log.info("Server started on port: " + PORT);

        // loop of request in sockets with timeout
        while (!stop)
        {
            serverSocket.setSoTimeout(SERVER_TIMEOUT);
            Socket socket;
            try {
                socket = serverSocket.accept();
                try {
                    new ServerThread(socket, id, messagesList, clients);
                } catch (IOException e) {
                    log.error("IO error");
                    socket.close();
                }

            } catch (SocketTimeoutException e) { //
            }
        }

        // Write messange into xml file
        saveMessagesXMLFile();
        log.info("Server stopped");
        serverSocket.close();
    }

    private static void saveMessagesXMLFile() throws ParserConfigurationException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.newDocument();
        String xmlContent = MessageBuilder.buildDocument(document, messagesList.values());

        OutputStream stream  = new FileOutputStream(new File(XML_FILE_NAME));
        OutputStreamWriter out = new OutputStreamWriter(stream, StandardCharsets.UTF_8);
        out.write(xmlContent + "\n");
        out.flush();
        out.close();
    }

    private static void loadMessageXMLFile() throws ParserConfigurationException, SAXException, IOException {
        SAXParserFactory  factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        List<Message> messages = new ArrayList<>();
        MessageParser saxp = new MessageParser(id, messages);
        Path str = (Paths.get(XML_FILE_NAME)).toAbsolutePath();
        InputStream is = new ByteArrayInputStream(
                Files.readAllBytes(str));

        parser.parse(is,saxp);
        for (Message message: messages)
        {
            messagesList.put(message.getId(), message); //??
        }
        id.incrementAndGet();
        is.close();
    }

    private static void quitCommandThread() {
        new Thread() {
            @Override
            public void run() {
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                while(true)
                {
                    String buf;
                    try {
                        buf = br.readLine();
                        if ("quit".equals(buf)) {
                            stop = true;
                            break;
                        } else
                        {
                            log.warn("Type 'quit' for exit termination");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }
}

