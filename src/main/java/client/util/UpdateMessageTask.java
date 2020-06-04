package client.util;

import client.ChatMessengerApp;
import client.util.Utility;

import java.util.TimerTask;

public class UpdateMessageTask extends TimerTask {
    ChatMessengerApp app;
    String receiver;
    public UpdateMessageTask(ChatMessengerApp app) {
        this.app = app;
    }

    @Override
    public void run() {
        Utility.messagesUpdate(app);
    }
}
