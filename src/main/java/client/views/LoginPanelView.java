package client.views;

import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;

@Slf4j
public class LoginPanelView extends AbstractView {

    public static final String ACTION_COMMAND_LOGIN = "login";
    private JPanel loginPanel; //отображает все окно
    private  JPanel mainPanel; //поля ввода
    private JButton loginButton;
    private JTextField userNameField;
    private JTextField serverIpAddressField;
    private JLabel errorWrongNameLabel;
    private JLabel errorExistNameLabel;


    //Singleton pattern
    private LoginPanelView() {
        super();
        initialize();
    }

    public static LoginPanelView getInstance() {
        return LoginPanelViewHolder.INSTANCE;
    }

    private static class LoginPanelViewHolder {
        private static final LoginPanelView INSTANCE = new LoginPanelView();
    }

    @Override
    public void initialize() {
        this.setName("loginPanelView");
        this.setLayout(new BorderLayout());
        this.add(getLoginPanel(), BorderLayout.CENTER);
        clearFields();
        initModel();
        InputMap im = getLoginButton().getInputMap();
        im.put(KeyStroke.getKeyStroke("ENTER"), "pressed");
        im.put(KeyStroke.getKeyStroke("released ENTER"), "released");
    }

    @Override
    public void clearFields() {
        getErrorWrongNameLabel().setVisible(false);
        getUserNameField().setText("");
        getServerIpAddressField().setText(parent.getModel().getServerIPAddress());
    }

    public void initModel() {
        parent.getModel().setCurrentUser("");
        parent.getModel().setLoggedUser("");
        getUserNameField().requestFocusInWindow();
        parent.getRootPane().setDefaultButton(getLoginButton());

    }

    public JPanel getLoginPanel() {
        if (loginPanel == null) {
            loginPanel = new JPanel();
            loginPanel.setLayout(new BorderLayout());
            loginPanel.add(getMainPanel(), BorderLayout.NORTH);
            addLabeledFiled(getMainPanel(), "name of user:", getUserNameField());
            addLabeledFiled(getMainPanel(), "server ip-address", getServerIpAddressField());
            loginPanel.add(getLoginButton(), BorderLayout.SOUTH);
        }
        return loginPanel;
    }

    public JPanel getMainPanel() {
        if (mainPanel == null) {
            mainPanel = new JPanel();
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        }
        return mainPanel;
    }

    public JButton getLoginButton() {
        if (loginButton == null) {
            loginButton = new JButton();
            loginButton.setText("Login...");
            loginButton.setName("loginButton");
            loginButton.setActionCommand(ACTION_COMMAND_LOGIN);
            loginButton.addActionListener(parent.getController());
        }
        return loginButton;
    }

    public JTextField getUserNameField() {
        if (userNameField == null) {
            userNameField = new JTextField(12);
            userNameField.setName("userNameField");
        }
        return userNameField;
    }

    public JTextField getServerIpAddressField() {
        if (serverIpAddressField == null) {
            serverIpAddressField = new JTextField(12);
            serverIpAddressField.setName("serverIpAddressField");
        }
        return serverIpAddressField;
    }

    public JLabel getErrorWrongNameLabel() {
        if (errorWrongNameLabel == null)
            errorWrongNameLabel = new JLabel("Wrong server ip address or user name");
        errorWrongNameLabel.setForeground(Color.red);
        return errorWrongNameLabel;
    }

    public JLabel getErrorExistNameLabel() {
        if (errorExistNameLabel == null)
            errorExistNameLabel = new JLabel("This name already exist");
        errorExistNameLabel.setForeground(Color.red);
        return errorExistNameLabel;
    }

    public void setErrorExistNameLabel(JLabel errorExistNameLabel) {
        this.errorExistNameLabel = errorExistNameLabel;
    }

    private void setErrorWrongNameLabelText(String errorText) {
        getErrorWrongNameLabel().setText(errorText);
    }
}
