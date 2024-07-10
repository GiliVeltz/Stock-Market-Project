package UI.Presenter;


import java.util.Date;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinSession;

import UI.Model.Response;
import UI.Model.UserDto;
import UI.View.Header;
// import ServiceLayer.Response;
public class HeaderPresenter {

    private final String _serverPort;
    private final Header view;
    
    public HeaderPresenter(Header view, String serverPort) {
        this.view = view;
        this._serverPort = serverPort;
    }
    
    @SuppressWarnings("rawtypes")
    public void loginUser(String username, String password) {
        RestTemplate restTemplate = new RestTemplate();
    
        UI.getCurrent().getPage().executeJs("return localStorage.getItem('authToken');")
                .then(String.class, token -> {
                    if (token != null && !token.isEmpty()) {
    
                        HttpHeaders headers = new HttpHeaders();
                        headers.add("Authorization", token);
    
                        String url = "http://localhost:" + _serverPort + "/api/user/login?username=" + username + "&password=" + password;
                        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
    
                        try {
                            ResponseEntity<Response> response = restTemplate.exchange(
                                    url,
                                    HttpMethod.POST,
                                    requestEntity,
                                    Response.class);
    
                            Response responseBody = response.getBody();
                            if (response.getStatusCode().is2xxSuccessful() && responseBody.getErrorMessage() == null) {
                                // Get the new token
                                String newToken = responseBody.getReturnValue().toString();
    
                                // Update the token in local storage using JavaScript
                                UI.getCurrent().getPage().executeJs("localStorage.setItem('authToken', $0);", newToken);
                                VaadinSession.getCurrent().setAttribute("username", username);

                                checkIfAdmin(newToken, username);

                                // view.showSuccessMessage("Login successful");
                                // view.switchToLogout();
                                // view.navigateToUserMainPage();
                                // System.out.println(response.getBody());
                            } else {
                                view.showErrorMessage("Login failed: " + responseBody.getErrorMessage());
                            }
                        } catch (HttpClientErrorException e) {
                            ResponseHandler.handleResponse(e.getStatusCode());
                        } catch (Exception e) {
                            view.showErrorMessage("Failed to parse response for Login");
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("Token not found in local storage.");
                        view.showErrorMessage("Login failed");
                    }
                });
    }

    @SuppressWarnings("rawtypes")
    private void checkIfAdmin(String token, String username) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token);

        String url = "http://localhost:" + _serverPort + "/api/user/isSystemAdmin?username=" + username;
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Response> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    requestEntity,
                    Response.class);
            Response responseBody = response.getBody();

            if ("User is an admin".equals(responseBody.getReturnValue().toString())) {
                VaadinSession.getCurrent().setAttribute("role", "admin");
            } else {
                VaadinSession.getCurrent().setAttribute("role", "user");
            }

            // Provide feedback to the user and navigate to the user main page
            view.showSuccessMessage("Login successful");
            view.switchToLogout();
            view.navigateToUserMainPage();
            System.out.println(responseBody);
        } catch (HttpClientErrorException e) {
            ResponseHandler.handleResponse(e.getStatusCode());
        } catch (Exception e) {
            view.showErrorMessage("Failed to check admin status");
            e.printStackTrace();
        }
    }
    
    @SuppressWarnings("rawtypes")
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
                            ResponseEntity<Response> response = restTemplate.exchange(
                                "http://localhost:" + _serverPort + "/api/user/register",
                                HttpMethod.POST,
                                requestEntity,
                                Response.class);
    
                            Response responseBody = response.getBody();
                            if (response.getStatusCode().is2xxSuccessful() && responseBody.getErrorMessage() == null) {
                                view.showSuccessMessage("Registration successful, Please sign in");
                            } else {
                                view.showErrorMessage("Registration failed: " + responseBody.getErrorMessage());
                            }
                        } catch (HttpClientErrorException e) {
                            ResponseHandler.handleResponse(e.getStatusCode());
                        } catch (Exception e) {
                            int startIndex = e.getMessage().indexOf("\"errorMessage\":\"") + 16;
                            int endIndex = e.getMessage().indexOf("\",", startIndex);
                            view.showErrorMessage(e.getMessage().substring(startIndex, endIndex));
                            e.printStackTrace();
                        }
                    } else {
                        view.showErrorMessage("Authorization token not found. Please log in.");
                    }
                });
    }
    
    @SuppressWarnings("rawtypes")
    public void logoutUser() {
        RestTemplate restTemplate = new RestTemplate();
        UI.getCurrent().getPage().executeJs("return localStorage.getItem('authToken');")
                .then(String.class, token -> {
                    if (token != null && !token.isEmpty()) {
    
                        HttpHeaders headers = new HttpHeaders();
                        headers.add("Authorization", token);
    
                        String url = "http://localhost:" + _serverPort + "/api/user/logout";
                        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
    
                        try {
                            ResponseEntity<Response> response = restTemplate.exchange(
                                    url,
                                    HttpMethod.POST,
                                    requestEntity,
                                    Response.class);
    
                            Response responseBody = response.getBody();
                            if (response.getStatusCode().is2xxSuccessful() && responseBody.getErrorMessage() == null) {
                                // Get the new token
                                String newToken = responseBody.getReturnValue().toString();
    
                                // Update the token in local storage using JavaScript
                                UI.getCurrent().getPage().executeJs("localStorage.setItem('authToken', $0);", newToken);
                                UI.getCurrent().getSession().setAttribute("username", null);
                                view.showSuccessMessage("Logout successful");
                                view.switchToLogin();
                                view.navigateToLandingPage();
                                System.out.println(response.getBody());
                            } else {
                                view.showErrorMessage("Logout failed: " + responseBody.getErrorMessage());
                            }
                        } catch (HttpClientErrorException e) {
                            ResponseHandler.handleResponse(e.getStatusCode());
                        } catch (Exception e) {
                            view.showErrorMessage("Failed to parse response for Logout");
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("Token not found in local storage.");
                        view.showErrorMessage("Logout failed");
                    }
                });
    }



    public void searchShop(String shopName, String shopId) {

                
    }


}

