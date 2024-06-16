package UI.Presenter;


import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.vaadin.flow.component.UI;
import java.util.Date;
import java.util.Set;

import UI.Model.ShopDto;
import UI.Model.UserDto;
import UI.View.Header;
import UI.Model.Response;

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
                                view.showSuccessMessage("Login successful");
                                view.switchToLogout();
                                System.out.println(response.getBody());
                            } else {
                                view.showErrorMessage("Login failed: " + responseBody.getErrorMessage());
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
    
    @SuppressWarnings("deprecation")
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
                            view.showErrorMessage(e.getMessage());
                            e.printStackTrace();
                        }
                    } else {
                        view.showErrorMessage("Authorization token not found. Please log in.");
                    }
                });
    }
    
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
                                view.showSuccessMessage("Logout successful");
                                view.switchToLogin();
                                System.out.println(response.getBody());
                            } else {
                                view.showErrorMessage("Logout failed: " + responseBody.getErrorMessage());
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

    public void openNewShop(String shopName, String bankDetails, String shopAddress) {
        RestTemplate restTemplate = new RestTemplate();
        ShopDto shopDto = new ShopDto(shopName, bankDetails, shopAddress);

        UI.getCurrent().getPage().executeJs("return localStorage.getItem('authToken');")
                .then(String.class, token -> {
                    if (token != null && !token.isEmpty()) {

                        HttpHeaders headers = new HttpHeaders();
                        headers.add("Authorization", token);

                        HttpEntity<ShopDto> requestEntity = new HttpEntity<>(shopDto, headers);

                        try {
                            ResponseEntity<Response> response = restTemplate.exchange(
                                "http://localhost:" + _serverPort + "/api/shop/openNewShop",
                                HttpMethod.POST,
                                requestEntity,
                                Response.class);
    
                            Response responseBody = response.getBody();
                            if (response.getStatusCode().is2xxSuccessful() && responseBody.getErrorMessage() == null) {
                                view.showSuccessMessage("Shop opened successfully");
                                System.out.println(response.getBody());
                            } else {
                                view.showErrorMessage("Failed to open shop: " + responseBody.getErrorMessage());
                            }
                        } catch (HttpClientErrorException e) {
                            ResponseHandler.handleResponse(e.getStatusCode());
                        } catch (Exception e) {
                            view.showErrorMessage("Failed to parse response: " + e.getMessage());
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("Token not found in local storage.");
                        view.showErrorMessage("Failed to open shop");
                    }
                });
    }
    
    public void SearchProducts(String category, Set<String> keyWord, String minPrice, String maxPrice, String productName){
        RestTemplate restTemplate = new RestTemplate();
    }
}
