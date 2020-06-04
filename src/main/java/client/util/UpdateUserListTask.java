package client.util;

import client.ChatMessengerApp;
import client.util.Utility;

import java.util.TimerTask;

public class UpdateUserListTask extends TimerTask {
    ChatMessengerApp app;
    public UpdateUserListTask(ChatMessengerApp app) {
        this.app = app;
    }

    @Override
    public void run() {
        Utility.usersUpdate(app);
    }
}
