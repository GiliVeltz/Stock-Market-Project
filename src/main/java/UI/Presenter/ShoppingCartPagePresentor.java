package UI.Presenter;

import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import UI.Model.Response; // Add this import statement
import UI.Model.SupplyInfoDto;

import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.vaadin.flow.component.UI;

import UI.Model.PurchaseCartDetailsDto;
import UI.Model.BasketDto;
import UI.Model.PaymentInfoDto;
import UI.View.ShoppingCartPageView;

public class ShoppingCartPagePresentor {
    ShoppingCartPageView view;

    public ShoppingCartPagePresentor(ShoppingCartPageView view) {
        this.view = view;
    }

    @SuppressWarnings("rawtypes")
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
                        try {
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
                        } catch (HttpClientErrorException e) {
                            view.showErrorMessage("HTTP error: " + e.getStatusCode());
                        } catch (Exception e) {
                            int startIndex = e.getMessage().indexOf("\"errorMessage\":\"") + 16;
                            int endIndex = e.getMessage().indexOf("\",", startIndex);
                            view.showErrorMessage("Failed to view cart: " + e.getMessage().substring(startIndex, endIndex));
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("Token not found in local storage.");
                        view.showErrorMessage("Failed to open cart");
                    }
                });
    }

    @SuppressWarnings("rawtypes")
    public void purchaseCart(PaymentInfoDto paymentInfoDto, SupplyInfoDto supplyInfoDto ,List<Integer> selectedIndexes) {
        RestTemplate restTemplate = new RestTemplate();
        PurchaseCartDetailsDto details = new PurchaseCartDetailsDto(paymentInfoDto, supplyInfoDto, selectedIndexes);

        UI.getCurrent().getPage().executeJs("return localStorage.getItem('authToken');")
                .then(String.class, token -> {
                    if (token != null && !token.isEmpty()) {
                        System.out.println("Token: " + token);

                        HttpHeaders headers = new HttpHeaders();
                        headers.add("Authorization", token);

                        HttpEntity<PurchaseCartDetailsDto> requestEntity = new HttpEntity<>(details, headers);

                        try{
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
                        } catch (HttpClientErrorException e) {
                            view.showErrorMessage("HTTP error: " + e.getStatusCode());
                        } catch (Exception e) {
                            int startIndex = e.getMessage().indexOf("\"errorMessage\":\"") + 16;
                            int endIndex = e.getMessage().indexOf("\",", startIndex);
                            view.showErrorMessage("Failed to purchase cart: " + e.getMessage().substring(startIndex, endIndex));
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("Token not found in local storage.");
                        view.showErrorMessage("Failed to purchase cart");
                    }
                });
    }

    @SuppressWarnings("rawtypes")
    public void removeItemFromCart(int shopID, int productID, int quantity) {
        RestTemplate restTemplate = new RestTemplate();

        UI.getCurrent().getPage().executeJs("return localStorage.getItem('authToken');")
                .then(String.class, token -> {
                    if (token != null && !token.isEmpty()) {
                        System.out.println("Token: " + token);

                        HttpHeaders headers = new HttpHeaders();
                        headers.add("Authorization", token);

                        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

                        ResponseEntity<Response> response = restTemplate.exchange(
                                "http://localhost:" + view.getServerPort() + "/api/user/removeProductFromShoppingCart?productID=" + productID + "&shopID=" + shopID + "&quantity=" + quantity,
                                HttpMethod.POST,
                                requestEntity,
                                Response.class);

                        if (response.getStatusCode().is2xxSuccessful()) {
                            Response responseBody = response.getBody();

                            if (responseBody.getErrorMessage() == null) {
                                view.showSuccessMessage("Product removed successfully");
                            }
                            else {
                                view.showErrorMessage("Failed to parse JSON response");
                            }                       
                        } else {
                            view.showErrorMessage("Failed to remove product from cart");
                        }
                    } else {
                        System.out.println("Token not found in local storage.");
                        view.showErrorMessage("Failed to remove product from cart");
                    }
                });
    }
}
