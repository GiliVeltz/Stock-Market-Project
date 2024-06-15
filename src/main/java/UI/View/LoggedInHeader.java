package UI.View;

public class LoggedInHeader extends Header {

    public LoggedInHeader(String serverPort) {
        super(serverPort);
        createLogoutButton();
        hideRegisterButton();
    }

}
