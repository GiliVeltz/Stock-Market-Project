package UI.Presenter;

import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.vaadin.flow.component.UI;

import UI.Model.ProductDto;
import UI.Model.Response;
import UI.Model.ShopDto;
import UI.View.ShopView;

public class ShopViewPresenter {

    ShopView _view;

    public ShopViewPresenter(ShopView view){
        this._view = view;
    }

    public void getShopProducts(){
         RestTemplate restTemplate = new RestTemplate();

        UI.getCurrent().getPage().executeJs("return localStorage.getItem('authToken');")
                .then(String.class, token -> {
                    if (token != null && !token.isEmpty()) {
                        System.out.println("Token: " + token);

                        HttpHeaders headers = new HttpHeaders();
                        headers.add("Authorization", token);

                        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
                        Integer shopId = (Integer) UI.getCurrent().getSession().getAttribute("shopId");
                        ResponseEntity<Response> response = restTemplate.exchange(
                                "http://localhost:" + _view.getServerPort() + "/api/shop/getAllProductInShop?shopId=" + shopId,
                                HttpMethod.GET,
                                requestEntity,
                                Response.class);

                        if (response.getStatusCode().is2xxSuccessful()) {
                            Response responseBody = response.getBody();

                            if (responseBody.getErrorMessage() == null) {
                                ObjectMapper objectMapper = new ObjectMapper();
                                List<ProductDto> productDtoList = objectMapper.convertValue(
                                        responseBody.getReturnValue(),
                                        TypeFactory.defaultInstance().constructCollectionType(List.class, ProductDto.class));
                                _view.displayAllProducts(productDtoList);
                                _view.showSuccessMessage("products present successfully");
                            }
                            else {
                                _view.showErrorMessage("Failed to parse JSON response");
                            }                       
                        } else {
                            _view.showErrorMessage("Failed to present products");
                        }
                    } else {
                        System.out.println("Token not found in local storage.");
                        _view.showErrorMessage("Failed to present products");
                    }
                });

    }

}
