package UI.Presenter;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinSession;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;


import java.util.List;
import java.util.Map;

import UI.Model.ShopDto;
import UI.Model.UserDto;
import UI.View.Header;
import UI.Model.ProductDto;
import UI.Model.ProductSearchDto;
import UI.Model.SearchProductResponseDto;
import UI.Model.Response;
import UI.View.SearchResultsView;

public class SearchProductsPresenter {

    private final String _serverPort;
    private final Header headerView;
    private SearchResultsView searchResultsView;
    
    public SearchProductsPresenter(Header headerView, String serverPort) {
        this.headerView = headerView;
        this._serverPort = serverPort;
    }

    public void setSearchResultsView(SearchResultsView searchResultsView) {
        this.searchResultsView = searchResultsView;
    }

    @SuppressWarnings("deprecation")
    public void searchProducts(String shopName, String productName, String category, List<String> keywords) {
        RestTemplate restTemplate = new RestTemplate();
        UI.getCurrent().getPage().executeJs("return localStorage.getItem('authToken');")
                .then(String.class, token -> {
                    if (token != null && !token.isEmpty()) {
                        ProductSearchDto productSearchDto = new ProductSearchDto(shopName, productName, category, keywords);
                        HttpHeaders headers = new HttpHeaders();
                        headers.add("Authorization", token);
                        headers.setContentType(MediaType.APPLICATION_JSON); // Set content type

                        String url = "http://localhost:" + _serverPort + "/api/shop/searchProductsInShop";

                        HttpEntity<ProductSearchDto> requestEntity = new HttpEntity<>(productSearchDto, headers);
                        ObjectMapper objectMapper = new ObjectMapper();

                        try {
                            ResponseEntity<String> response = restTemplate.exchange(
                                url,
                                HttpMethod.POST,
                                requestEntity,
                                String.class);

                            String responseBody = response.getBody();
                            if (response.getStatusCode().is2xxSuccessful()) {
                                // convert to ResponseDTO
                                SearchProductResponseDto responseDto = objectMapper.readValue(responseBody, SearchProductResponseDto.class);
                                Map<String, List<ProductDto>> shopNameToProducts = responseDto.getReturnValue();
                                // Convert the JsonNode to the desired type
                                //Map<String, List<ProductDto>> shopNameToProducts = objectMapper.readValue(responseBody, new TypeReference<Map<String, List<ProductDto>>>() {});
                                if (shopNameToProducts != null && !shopNameToProducts.isEmpty()) {
                                    searchResultsView.displayResponseProducts(shopNameToProducts);
                                    // Process the map and update the UI
                                    // for (Map.Entry<String, List<ProductDto>> entry : shopNameToProducts.entrySet()) {
                                    //     String shopNameKey = entry.getKey();
                                    //     List<ProductDto> products = entry.getValue();
    
                                    //     // Create UI elements for each shopId and its associated products
                                    //     searchResultsView.createProductsInShopButtons(shopNameKey, products);
                                    // }
                                    // headerView.setSearchResultsVisible(true);
                                }
                                else {
                                    searchResultsView.showErrorMessage("Searched products loading failed");
                                    searchResultsView.getUI().ifPresent(ui -> ui.navigate("user"));
                                }
                            }   
                            else {
                                headerView.showErrorMessage("Search Results loading failed with status code: " + response.getStatusCodeValue());
                            }
                        }
                        catch (HttpClientErrorException e) {
                            ResponseHandler.handleResponse(e.getStatusCode());
                        }
                        catch (Exception e) {
                            headerView.showErrorMessage("Failed to parse response");
                            e.printStackTrace();
                            headerView.getUI().ifPresent(ui -> ui.navigate("user"));
                        }
                    } else {
                        headerView.showErrorMessage("Authorization token not found. Please log in.");
                    }
                });
    }


}
