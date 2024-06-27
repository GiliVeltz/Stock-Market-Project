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

import com.fasterxml.jackson.databind.JsonNode;
import com.nimbusds.jose.shaded.gson.Gson;
import com.vaadin.flow.component.UI;

import Dtos.ProductDto;
import UI.Model.Response;
import UI.Model.ShopDto;
import UI.View.Header;
import UI.View.SearchShopResultsView;

public class SearchShopPresenter {

    private final String _serverPort;
    private final Header headerView;
    private SearchShopResultsView searchShopsResultsView;

    public SearchShopPresenter(Header headerView, String serverPort) {
        this.headerView = headerView;
        this._serverPort = serverPort;
    }

    public void setSearchShopsResultsView(SearchShopResultsView searchShopsResultsView) {
        this.searchShopsResultsView = searchShopsResultsView;
    }


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
                                headerView.showSuccessMessage("The shop search succeeded");

                                // Convert data to JSON
                                String shopProductJson = new Gson().toJson(responseBody.getReturnValue());

                                // Navigate to the new view with data as parameter NOT Working
                                // UI.getCurrent().navigate(SearchShopResultsView.class, shopProductJson);
                            } else {
                                headerView.showErrorMessage("The shop search failed: " + responseBody.getErrorMessage());
                            }
                        } catch (HttpClientErrorException e) {
                            ResponseHandler.handleResponse(e.getStatusCode());
                        } catch (Exception e) {
                            headerView.showErrorMessage("Failed to parse response: " + e.getMessage());
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("Token not found in local storage.");
                        headerView.showErrorMessage("The shop search failed");
                    }
                });
    }
    

}
