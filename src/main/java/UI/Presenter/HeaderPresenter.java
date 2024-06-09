package UI.Presenter;

import java.net.http.HttpHeaders;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;

import UI.Model.UserDto;
import UI.View.Header;

public class HeaderPresenter {

    private final String _serverPort;
    private final Header view;

    public HeaderPresenter(Header view, String serverPort) {
        this.view = view;
        this._serverPort = serverPort;
    }

    public void onLoginButtonClick() {
        view.getLoginButton().setText("Logout");
        view.getLoginButton().addClickListener(event -> logout());
    }

    public void onRegisterButtonClick() {
        view.getRegisterButton().addClickListener(event -> register());
    }

    private void logout() {
        view.getLoginButton().setText("Login");
        UI.getCurrent().getPage().executeJs("localStorage.removeItem('authToken');");
        Notification.show("Logged out");
    }

    private void register() {
        Dialog registrationDialog = view.createRegistrationDialog();
        registrationDialog.open();
    }

    public void loginUser(String username, String password) {
        RestTemplate restTemplate = new RestTemplate();
        UserDto userDto = new UserDto(username, "", password);

        UI.getCurrent().getPage().executeJs("return localStorage.getItem('authToken');")
                .then(String.class, token -> {
                    if (token != null && !token.isEmpty()) {
                        System.out.println("Token: " + token);

                        HttpHeaders headers = new HttpHeaders();
                        headers.add("Authorization", token);

                        HttpEntity<UserDto> requestEntity = new HttpEntity<>(userDto, headers);

                        ResponseEntity<String> response = restTemplate.exchange(
                                "http://localhost:" + _serverPort + "/api/user/login",
                                HttpMethod.POST,
                                requestEntity,
                                String.class);

                        if (response.getStatusCode().is2xxSuccessful()) {
                            view.showSuccessMessage("Login successful");
                            System.out.println(response.getBody());
                        } else {
                            view.showErrorMessage("Login failed");
                        }
                    } else {
                        System.out.println("Token not found in local storage.");
                        view.showErrorMessage("Login failed");
                    }
                });
    }

    public void registerUser(String username, String email, String password) {
        RestTemplate restTemplate = new RestTemplate();
        UserDto userDto = new UserDto(username, email, password);

        UI.getCurrent().getPage().executeJs("return localStorage.getItem('authToken');")
                .then(String.class, token -> {
                    if (token != null && !token.isEmpty()) {
                        System.out.println("Token: " + token);

                        HttpHeaders headers = new HttpHeaders();
                        headers.add("Authorization", token);

                        HttpEntity<UserDto> requestEntity = new HttpEntity<>(userDto, headers);

                        ResponseEntity<String> response = restTemplate.exchange(
                                "http://localhost:" + _serverPort + "/api/user/register",
                                HttpMethod.POST,
                                requestEntity,
                                String.class);

                        if (response.getStatusCode().is2xxSuccessful()) {
                            view.showSuccessMessage("Registration successful");
                            System.out.println(response.getBody());
                        } else {
                            view.showErrorMessage("Registration failed");
                        }
                    } else {
                        System.out.println("Token not found in local storage.");
                        view.showErrorMessage("Registration failed");
                    }
                });
    }
}

