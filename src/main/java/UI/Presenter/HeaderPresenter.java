package UI.Presenter;


import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.ParameterizedTypeReference;

import com.nimbusds.jose.shaded.gson.Gson;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinSession;

import com.vaadin.flow.server.VaadinSession;

import org.springframework.http.*;

import Dtos.ProductDto;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import UI.Model.UserDto;
import UI.View.Header;
import UI.View.SearchShopResultsView;
import UI.Model.Response;
import UI.Model.SearchShopDto;
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
                                view.showSuccessMessage("Login successful");
                                view.switchToLogout();
                                view.navigateToUserMainPage();
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
                            @SuppressWarnings("rawtypes")
                            ResponseEntity<Response> response = restTemplate.exchange(
                                "http://localhost:" + _serverPort + "/api/user/register",
                                HttpMethod.POST,
                                requestEntity,
                                Response.class);
    
                            @SuppressWarnings("rawtypes")
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
                            view.showErrorMessage("Failed to parse response");
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("Token not found in local storage.");
                        view.showErrorMessage("Logout failed");
                    }
                });
    }

    @SuppressWarnings("unused")
    public void SearchProducts(String category, Set<String> keyWord, String minPrice, String maxPrice, String productName){
        RestTemplate restTemplate = new RestTemplate();
    }


    @SuppressWarnings("unused")
    public void searchShop(String shopName, String shopId) {
        RestTemplate restTemplate = new RestTemplate();
        UI.getCurrent().getPage().executeJs("return localStorage.getItem('authToken');")
                .then(String.class, token -> {
                    if (token != null && !token.isEmpty()) {
                        HttpHeaders headers = new HttpHeaders();
                        headers.add("Authorization", token);

                        // Create URL with parameters
                        String url = "http://localhost:" + _serverPort + "/api/shop/searchAndDisplayShopByID?shopId=" + shopId;

                        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

                        try {
                            ResponseEntity<Response<Map<ShopDto, List<ProductDto>>>> response = restTemplate.exchange(
                                url,
                                HttpMethod.GET,
                                requestEntity,
                                new ParameterizedTypeReference<Response<Map<ShopDto, List<ProductDto>>>>() {}
                            );

                            Response<Map<ShopDto, List<ProductDto>>> responseBody = response.getBody();
                            if (response.getStatusCode().is2xxSuccessful() && responseBody.getErrorMessage() == null) {
                                view.showSuccessMessage("The shop search succeeded");

                                // Convert data to JSON
                                String shopProductJson = new Gson().toJson(responseBody.getReturnValue());

                                // Navigate to the new view with data as parameter NOT Working
                                // UI.getCurrent().navigate(SearchShopResultsView.class, shopProductJson);
                            } else {
                                view.showErrorMessage("The shop search failed: " + responseBody.getErrorMessage());
                            }
                        } catch (HttpClientErrorException e) {
                            ResponseHandler.handleResponse(e.getStatusCode());
                        } catch (Exception e) {
                            view.showErrorMessage("Failed to parse response: " + e.getMessage());
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("Token not found in local storage.");
                        view.showErrorMessage("The shop search failed");
                    }
                });
    }


}

