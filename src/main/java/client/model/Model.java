package client.model;

import client.ChatMessengerApp;
import domain.Message;

import javax.swing.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Model {

    private ChatMessengerApp parent;
    private String currentUser;
    private String loggedUser;
    private String lastMessageText;
    private String receiver = "";
    private long lastMessageId;
    private Set<Message> messages;
    private List<String> onlineUsers = new ArrayList<>();




    private DefaultListModel listModel = new DefaultListModel(){};
    private String serverIPAddress = "127.0.0.1";

    private static class ModelHolder {
        private static final Model INSTANCE = new Model();
    }

    public static Model getInstance() {
        return ModelHolder.INSTANCE;
    }

    public void initialize() {
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
        lastMessageId = 0L;
        currentUser = "";
        loggedUser = "";
        receiver="";
        lastMessageText = "";
    }

    private Model(){  }

    public DefaultListModel getListModel() {
        return listModel;
    }

    public void setListModel(DefaultListModel listModel) {
        this.listModel = listModel;
    }

    public String messagesToString() {
        System.out.println(messages.toString());
        return messages.toString();

    }

    public long getLastMessageId() {
        return lastMessageId;
    }

    public void setLastMessageId(long lastMessageId) {
        this.lastMessageId = lastMessageId;
    }

    public void addMessages(List<Message> messages) {
        this.getMessages().addAll(messages);
        parent.getChatPanelView(false)
            .modelChangedNotification(messages.toString());
    }

    public void addUsers(List<String> users) {
        boolean added = false;
        boolean deleted = false;

        //add
        for (String i : users)
        {
            if (!listModel.contains(i) && !i.equals(currentUser)) {
                listModel.addElement(i);
                if (!added) added = true;
            }
        }
        //delete
        for (int i = 0; i < listModel.size(); i++)
        {
            if (!users.contains(listModel.get(i)))
            {
                listModel.remove(i);
                if (!deleted) deleted = true;
            }
        }
        if (deleted || added)
        {
            parent.getChatPanelView(false).updateUsersLabel();
        }
    }



    public List<String> getUserOnline() {
        return onlineUsers;
    }
    public boolean isContainUserName(String name)
    {
        return onlineUsers.contains(name);
    }
    public void setUserOnline(List<String> userOnline) {
        this.onlineUsers = userOnline;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public ChatMessengerApp getParent() {
        return parent;
    }
    public void setParent(ChatMessengerApp parent) {
        this.parent = parent;
    }
    public String getCurrentUser() {
        return currentUser;
    }
    public void setCurrentUser(String currentUser) {
        this.currentUser = currentUser;
    }
    public String getLoggedUser() {
        return loggedUser;
    }
    public void setLoggedUser(String loggedUser) {
        this.loggedUser = loggedUser;
    }
    public String getLastMessageText() {
        return lastMessageText;
    }
    public void setLastMessageText(String lastMessageText) {
        this.lastMessageText = lastMessageText;
    }
    public Set<Message> getMessages() {
        return messages;
    }
    public void setMessages(Set<Message> messages) {
        this.messages = messages;
    }
    public String getServerIPAddress() { return serverIPAddress; }
    public void setServerIPAddress(String serverIPAddress) { this.serverIPAddress = serverIPAddress;}
}
