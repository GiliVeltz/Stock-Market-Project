package UI.Presenter;

import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.vaadin.flow.component.UI;

import UI.Model.BasketDto;
import UI.View.ShoppingCartPageView;

public class ShoppingCartPagePresentor {
    ShoppingCartPageView view;

    public ShoppingCartPagePresentor(ShoppingCartPageView view) {
        this.view = view;
    }

    public void viewCart(){
        RestTemplate restTemplate = new RestTemplate();

        UI.getCurrent().getPage().executeJs("return localStorage.getItem('authToken');")
                .then(String.class, token -> {
                    if (token != null && !token.isEmpty()) {
                        System.out.println("Token: " + token);

                        HttpHeaders headers = new HttpHeaders();
                        headers.add("Authorization", token);

                        HttpEntity<List<BasketDto>> requestEntity = new HttpEntity<>(headers);

                        String username = (String) UI.getCurrent().getSession().getAttribute("username");
                        
                        ResponseEntity<String> response = restTemplate.exchange(
                                "http://localhost:" + view.getServerPort() + "/api/user/viewShoppingCart?username=" + username,
                                HttpMethod.GET,
                                requestEntity,
                                String.class);

                        if (response.getStatusCode().is2xxSuccessful()) {
                            view.showSuccessMessage("Cart opened successfully");
                            System.out.println(response.getBody());
                        } else {
                            view.showErrorMessage("Failed to open cart");
                        }
                    } else {
                        System.out.println("Token not found in local storage.");
                        view.showErrorMessage("Failed to open shop");
                    }
                });
    }
}
