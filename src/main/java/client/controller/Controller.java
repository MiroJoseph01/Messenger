package client.controller;

import Server.AdminView;
import client.*;
import client.controller.commands.*;
import client.model.Model;
import client.util.Utility;
import client.views.ChatPanelView;
import client.views.LoginPanelView;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.commons.validator.routines.InetAddressValidator;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;

import static client.views.ChatPanelView.*;
import static client.views.LoginPanelView.ACTION_COMMAND_LOGIN;

@Slf4j
public class Controller implements ActionListener {
    private ChatMessengerApp parent;
    private Command command;

    private Controller() {
    }

    public static Controller getInstance() {
        return ControllerHolder.INSTANCE;
    }

    private static class ControllerHolder {
        private static final Controller INSTANCE = new Controller();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            action(e);
        } catch (ParseException parseEx) {
            log.error(parseEx.getMessage());
        }
        command.execute();
    }

    private void action(ActionEvent e) throws ParseException {
        String commandName = e.getActionCommand();
        switch (commandName) {
            case ACTION_COMMAND_LOGIN: {
                Model model = parent.getModel();

                LoginPanelView view = Utility.findParent(
                        (Component) e.getSource(), LoginPanelView.class);

                if (! EmailValidator.getInstance().isValid(view.getUserNameField().getText()) ||
                        !InetAddressValidator.getInstance().isValid(view.getServerIpAddressField().getText())) {
                    command = new LoginErrorCommand(view, "WRONG");
                }
                else {

                    model.setCurrentUser(view.getUserNameField().getText());

                    Utility.usersUpdate(parent);

                    if (!model.isContainUserName(model.getCurrentUser()))
                    {
                        model.setServerIPAddress(view.getServerIpAddressField().getText());
                        command = new ShowChatViewCommand(parent, view);
                    }

                    else {
                        model.setCurrentUser("");
                        command = new LoginErrorCommand(view, "EXIST");
                    }

                }
            }
            break;
            case SEND_ACTION_COMMAND: {
                ChatPanelView view = Utility.findParent(
                        (Component) e.getSource(), ChatPanelView.class);
                parent.getModel().setLastMessageText(view.getTextMessageField().getText());
                command = new SendMessageCommand(parent, view);
            }
            break;


            case LOGOUT_ACTION_COMMAND: {
                ChatPanelView view = Utility.findParent(
                        (Component) e.getSource(), ChatPanelView.class);
                Utility.deleteUser(parent);
                parent.getModel().initialize();
                command = new ShowLoginVewCommand(parent, view);
            }
            break;
            case BACK_ACTION_COMMAND:{
                ChatPanelView view = Utility.findParent(
                        (Component) e.getSource(), ChatPanelView.class);
                command = new BackChatViewCommnad(parent,view);
            }
            break;
            default:
                throw new ParseException("Unknown command: " + commandName, 0);

        }
    }


    public void setParent(ChatMessengerApp parent) {
        this.parent = parent;
    }
}
