package UI.Presenter;

import java.util.List;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.shaded.gson.Gson;
import com.vaadin.flow.component.UI;

import UI.Model.Response;
import UI.Model.ShopDto;
import UI.Model.ProductDto;
import UI.View.ProductView;

public class ProductPresenter {

    private final ProductView view;

    public ProductPresenter(ProductView view) {
        this.view = view;
    }

    public void productInfo(String shopId, String productId) {
        RestTemplate restTemplate = new RestTemplate();
        UI.getCurrent().getPage().executeJs("return localStorage.getItem('authToken');")
                .then(String.class, token -> {
                    if (token != null && !token.isEmpty()) {
                        HttpHeaders headers = new HttpHeaders();
                        headers.add("Authorization", token);

                        // Corrected URL with proper parameter concatenation
                        String url = "http://localhost:" + view.getServerPort() + "/api/shop/displayProductGeneralInfo?shopId=" + shopId + "&productId=" + productId;

                        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

                        try {
                            ResponseEntity<String> response = restTemplate.exchange(
                                url,
                                HttpMethod.GET,
                                requestEntity,
                                String.class
                            );

                            if (response.getStatusCode().is2xxSuccessful()) {
                                ObjectMapper objectMapper = new ObjectMapper();
                                Response responseBody = objectMapper.readValue(response.getBody(), Response.class);

                                if (responseBody.getErrorMessage() == null) {
                                    view.showSuccessMessage("Product information fetched successfully");

                                    // Convert data to JSON
                                    ProductDto productInfo = objectMapper.readValue(objectMapper.writeValueAsString(responseBody.getReturnValue()), ProductDto.class);

                                    // Display product information on the view
                                    view.displayProductInfo(productInfo);
                                } else {
                                    view.showErrorMessage("Product information fetch failed: " + responseBody.getErrorMessage());
                                }
                            } else {
                                view.showErrorMessage("Product information fetch failed: " + response.getStatusCode());
                            }
                        } catch (HttpClientErrorException e) {
                            view.showErrorMessage("HTTP error: " + e.getStatusCode());
                        } catch (Exception e) {
                            view.showErrorMessage("Failed to parse response: " + e.getMessage());
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("Token not found in local storage.");
                        view.showErrorMessage("Product information fetch failed");
                    }
                });
    }
}
