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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.nimbusds.jose.shaded.gson.Gson;
import com.vaadin.flow.component.UI;

import Dtos.ProductDto;
import UI.Model.Response;
import UI.Model.ShopDto;
import UI.View.SearchShopResultsView;

public class searchShopsPresenter {

    private final SearchShopResultsView view;

    public searchShopsPresenter(SearchShopResultsView view) {
        this.view = view;
    }

    public void searchShopByID() {
        RestTemplate restTemplate = new RestTemplate();
    
        // UI.getCurrent().getPage().executeJs("return localStorage.getItem('authToken');").then(String.class, token -> {
        //     if (token != null && !token.isEmpty()) {
        //         System.out.println("Token: " + token);
    
        //         HttpHeaders headers = new HttpHeaders();
        //         headers.add("Authorization", token);
    
        //         HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        //         Integer shopId = (Integer) UI.getCurrent().getSession().getAttribute("shopId");
        //         ResponseEntity<Response> response = restTemplate.exchange(
        //                 "http://localhost:" + view.getServerPort()
        //                         + "/api/shop/searchAndDisplayShopByID?shopId=" + shopId,
        //                 HttpMethod.GET, requestEntity, Response.class);
    
        //         if (response.getStatusCode().is2xxSuccessful()) {
        //             Response<Map<ShopDto, List<ProductDto>>> responseBody = response.getBody();
        //             if (responseBody != null) {
        //                 if (responseBody.getErrorMessage() == null) {
        //                     Map<ShopDto, List<ProductDto>> shopMap = responseBody.getReturnValue();
        //                     view.displaySearchResults(shopMap);
        //                     view.showSuccessMessage("Products presented successfully");
        //                 } else {
        //                     view.showErrorMessage("Backend error: " + responseBody.getErrorMessage());
        //                 }
        //             } else {
        //                 view.showErrorMessage("Empty response body received");
        //             }
        //         } else {
        //             view.showErrorMessage("Failed to fetch data: " + response.getStatusCode().toString());
        //         }
        //     } else {
        //         System.out.println("Token not found in local storage.");
        //         view.showErrorMessage("Failed to present products");
        //     }
        // });
    }

    public void searchShopByName(){
        RestTemplate restTemplate = new RestTemplate();

    //    UI.getCurrent().getPage().executeJs("return localStorage.getItem('authToken');")
    //            .then(String.class, token -> {
    //                if (token != null && !token.isEmpty()) {
    //                    System.out.println("Token: " + token);

    //                    HttpHeaders headers = new HttpHeaders();
    //                    headers.add("Authorization", token);

    //                    HttpEntity<String> requestEntity = new HttpEntity<>(headers);
    //                    String shopName = (String) UI.getCurrent().getSession().getAttribute("shopName");
    //                    ResponseEntity<Response> response = restTemplate.exchange(
    //                            "http://localhost:" + view.getServerPort() + "/api/shop/searchAndDisplayShopByName?shopName=" + shopName,
    //                            HttpMethod.GET,
    //                            requestEntity,
    //                            Response.class);

    //                    if (response.getStatusCode().is2xxSuccessful()) {
    //                        Response responseBody = response.getBody();

    //                        if (responseBody.getErrorMessage() == null) {
    //                            ObjectMapper objectMapper = new ObjectMapper();
    //                            Response<Map <ShopDto, List<ProductDto>>> shopMap = objectMapper.convertValue(
    //                                    responseBody.getReturnValue(),
    //                                    TypeFactory.defaultInstance().constructMapType(Map.class, ShopDto.class, List.class));
    //                            view.displaySearchResults(shopMap);
    //                            view.showSuccessMessage("products present successfully");
    //                        }
    //                        else {
    //                            view.showErrorMessage("Failed to parse JSON response");
    //                        }                       
    //                    } else {
    //                        view.showErrorMessage("Failed to present products");
    //                    }
    //                } else {
    //                    System.out.println("Token not found in local storage.");
    //                    view.showErrorMessage("Failed to present products");
    //                }
    //            });

   }

    

}
