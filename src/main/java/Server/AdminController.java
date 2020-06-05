package Server;

import client.util.Utility;
import client.views.ChatPanelView;
import domain.Message;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;

public class AdminController implements ActionListener {
    private ChatMessServer server;
    private AdminView view;
    public AdminController(ChatMessServer server)
    {
        this.server=server;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        AdminView view = Utility.findParent(
                (Component) actionEvent.getSource(), AdminView.class);

        Set<Message> list = new HashSet<Message>();
        list.addAll(server.getValues());
        server.setMessages(list);
        view.modelChangedNotification(server.getMessages().toString());
        System.out.println(list);
            view.getMessagesTextPane().setText(server.getValues().toString());
        view.getRes().clear();
        view.getRes().addElement("No Receiver");
        System.out.println(server.getUsers());
        for (String i: server.getUsers()) {
            view.getRes().addElement(i);
        }
    }
}
