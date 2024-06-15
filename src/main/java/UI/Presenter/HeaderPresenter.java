package UI.Presenter;


import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.UI;
import java.util.Date;

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
    
                        try {
                                ResponseEntity<String> response = restTemplate.exchange(
                                    url,
                                    HttpMethod.GET,
                                    requestEntity,
                                    String.class);
                
                            ObjectMapper objectMapper = new ObjectMapper();
                            String responseBody = response.getBody();
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
                        } catch (HttpClientErrorException e) {
                            ResponseHandler.handleResponse(e.getStatusCode());
                        } catch (Exception e) {
                            view.showErrorMessage("Failed to parse response");
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("Token not found in local storage.");
                        view.showErrorMessage("Login failed");
                    }
                });
    }
    
    public void registerUser(String username, String email, String password, Date birDate) {
        RestTemplate restTemplate = new RestTemplate();
        UserDto userDto = new UserDto(username, email, password, birDate);
    
        HttpHeaders headers = new HttpHeaders();
        UI.getCurrent().getPage().executeJs("return localStorage.getItem('authToken');")
                .then(String.class, token -> {
                    if (token != null && !token.isEmpty()) {
                        headers.set("Authorization", token);
                        
                        HttpEntity<UserDto> requestEntity = new HttpEntity<>(userDto, headers);
    
                        try {
                            ResponseEntity<String> response = restTemplate.exchange(
                                "http://localhost:" + _serverPort + "/api/user/register",
                                HttpMethod.POST,
                                requestEntity,
                                String.class);
                            
                            ObjectMapper objectMapper = new ObjectMapper();
                            
                            if (response.getStatusCode().is2xxSuccessful()) {
                                JsonNode responseJson = objectMapper.readTree(response.getBody());
                                if (responseJson.get("errorMessage").isNull()) {
                                    view.showSuccessMessage("Registration successful, Please sign in");
                                } else {
                                    view.showErrorMessage("Registration failed: " + responseJson.get("errorMessage").asText());
                                }
                            } else {
                                view.showErrorMessage("Registration failed with status code: " + response.getStatusCodeValue());
                            }
                        } catch (HttpClientErrorException e) {
                            ResponseHandler.handleResponse(e.getStatusCode());
                        } catch (Exception e) {
                            view.showErrorMessage("Failed to parse response: " + e.getMessage());
                            e.printStackTrace();
                        }
                    } else {
                        view.showErrorMessage("Authorization token not found. Please log in.");
                    }
                });
    }
    
    public void logoutUser(){
        RestTemplate restTemplate = new RestTemplate();
        UI.getCurrent().getPage().executeJs("return localStorage.getItem('authToken');")
                .then(String.class, token -> {
                    if (token != null && !token.isEmpty()) {
                        System.out.println("Token: " + token);
    
                        HttpHeaders headers = new HttpHeaders();
                        headers.add("Authorization", token);
    
                        String url = "http://localhost:" + _serverPort + "/api/user/logout";
                        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
    
                        try {
                                ResponseEntity<String> response = restTemplate.exchange(
                                    url,
                                    HttpMethod.GET,
                                    requestEntity,
                                    String.class);
                
                            ObjectMapper objectMapper = new ObjectMapper();
                            String responseBody = response.getBody();
                            JsonNode responseJson = objectMapper.readTree(responseBody);
                            if (response.getStatusCode().is2xxSuccessful() && responseJson.get("errorMessage").isNull()) {
                                // Get the new token
                                token = responseJson.get("returnValue").asText();
    
                                // Update the token in local storage using JavaScript
                                UI.getCurrent().getPage().executeJs("localStorage.setItem('authToken', $0);", token);
                                view.showSuccessMessage("Logout successful");
                                view.switchToLogout();
                                System.out.println(response.getBody());
                            } else {
                                view.showErrorMessage("Logout failed");
                            }
                        } catch (HttpClientErrorException e) {
                            ResponseHandler.handleResponse(e.getStatusCode());
                        } catch (Exception e) {
                            view.showErrorMessage("Failed to parse response");
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("Token not found in local storage.");
                        view.showErrorMessage("Logout failed");
                    }
                });

    }
    }