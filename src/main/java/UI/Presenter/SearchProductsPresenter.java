package UI.Presenter;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.UI;
import java.util.List;
import java.util.Map;
import UI.View.Header;
import UI.Model.ProductDto;
import UI.Model.ProductSearchDto;
import UI.Model.SearchProductResponseDto;
import UI.View.SearchProductsResultsView;

public class SearchProductsPresenter {

    private final String _serverPort;
    private final Header headerView;
    private SearchProductsResultsView searchProductsResultsView;
    
    public SearchProductsPresenter(Header headerView, String serverPort) {
        this.headerView = headerView;
        this._serverPort = serverPort;
    }

    public void setSearchProductsResultsView(SearchProductsResultsView searchProductsResultsView) {
        this.searchProductsResultsView = searchProductsResultsView;
    }

    @SuppressWarnings("deprecation")
    public void searchProducts(String shopName, String productName, String category, List<String> keywords) {
        RestTemplate restTemplate = new RestTemplate();
        UI.getCurrent().getPage().executeJs("return localStorage.getItem('authToken');")
                .then(String.class, token -> {
                    if (token != null && !token.isEmpty()) {
                        Integer shopId = sendShopNameToIdRequest(restTemplate, token, shopName);
                        ProductSearchDto productSearchDto = new ProductSearchDto(shopId, productName, category, keywords);
                        HttpHeaders headers = new HttpHeaders();
                        headers.add("Authorization", token);
                        headers.setContentType(MediaType.APPLICATION_JSON); // Set content type

                        String url = createUrlBySearchType(productSearchDto);

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
        
                                if (shopNameToProducts != null) {
                                    searchProductsResultsView.displayResponseProducts(shopNameToProducts);
                                }
                                else {
                                    searchProductsResultsView.showErrorMessage("Searched products loading failed");
                                    searchProductsResultsView.getUI().ifPresent(ui -> ui.navigate("user"));
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

    // Send search products request
    private String createUrlBySearchType(ProductSearchDto productSearchDto) {
        if (productSearchDto.getProductName() != null && !productSearchDto.getProductName().isEmpty()) {
            return "http://localhost:" + _serverPort + "/api/shop/searchProductsInShopByName";
        }
        // Search by category
        else if (productSearchDto.getCategory() != null) {
           return "http://localhost:" + _serverPort + "/api/shop/searchProductsInShopByCategory";
        }
        // Search by keywords
        else {
            return "http://localhost:" + _serverPort + "/api/shop/searchProductsInShopByKeywords";
        }
    }


    // Send shop name to id request
    private Integer sendShopNameToIdRequest(RestTemplate restTemplate, String token, String shopName) {
        if (shopName == null || shopName.isEmpty()) {
            return null;
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token);
        headers.setContentType(MediaType.APPLICATION_JSON); // Set content type
        String url = "http://localhost:" + _serverPort + "/api/shop/getShopIdByName";
        HttpEntity<String> requestEntity = new HttpEntity<>(shopName, headers);
        ObjectMapper objectMapper = new ObjectMapper();
        Integer shopId = null;
        try {
            ResponseEntity<Response> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                Response.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                shopId = (Integer) response.getBody().getReturnValue();
            }
            else {
                headerView.showErrorMessage("Shop ID loading failed with status code: " + response.getStatusCodeValue());
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
        return shopId;
    }


}

