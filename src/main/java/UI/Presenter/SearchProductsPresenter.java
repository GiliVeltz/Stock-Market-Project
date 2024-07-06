package UI.Presenter;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.UI;

import UI.Model.ProductDto;
import UI.Model.ProductSearchDto;
import UI.Model.SearchProductResponseDto;
import UI.View.Header;
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
                            Integer shopId = null;
                            HttpHeaders headers = new HttpHeaders();
                            headers.add("Authorization", token);
                            headers.setContentType(MediaType.APPLICATION_JSON); // Set content type
                            ObjectMapper objectMapper = new ObjectMapper();
                            try {
                                if (shopName != null) {
                                    String url = "http://localhost:" + _serverPort + "/api/shop/getShopIdByName";
                                    HttpEntity<String> requestEntity1 = new HttpEntity<>(shopName, headers);
                                    
                                    // 1st request : getID
                                    ResponseEntity<String> response = restTemplate.exchange(
                                        url,
                                        HttpMethod.POST,
                                        requestEntity1,
                                        String.class);

                                    if (response.getStatusCode().is2xxSuccessful()) {
                                        // Parse JSON response to extract shop ID
                                        JsonNode rootNode = objectMapper.readTree(response.getBody());
                                        shopId = (rootNode.path("returnValue").asInt());
                                    } else {
                                        searchProductsResultsView.displayResponseShopNotFound(shopName);
                                        headerView.showErrorMessage("Shop ID loading failed with status code: " + response.getStatusCode().value());
                                    }
                                }
                                ProductSearchDto productSearchDto = new ProductSearchDto(shopId, productName, category, keywords);
                                String url = createUrlBySearchType(productSearchDto);
                                HttpEntity<ProductSearchDto> requestEntity2 = new HttpEntity<>(productSearchDto, headers);
                                ResponseEntity<String> response = restTemplate.exchange(
                                    url,
                                    HttpMethod.POST,
                                    requestEntity2,
                                    String.class);

                                String responseBody = response.getBody();
                                if (response.getStatusCode().is2xxSuccessful()) {
                                    // convert to ResponseDTO
                                    SearchProductResponseDto responseDto = objectMapper.readValue(responseBody, SearchProductResponseDto.class);
                                    Map<String, List<ProductDto>> shopNameToProducts = responseDto.getReturnValue();
            
                                    if (shopNameToProducts != null) {
                                        searchProductsResultsView.displayResponseProductsNew(shopNameToProducts);
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
                            if (e.getMessage().contains("not exist")) {
                                searchProductsResultsView.displayResponseShopNotFound(shopName);
                            }
                        } catch (Exception e) {
                            int startIndex = e.getMessage().indexOf("\"errorMessage\":\"") + 16;
                            int endIndex = e.getMessage().indexOf("\",", startIndex);
                            headerView.showErrorMessage(e.getMessage().substring(startIndex, endIndex));
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


    // // Send shop name to id request
    // @SuppressWarnings("deprecation")
    // public boolean sendShopNameToIdRequest(String shopName) {
    //     boolean[] isSuccess = {false};
    //     RestTemplate restTemplate = new RestTemplate();
    //     UI.getCurrent().getPage().executeJs("return localStorage.getItem('authToken');")
    //             .then(String.class, token -> {
    //                 if (token != null && !token.isEmpty()) {
    //                     HttpHeaders headers = new HttpHeaders();
    //                     headers.add("Authorization", token);
    //                     headers.setContentType(MediaType.APPLICATION_JSON); // Set content type

    //                     String url = "http://localhost:" + _serverPort + "/api/shop/getShopIdByName";
    //                     HttpEntity<String> requestEntity = new HttpEntity<>(shopName, headers);
    //                     ObjectMapper objectMapper = new ObjectMapper();

    //                     try {
    //                         ResponseEntity<String> response = restTemplate.exchange(
    //                             url,
    //                             HttpMethod.POST,
    //                             requestEntity,
    //                             String.class);

    //                         if (response.getStatusCode().is2xxSuccessful()) {
    //                             // Parse JSON response to extract shop ID
    //                             JsonNode rootNode = objectMapper.readTree(response.getBody());
    //                             setShopId(rootNode.path("returnValue").asInt());
    //                             isSuccess[0] = true;
    //                         } else {
    //                             searchProductsResultsView.displayResponseShopNotFound(shopName);
    //                             headerView.showErrorMessage("Shop ID loading failed with status code: " + response.getStatusCode().value());
    //                             isSuccess[0] = false;                            
    //                         }
    //                     }
    //                     catch (HttpClientErrorException e) {
    //                         if (e.getMessage().contains("not exist")) {
    //                             searchProductsResultsView.displayResponseShopNotFound(shopName);
    //                         }
    //                         else {
    //                             ResponseHandler.handleResponse(e.getStatusCode());
    //                         }
    //                         isSuccess[0] = false;
    //                     }
    //                     catch (Exception e) {
    //                         headerView.showErrorMessage("Failed to parse response");
    //                         e.printStackTrace();
    //                         headerView.getUI().ifPresent(ui -> ui.navigate("user"));
    //                         isSuccess[0] = false;
    //                     }
    //                 } else {
    //                     headerView.showErrorMessage("Authorization token not found. Please log in.");
    //                     isSuccess[0] = false;
    //                 }
    //             });
    //             return isSuccess[0];
    // }






        // RestTemplate restTemplate = new RestTemplate();
        // UI.getCurrent().getPage().executeJs("return localStorage.getItem('authToken');")
        //     .then(String.class, token -> {
        //         if (token != null && !token.isEmpty()) {
        //             HttpHeaders headers = new HttpHeaders();
        //             headers.add("Authorization", token);
        //             headers.setContentType(MediaType.APPLICATION_JSON); // Set content type
        //             String url = "http://localhost:" + _serverPort + "/api/shop/getShopIdByName";
        //             HttpEntity<String> requestEntity = new HttpEntity<>(shopName, headers);
        //             ObjectMapper objectMapper = new ObjectMapper();
        //             try {
        //                 ResponseEntity<String> response = restTemplate.exchange(
        //                     url,
        //                     HttpMethod.POST,
        //                     requestEntity,
        //                     String.class);
        //                 if (response.getStatusCode().is2xxSuccessful()) {
        //                     // Parse JSON response to extract shop ID
        //                     JsonNode rootNode = objectMapper.readTree(response.getBody());
        //                     Integer shopId = rootNode.path("returnValue").asInt();
        //                     return shopId;
        //                 } else {
        //                     headerView.showErrorMessage("Shop ID loading failed with status code: " + response.getStatusCode().value());
        //                     return -1;
        //                 }
        //             }
        //             catch (HttpClientErrorException e) {
        //                 ResponseHandler.handleResponse(e.getStatusCode());
        //                 return -1;
        //             }
        //             catch (Exception e) {
        //                 headerView.showErrorMessage("Failed to parse response");
        //                 e.printStackTrace();
        //                 headerView.getUI().ifPresent(ui -> ui.navigate("user"));
        //                 return -1;
        //             }
//     }
// });
}

