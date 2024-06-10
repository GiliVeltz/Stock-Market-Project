package UI.Presenter;


import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.UI;

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

        UI.getCurrent().getPage().executeJs("return localStorage.getItem('authToken');")
                .then(String.class, token -> {
                    if (token != null && !token.isEmpty()) {
                        System.out.println("Token: " + token);

                        HttpHeaders headers = new HttpHeaders();
                        headers.add("Authorization", token);


                        String url = "http://localhost:" + _serverPort + "/api/user/login?username=" + username + "&password=" + password;
                        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

                        ResponseEntity<String> response = restTemplate.exchange(
                                url,
                                HttpMethod.GET,
                                requestEntity,
                                String.class);

                        ObjectMapper objectMapper = new ObjectMapper();
                        String responseBody = response.getBody();
                        try{
                            JsonNode responseJson = objectMapper.readTree(responseBody);
                            if (response.getStatusCode().is2xxSuccessful() && responseJson.get("errorMessage").isNull()) {
                                // Get the new token
                                token = responseJson.get("returnValue").asText();

                                // Update the token in local storage using JavaScript
                                UI.getCurrent().getPage().executeJs("localStorage.setItem('authToken', $0);", token);
                                view.showSuccessMessage("Login successful");
                                view.switchToLogout();
                                System.out.println(response.getBody());
                            } else {
                                view.showErrorMessage("Login failed");
                            }
                        } catch (Exception e) {
                            view.showErrorMessage("Failed to parse response");
                            e.printStackTrace();
                            return;
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
                        
                        ObjectMapper objectMapper = new ObjectMapper();
                        String responseBody = response.getBody();
                        try{
                            JsonNode responseJson = objectMapper.readTree(responseBody);     
                            if (response.getStatusCode().is2xxSuccessful() && responseJson.get("errorMessage").isNull()) {
                                view.showSuccessMessage("Registration successful, Please sign in");
                                System.out.println(response.getBody());
                            } else {
                                view.showErrorMessage("Registration failed");
                            }
                        }catch (Exception e) {
                            view.showErrorMessage("Failed to parse response");
                            e.printStackTrace();
                            return;
                        }
                    } else {
                        System.out.println("Token not found in local storage.");
                        view.showErrorMessage("Registration failed");
                    }
                });
    }

    public void logoutUser(){
        
    }
}

