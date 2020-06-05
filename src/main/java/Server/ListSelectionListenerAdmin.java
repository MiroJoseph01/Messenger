package Server;


import client.util.Utility;
import client.views.ChatPanelView;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.stream.Collectors;

public class ListSelectionListenerAdmin implements ListSelectionListener {
    private ChatMessServer parent;
    public ListSelectionListenerAdmin(ChatMessServer parent){
        this.parent=parent;
    }
    @Override
    public void valueChanged(ListSelectionEvent listSelectionEvent) {
        AdminView view = Utility.findParent(
                (Component) listSelectionEvent.getSource(), AdminView.class);
        if(!listSelectionEvent.getValueIsAdjusting()){
            JList list = (JList) listSelectionEvent.getSource();
            Object selectionValues[] = list.getSelectedValues();
            for (int i = 0, n = selectionValues.length; i < n; i++) {
                ChatOoO((String) selectionValues[i], view);
                view.getUsersJlist().clearSelection();
            }
        }
    }
    private final void ChatOoO(String receiver, AdminView view){
        if(receiver.equals("No Receiver")){
            view.getMessagesTextPane().setText(parent.getMessages().stream().filter(y->y.getUserNT().equals(""))
                    .collect(Collectors.toSet()).toString());
        }
        else {
            String[] r= receiver.split(",");
            view.getMessagesTextPane().setText(parent.getMessages().stream().filter(y->y.getUserNT().equals(r[0])&&y.getUserNF().equals(r[1])||y.getUserNT().equals(r[1])&&y.getUserNF().equals(r[0]))
                    .collect(Collectors.toSet()).toString());
        }

    }
}
