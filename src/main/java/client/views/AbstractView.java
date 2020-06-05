package client.views;

import Server.ChatMessServer;
import client.ChatMessengerApp;

import javax.swing.*;
import java.awt.*;

public abstract class AbstractView extends JPanel {
    protected static ChatMessengerApp parent;
    protected static ChatMessServer parentAdmin;
    public static void setParent(ChatMessengerApp parent) {
        AbstractView.parent = parent;
    }
    public static void setParentAdmin(ChatMessServer parent) {
        AbstractView.parentAdmin = parent;
    }
    public AbstractView() {
        super();
    }

    public abstract void initialize();
    public abstract void clearFields();

    protected void addLabeledFiled(JPanel panel, String labelText, Component field) {
        JLabel label = new JLabel(labelText);
        label.setLabelFor(field);
        panel.add(label);
        panel.add(field);
    }


}
