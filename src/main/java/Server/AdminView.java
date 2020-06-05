package Server;

import client.views.AbstractView;
import client.views.ChatPanelView;
import domain.Message;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Element;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import java.awt.*;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class AdminView extends AbstractView {
    private ChatMessServer parent;
    private JPanel messagesMainPanel; //главная панель
    private JPanel usersListPanel;
    private JPanel messagesPanel; //панель сообщений
    private JScrollPane messagesListPanel; //панель текстового поля для сообщений
    private JTextPane messagesTextPane; //само поле сообщения
    private JList<String> usersJlist;
    private JButton update;

    private AdminView() {
        super();
        initialize();
    }

    public void modelChangedNotification(String newMessages) {
        if (newMessages.length() != 0) {
            log.trace("New messages arrived: " + newMessages);
            HTMLDocument document = (HTMLDocument) getMessagesTextPane().getStyledDocument();
            Element element = document.getElement(document.getRootElements()[0],
                    HTML.Attribute.ID, "body");
            try {
                document.insertBeforeEnd(element, newMessages);
            } catch (BadLocationException | IOException e) {
                log.error("Bad location error: " + e.getMessage());
            }
            getMessagesTextPane().setCaretPosition(document.getLength());
            log.trace("Messages text update");
        }
    }

    public void initModel(boolean getMessages, String text) {
        if (getMessages) {
            getMessagesTextPane().setText(text);
        }

    }

    public static AdminView getInstance() {
        return AdminView.AdminViewHolder.INSTANCE;
    }

    private static class AdminViewHolder {
        private static final AdminView INSTANCE = new AdminView();
    }

    @Override
    public void initialize() {
        this.setName("adminView");
        this.setLayout(new BorderLayout());
        this.add(getMessagesMainPanel(), BorderLayout.CENTER);
    }

    @Override
    public void clearFields() {
        getMessagesTextPane().setText("");
    }

    public JButton getUpdate(){
        if(update==null){
            update=new JButton();
            update.setText("Update");
            update.setName("updateButton");
            update.addActionListener(new AdminController(parentAdmin));
        }
        return  update;
    }

    public JPanel getMessagesMainPanel() {

        if (messagesMainPanel == null) {
            messagesMainPanel = new JPanel();
            messagesMainPanel.setLayout(new BorderLayout());
            messagesMainPanel.add(getMessagesPanel());
            messagesMainPanel.add(getMessagesListPanel());
            messagesMainPanel.add(getUsersListPanel(), BorderLayout.EAST);


        }
        return messagesMainPanel;

    }

    public JPanel getUsersListPanel() {
        if (usersListPanel == null) {

            usersListPanel = new JPanel();

            ////JLabel l = getUsersLabel();
           // l.setFont(new Font(Font.SERIF, Font.PLAIN, 14));

            JPanel JListPanel = new JPanel();
            JListPanel.setLayout(new BorderLayout());
            JListPanel.add(getUsersJlist(), BorderLayout.CENTER);

            JPanel userPanel = new JPanel();
            userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));
            userPanel.setPreferredSize(new Dimension(200, getHeight()));
            //userPanel.add(l);
            userPanel.add(JListPanel);

            usersListPanel.setLayout(new BorderLayout());
            usersListPanel.setBorder(BorderFactory.createLineBorder(Color.black));
            usersListPanel.add(userPanel, BorderLayout.CENTER);
            usersListPanel.setPreferredSize(new Dimension(200, getHeight()));
            usersListPanel.add(getUpdate(), BorderLayout.SOUTH);

           // usersListPanel.add(getMainChatButton(), BorderLayout.SOUTH);
        }
        return usersListPanel;
    }



    public DefaultListModel<String> res;

    public DefaultListModel<String> getRes() {
        return res;
    }

    public void setRes(DefaultListModel<String> res) {
        this.res = res;
    }

    public JList<String> getUsersJlist() {
        if (usersJlist == null) {
            setRes(new DefaultListModel<>());
            getRes().addElement("No Receiver");
            for (String i: parentAdmin.getUsers())
            {
                getRes().addElement(i);
            }
                usersJlist = new JList<String>(res);
                usersJlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            usersJlist.setPreferredSize(new Dimension(200, getHeight()));
            usersJlist.setFont(new Font(Font.SERIF, Font.PLAIN, 14));
            DefaultListCellRenderer renderer = (DefaultListCellRenderer) usersJlist.getCellRenderer();
            renderer.setHorizontalAlignment(SwingConstants.CENTER);

            ListSelectionListener listSelectionListener = new ListSelectionListenerAdmin(parentAdmin);
            usersJlist.addListSelectionListener(listSelectionListener);

        }
        return usersJlist;
    }


    public JPanel getMessagesPanel() {
        if (messagesPanel == null) {
            messagesPanel = new JPanel();
            messagesPanel.setLayout(new BorderLayout());
            messagesPanel.setPreferredSize(new Dimension(300, getHeight()));
            messagesPanel.setFont(new Font(Font.SERIF, Font.PLAIN, 14));
        }
        return messagesPanel;
    }


    public JScrollPane getMessagesListPanel() {
        if (messagesListPanel == null) {
            messagesListPanel = new JScrollPane(getMessagesTextPane());
            messagesListPanel
                    .setVerticalScrollBarPolicy(
                            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        }
        return messagesListPanel;
    }

    public JTextPane getMessagesTextPane() {
        if (messagesTextPane == null) {
            messagesTextPane = new JTextPane();
            messagesTextPane.setContentType("text/html");
            messagesTextPane.setEditable(false);
            messagesTextPane.setName("messagesTextArea");
            ((DefaultCaret) messagesTextPane.getCaret())
                    .setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        }
        return messagesTextPane;

    }

}
