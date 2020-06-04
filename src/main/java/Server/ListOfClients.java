package Server;

import lombok.extern.slf4j.Slf4j;

import java.net.Socket;
import java.util.*;

@Slf4j
public class ListOfClients {
    //private HashMap<Socket,String> userNames = new HashMap<Socket, String>();
    private List<String> userNames = new ArrayList<>();
    private ListOfClients() {
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        if (userNames.size() == 0)
            return "";
        for (int i = 0; i < userNames.size() - 1; i++) {
            str.append(userNames.get(i)).append(",");
        }
        str.append(userNames.get(userNames.size() - 1));
        return str.toString();
    }

    public List<String> getUserNames() {
        return userNames;
    }

    public void addName(String name) {
        userNames.add(name);
    }
    public void removeName(String name) {
        userNames.remove(name);
    }
    public boolean ContainsName(String name) {
        return userNames.contains(name);
    }

    public static ListOfClients getInstance() {
        return ListOfClientsHolder.INSTANCE;
    }

    private static class ListOfClientsHolder {
        private static final ListOfClients INSTANCE = new ListOfClients();
    }
}
