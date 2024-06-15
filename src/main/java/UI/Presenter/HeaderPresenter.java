package UI.Presenter;


import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.vaadin.flow.component.UI;

import UI.Model.ShopDto;
import UI.Model.UserDto;
import UI.View.Header;

public class HeaderPresenter {

    private final String _serverPort;
    private final Header view;

    public HeaderPresenter(Header view, String serverPort) {
        this.view = view;
        this._serverPort = serverPort;
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

    public void logoutUser(){
        
    }

    public void openNewShop(String shopName, String bankDetails, String shopAddress){
        RestTemplate restTemplate = new RestTemplate();
        ShopDto shopDto = new ShopDto(shopName, bankDetails, shopAddress);

        UI.getCurrent().getPage().executeJs("return localStorage.getItem('authToken');")
                .then(String.class, token -> {
                    if (token != null && !token.isEmpty()) {
                        System.out.println("Token: " + token);

                        HttpHeaders headers = new HttpHeaders();
                        headers.add("Authorization", token);

                        HttpEntity<ShopDto> requestEntity = new HttpEntity<>(shopDto, headers);

                        ResponseEntity<String> response = restTemplate.exchange(
                                "http://localhost:" + _serverPort + "/api/shop/openNewShop",
                                HttpMethod.POST,
                                requestEntity,
                                String.class);

                        if (response.getStatusCode().is2xxSuccessful()) {
                            view.showSuccessMessage("Shop opened successfully");
                            System.out.println(response.getBody());
                        } else {
                            view.showErrorMessage("Failed to open shop");
                        }
                    } else {
                        System.out.println("Token not found in local storage.");
                        view.showErrorMessage("Failed to open shop");
                    }
                });
    }
}

