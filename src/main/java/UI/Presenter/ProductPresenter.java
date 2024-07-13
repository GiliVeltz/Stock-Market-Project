package UI.Presenter;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.UI;

import UI.Model.ProductDto;
import UI.Model.ProductGetterDto;
import UI.Model.Response;
import UI.View.ProductView;

@SuppressWarnings("rawtypes")
public class ProductPresenter {

    private final ProductView _view;
    private final String _serverPort;

    public ProductPresenter(ProductView view, String serverPort) {
        this._view = view;
        this._serverPort = serverPort;
    }

    public void productInfo(String shopId, String productId) {
        RestTemplate restTemplate = new RestTemplate();
        UI.getCurrent().getPage().executeJs("return localStorage.getItem('authToken');")
                .then(String.class, token -> {
                    if (token != null && !token.isEmpty()) {
                        HttpHeaders headers = new HttpHeaders();
                        headers.add("Authorization", token);

                        // Corrected URL with proper parameter concatenation
                        String url = "http://localhost:" + _view.getServerPort() + "/api/shop/displayProductGeneralInfo?shopId=" + shopId + "&productId=" + productId;

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
                                    _view.showSuccessMessage("Product information fetched successfully");

                                    // Convert data to JSON
                                    ProductDto productInfo = objectMapper.readValue(objectMapper.writeValueAsString(responseBody.getReturnValue()), ProductDto.class);

                                    // Display product information on the view
                                    _view.displayProductStringInfo(productInfo);
                                } else {
                                    _view.showErrorMessage("Product information fetch failed: " + responseBody.getErrorMessage());
                                }
                            } else {
                                _view.showErrorMessage("Product information fetch failed: " + response.getStatusCode());
                            }
                        } catch (HttpClientErrorException e) {
                            _view.showErrorMessage("HTTP error: " + e.getStatusCode());
                        } catch (Exception e) {
                            int startIndex = e.getMessage().indexOf("\"errorMessage\":\"") + 16;
                            int endIndex = e.getMessage().indexOf("\",", startIndex);
                            _view.showErrorMessage(e.getMessage().substring(startIndex, endIndex));
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("Token not found in local storage.");
                        _view.showErrorMessage("Product information fetch failed");
                    }
                });
    }

    public void getDetailedProduct(int shopId, int productId) {
        RestTemplate restTemplate = new RestTemplate();

        UI.getCurrent().getPage().executeJs("return localStorage.getItem('authToken');")
                .then(String.class, token -> {
                    if (token != null && !token.isEmpty()) {
                        try
                        {
                            System.out.println("Token: " + token);

                            HttpHeaders headers = new HttpHeaders();
                            headers.add("Authorization", token);

                            HttpEntity<String> requestEntity = new HttpEntity<>(headers);

                            ResponseEntity<Response> response = restTemplate.exchange(
                                    "http://localhost:" + _serverPort + "/api/shop/getDetailedProduct?shopId=" + shopId +
                                    "&productId=" + productId,
                                    HttpMethod.GET,
                                    requestEntity,
                                    Response.class);

                            if (response.getStatusCode().is2xxSuccessful()) {
                                Response responseBody = response.getBody();

                                if (responseBody.getErrorMessage() == null) {
                                    ObjectMapper objectMapper = new ObjectMapper();
                                    ProductGetterDto productGetterDto = objectMapper.convertValue(responseBody.getReturnValue(), ProductGetterDto.class);
                                    _view.displayAllProductDetails(productGetterDto);
                                    _view.showSuccessMessage("product details are displayed successfully");
                                } else {
                                    _view.showErrorMessage("Failed to parse JSON response");
                                }
                            } else {
                                _view.showErrorMessage("Failed to display product's details");
                            }
                        } catch (HttpClientErrorException e) {
                            _view.showErrorMessage("HTTP error: " + e.getStatusCode());
                        } catch (Exception e) {
                            int startIndex = e.getMessage().indexOf("\"errorMessage\":\"") + 16;
                            int endIndex = e.getMessage().indexOf("\",", startIndex);
                            _view.showErrorMessage("Failed to display product's details: " + e.getMessage().substring(startIndex, endIndex));
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("Token not found in local storage.");
                        _view.showErrorMessage("Failed to present products");
                    }
                });
    }

    public void addProductToCart(int shopId, int productId, int quantity) {
        RestTemplate restTemplate = new RestTemplate();

        UI.getCurrent().getPage().executeJs("return localStorage.getItem('authToken');")
                .then(String.class, token -> {
                    if (token != null && !token.isEmpty()) {
                        try
                        {
                            System.out.println("Token: " + token);

                            HttpHeaders headers = new HttpHeaders();
                            headers.add("Authorization", token);

                            HttpEntity<String> requestEntity = new HttpEntity<>(headers);

                            ResponseEntity<Response> response = restTemplate.exchange(
                                    "http://localhost:" + _serverPort + "/api/user/addProductToShoppingCart?productID=" + productId +
                                    "&shopID=" + shopId + "&quantity=" + quantity,
                                    HttpMethod.POST,
                                    requestEntity,
                                    Response.class);

                            if (response.getStatusCode().is2xxSuccessful()) {
                                Response responseBody = response.getBody();

                                if (responseBody.getErrorMessage() == null) {
                                    _view.showSuccessMessage("Product added to cart successfully");
                                }
                                else {
                                    _view.showErrorMessage("Failed to parse JSON response");
                                }                       
                            } else {
                                _view.showErrorMessage("Failed to add product to cart");
                            }
                        } catch (HttpClientErrorException e) {
                            _view.showErrorMessage("HTTP error: " + e.getStatusCode());
                        } catch (Exception e) {
                            int startIndex = e.getMessage().indexOf("\"errorMessage\":\"") + 16;
                            int endIndex = e.getMessage().indexOf("\",", startIndex);
                            _view.showErrorMessage("Failed to add product to cart: " + e.getMessage().substring(startIndex, endIndex));
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("Token not found in local storage.");
                        _view.showErrorMessage("Failed to add product to cart");
                    }
                });
    }
}
