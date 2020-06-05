package client.controller;

import Server.ChatMessServer;
import client.ChatMessengerApp;
import client.util.Utility;
import client.views.ChatPanelView;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;

public class ListSelectionListenerApp implements javax.swing.event.ListSelectionListener {
    private ChatMessengerApp parent;
    public ListSelectionListenerApp(ChatMessengerApp parent){
        this.parent=parent;
    }
    @Override
    public void valueChanged(ListSelectionEvent listSelectionEvent) {
        ChatPanelView view = Utility.findParent(
                (Component) listSelectionEvent.getSource(), ChatPanelView.class);
        if(!listSelectionEvent.getValueIsAdjusting()){
            JList list = (JList) listSelectionEvent.getSource();
            Object selectionValues[] = list.getSelectedValues();
            for (int i = 0, n = selectionValues.length; i < n; i++) {
                ChatOoO(parent.getModel().getCurrentUser(),(String) selectionValues[i], view);
                view.getUsersJlist().clearSelection();
            }
        }
    }

    private final void ChatOoO(String sender, String receiver, ChatPanelView view){
        parent.getModel().setReceiver(receiver);
        parent.getModel().setLastMessageId(0);
        view.getMessagesTextPane().setText("");
        parent.getModel().getMessages().clear();
        view.getMessagesTextPane().setText(parent.getModel().messagesToString());
        view.getMainChatButton().setVisible(true);

    }

}
