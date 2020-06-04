package client.controller.commands;

import client.ChatMessengerApp;
import client.controller.commands.Command;
import client.views.ChatPanelView;

public class BackChatViewCommnad implements Command {

    private ChatPanelView view;
    private ChatMessengerApp parent;

    public BackChatViewCommnad(ChatMessengerApp parent, ChatPanelView p) {
        this.parent=parent;
        view = p;
    }

    @Override
    public void execute() {
        parent.getModel().setReceiver("");
        parent.getModel().setLastMessageId(0);
        view.getMessagesTextPane().setText("");
        parent.getModel().getMessages().clear();
        view.getMessagesTextPane().setText(parent.getModel().messagesToString());
        view.getMainChatButton().setVisible(false);
        view.repaint();
    }
}
