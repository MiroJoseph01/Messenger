package Server;


import client.views.AbstractView;
import client.views.ViewFactory;
import domain.Message;
import domain.xml.MessageBuilder;
import domain.xml.MessageParser;

import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;


import javax.swing.*;
import javax.xml.parsers.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
public class ChatMessServer extends JFrame {
    public static final int PORT = 7080;
    private static final int SERVER_TIMEOUT = 500;
    private static final String XML_FILE_NAME = "messages.xml";//?
    private static volatile boolean stop = false;
    private static AtomicInteger id = new AtomicInteger(0);
    private static Map<Long, Message> messagesList =
            Collections.synchronizedSortedMap(new TreeMap<Long, Message>());
    private static final ViewFactory VIEWS;
    private Set<Message> listMess;

    public static final long ADMIN_DELAY = 2000;
    public static final long ADMIN_PERIOD = 10000;
    public static final int WIDTH = 500;
    public static final int HEIGHT = 600;



    private static Timer timer;

    static {
        VIEWS = ViewFactory.getInstance();
    }

    public ChatMessServer() {
        super();
        initialize();
    }

    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
         ListOfClients clients = ListOfClients.getInstance();
        // Load xml files with prev messages
        loadMessageXMLFile();

        // Run thread with quit command handler
        quitCommandThread();
        // Create new Socket Server
        ServerSocket serverSocket = new ServerSocket(PORT);
        log.info("Server started on port: " + PORT);

        JFrame frame = new ChatMessServer();
        frame.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent we){
                ChatMessServer app = (ChatMessServer) frame;
                app.showAdminView();
            }
        });
        frame.setVisible(true);
        //frame.setResizable(false);
        frame.repaint();
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
        timer.cancel();
    }

    public void showAdminView() {
        showPanel(getAdminView(true));
    }

    public AdminView getAdminView(boolean doGetMessages) {
            AdminView adminView = VIEWS.getview("admin");
            adminView.initModel(doGetMessages, listMess.toString());
            return adminView;
    }

    public void setMessages(Set<Message> messages) {
        this.listMess = messages;
    }
    public Set<Message> getMessages(){return listMess;}

    public List<String> getUsers(){
       List<String> res = new ArrayList<String>();
        for (Message i: getValues()) {
            res.add(i.getUserNT());
            res.add(i.getUserNF());
        }
        List<String> temp = res.stream().distinct().filter(x->!x.equals("")).collect(Collectors.toList());
        res.clear();
        for (String i: temp) {
            for( String j: temp){
                if(i.equals(j)) continue;
                if(res.contains(j+","+i)){
                    continue;
                }
                res.add(i+","+j);
            }
        }

        res = res.stream().filter(x->{
            String[] r = x.split(",");
            return getValues().stream()
                    .anyMatch(y->y.getUserNT().equals(r[0])&&y.getUserNF().equals(r[1])||y.getUserNT().equals(r[1])&&y.getUserNF().equals(r[0]));
        }).collect(Collectors.toList());
        return res;
    }

    private void initialize() {
        AbstractView.setParentAdmin(this);
        VIEWS.viewRegister("admin", AdminView.getInstance());

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(WIDTH, HEIGHT);
        this.setLocationRelativeTo(null);
        this.setTitle("Chat Messenger Administration");

        setMessages(new TreeSet<Message>(){
            @Override
            public String toString() {
                StringBuilder result = new StringBuilder("<html><body id ='body'>");
                Iterator<Message> i = iterator();
                while (i.hasNext()){
                    result.append(i.next().toString()).append("\n");}
                return result.append("</body></html>").toString();
            }
        });


        Set<Message> list = new HashSet<Message>();
        list.addAll(messagesList.values());
        this.setMessages(list);
        getAdminView(true).modelChangedNotification(getMessages().toString());
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.add(getAdminView(true), BorderLayout.CENTER);
        this.setContentPane(contentPanel);
    }

    public Collection<Message> getValues(){
        return messagesList.values();
    }

    public static Timer getTimer() {
        return timer;
    }

    public static void setTimer(Timer timer) {
        ChatMessServer.timer = timer;
    }

    private void showPanel(JPanel panel) {
        getContentPane().add(panel, BorderLayout.CENTER);
        panel.setVisible(true);
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

