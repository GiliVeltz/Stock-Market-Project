package UI.Presenter;


import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import UI.Model.Response;
import UI.Model.ShopDto;

import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.UI;
import com.fasterxml.jackson.databind.type.TypeFactory;

import UI.View.AllShopView;

public class AllShopPresenter {

    AllShopView view;

    public AllShopPresenter(AllShopView view) {
        this.view = view;
    }

    @SuppressWarnings("rawtypes")
    public void getAllShops(){
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
                                    "http://localhost:" + view.getServerPort() + "/api/shop/getAllShops",
                                    HttpMethod.GET,
                                    requestEntity,
                                    Response.class);
    
                            if (response.getStatusCode().is2xxSuccessful()) {
                                Response responseBody = response.getBody();
    
                                if (responseBody.getErrorMessage() == null) {
                                    ObjectMapper objectMapper = new ObjectMapper();
                                    List<ShopDto> shopDtoList = objectMapper.convertValue(
                                            responseBody.getReturnValue(),
                                            TypeFactory.defaultInstance().constructCollectionType(List.class, ShopDto.class));
                                    view.showShops(shopDtoList);
                                    view.showSuccessMessage("shops present successfully");
                                }
                                else {view.showErrorMessage("Failed to parse JSON response");}                       
                            } else {view.showErrorMessage("Failed to present shops");}
                        }catch (HttpClientErrorException e) {
                            view.showErrorMessage("HTTP error: " + e.getStatusCode());
                        } catch (Exception e) {
                            int startIndex = e.getMessage().indexOf("\"errorMessage\":\"") + 16;
                            int endIndex = e.getMessage().indexOf("\",", startIndex);
                            view.showErrorMessage("Failed to present shops: " + e.getMessage().substring(startIndex, endIndex));
                            e.printStackTrace();
                        }

                    } else {
                        System.out.println("Token not found in local storage.");
                        view.showErrorMessage("Failed to present shops");
                    }
                });
    }

}
