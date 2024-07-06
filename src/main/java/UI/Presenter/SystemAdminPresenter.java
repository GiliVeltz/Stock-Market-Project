package UI.Presenter;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.vaadin.flow.component.UI;

import UI.Model.ShopDto;
import UI.View.SystemAdminPageView;

public class SystemAdminPresenter {
    private final SystemAdminPageView view;

    public SystemAdminPresenter(SystemAdminPageView view) {
        this.view = view;
    }

    public void closeShop(String shopId) {
        RestTemplate restTemplate = new RestTemplate();
        UI.getCurrent().getPage().executeJs("return localStorage.getItem('authToken');")
                .then(String.class, token -> {
                    if (token != null && !token.isEmpty()) {
                        System.out.println("Token: " + token);

                        HttpHeaders headers = new HttpHeaders();
                        headers.add("Authorization", token);

                        HttpEntity<ShopDto> requestEntity = new HttpEntity<>(headers);

                        ResponseEntity<String> response = restTemplate.exchange(
                                "http://localhost:" + view.getServerPort() + "/api/shop/closeShop?shopId=" + shopId ,
                                HttpMethod.POST,
                                requestEntity,
                                String.class);

                        if (response.getStatusCode().is2xxSuccessful()) {
                            view.showSuccessMessage("The shop has been closed successfully.");
                            System.out.println(response.getBody());
                        } else {
                            view.showErrorMessage("Failed to close the shop");
                        }
                    } else {
                        System.out.println("Token not found in local storage.");
                        view.showErrorMessage("Failed to close the shop");
                    }
                });
    }

    public void getShopPurchaseHistory(String shopId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getShopPurchaseHistory'");
    }

    public void getUserPurchaseHistory(String username) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getUserPurchaseHistory'");
    }


}
