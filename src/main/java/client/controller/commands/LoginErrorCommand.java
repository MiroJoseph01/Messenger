package client.controller.commands;


import client.views.LoginPanelView;

public class  LoginErrorCommand implements Command {
    public static final String WRONG_NAME_ERROR = "WRONG";
    public static final String NAME_EXIST = "EXIST";
    private String error;
    private LoginPanelView view;
    public LoginErrorCommand(LoginPanelView view, String error) {
        this.error = error;
        this.view = view;
    }

    @Override
    public void execute() {
        view.setVisible(false);
        switch (error)
        {
            case WRONG_NAME_ERROR:
                view.getMainPanel().add(view.getErrorWrongNameLabel());
                view.getErrorWrongNameLabel().setVisible(true);
                view.setVisible(true);
                view.repaint();
                break;

            case NAME_EXIST:
                view.getMainPanel().add(view.getErrorExistNameLabel());
                view.getErrorExistNameLabel().setVisible(true);
                view.setVisible(true);
                view.repaint();
                break;
        }
    }
}
