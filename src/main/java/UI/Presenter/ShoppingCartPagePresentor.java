package UI.Presenter;

import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import UI.Model.Response; // Add this import statement
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.core.type.TypeReference;

import com.vaadin.flow.component.UI;

import UI.Model.PurchaseCartDetailsDto;
import UI.Model.BasketDto;
import UI.View.ShoppingCartPageView;

public class ShoppingCartPagePresentor {
    ShoppingCartPageView view;

    public ShoppingCartPagePresentor(ShoppingCartPageView view) {
        this.view = view;
    }

    public void viewCart(){
        RestTemplate restTemplate = new RestTemplate();

        UI.getCurrent().getPage().executeJs("return localStorage.getItem('authToken');")
                .then(String.class, token -> {
                    if (token != null && !token.isEmpty()) {
                        System.out.println("Token: " + token);

                        HttpHeaders headers = new HttpHeaders();
                        headers.add("Authorization", token);

                        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

                        String username = (String) UI.getCurrent().getSession().getAttribute("username");
                        
                        ResponseEntity<Response> response = restTemplate.exchange(
                                "http://localhost:" + view.getServerPort() + "/api/user/viewShoppingCart?username=" + username,
                                HttpMethod.GET,
                                requestEntity,
                                Response.class);

                        if (response.getStatusCode().is2xxSuccessful()) {
                            Response responseBody = response.getBody();

                            if (responseBody.getErrorMessage() == null) {
                                ObjectMapper objectMapper = new ObjectMapper();
                                List<BasketDto> basketDtoList = objectMapper.convertValue(
                                        responseBody.getReturnValue(),
                                        TypeFactory.defaultInstance().constructCollectionType(List.class, BasketDto.class));
                                view.showBaskets(basketDtoList);
                                view.showSuccessMessage("Cart opened successfully");
                            }
                            else {
                                view.showErrorMessage("Failed to parse JSON response");
                            }                       
                        } else {
                            view.showErrorMessage("Failed to open cart");
                        }
                    } else {
                        System.out.println("Token not found in local storage.");
                        view.showErrorMessage("Failed to open cart");
                    }
                });
    }

    public void purchaseCart(List<Integer> selectedIndexes, String cardNumber, String address) {
        RestTemplate restTemplate = new RestTemplate();
        PurchaseCartDetailsDto details = new PurchaseCartDetailsDto(selectedIndexes, cardNumber, address);

        UI.getCurrent().getPage().executeJs("return localStorage.getItem('authToken');")
                .then(String.class, token -> {
                    if (token != null && !token.isEmpty()) {
                        System.out.println("Token: " + token);

                        HttpHeaders headers = new HttpHeaders();
                        headers.add("Authorization", token);

                        HttpEntity<PurchaseCartDetailsDto> requestEntity = new HttpEntity<>(details, headers);

                        ResponseEntity<Response> response = restTemplate.exchange(
                                "http://localhost:" + view.getServerPort() + "/api/user/purchaseCart?details=" + details,
                                HttpMethod.POST,
                                requestEntity,
                                Response.class);

                        if (response.getStatusCode().is2xxSuccessful()) {
                            Response responseBody = response.getBody();

                            if (responseBody.getErrorMessage() == null) {
                                view.showSuccessMessage("Cart purchased successfully");
                            }
                            else {
                                view.showErrorMessage("Failed to parse JSON response");
                            }                       
                        } else {
                            view.showErrorMessage("Failed to purchase cart");
                        }
                    } else {
                        System.out.println("Token not found in local storage.");
                        view.showErrorMessage("Failed to purchase cart");
                    }
                });
    }
}
