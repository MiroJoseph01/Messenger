package client.views;

import java.util.HashMap;
import java.util.Map;

public class ViewFactory {
    private Map<String, AbstractView> instances;

    //Singleton pattern
    private ViewFactory() {
        instances = new HashMap<>();
    }

    public static ViewFactory getInstance() {
        return ViewFactoryHolder.INSTANCE;
    }

    private static class ViewFactoryHolder {
        private static final ViewFactory INSTANCE = new ViewFactory();
    }

    public void viewRegister(String name, AbstractView view) {
        instances.put(name, view);
    }

    public <T extends AbstractView> T getview(String name) {
        return (T) instances.get(name);
    }
}
