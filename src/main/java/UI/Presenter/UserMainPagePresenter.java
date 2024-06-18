package UI.Presenter;
import java.util.Date;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.vaadin.flow.component.UI;

import Dtos.UserDto;
import UI.Model.ShopDto;
import UI.View.UserMainPageView;

public class UserMainPagePresenter {

    private final UserMainPageView view;

    public UserMainPagePresenter(UserMainPageView view) {
        this.view = view;
    }

    public void openNewShop(String shopName, String bankDetails, String shopAddress){
        RestTemplate restTemplate = new RestTemplate();
        ShopDto shopDto = new ShopDto(shopName, bankDetails, shopAddress);

        UI.getCurrent().getPage().executeJs("return localStorage.getItem('authToken');")
                .then(String.class, token -> {
                    if (token != null && !token.isEmpty()) {
                        System.out.println("Token: " + token);

                        HttpHeaders headers = new HttpHeaders();
                        headers.add("Authorization", token);

                        HttpEntity<ShopDto> requestEntity = new HttpEntity<>(shopDto, headers);

                        ResponseEntity<String> response = restTemplate.exchange(
                                "http://localhost:" + view.getServerPort() + "/api/shop/openNewShop",
                                HttpMethod.POST,
                                requestEntity,
                                String.class);

                        if (response.getStatusCode().is2xxSuccessful()) {
                            view.showSuccessMessage("Shop opened successfully");
                            System.out.println(response.getBody());
                        } else {
                            view.showErrorMessage("Failed to open shop");
                        }
                    } else {
                        System.out.println("Token not found in local storage.");
                        view.showErrorMessage("Failed to open shop");
                    }
                });
    }

    public UserDto getUserInfo(String username) {
        // Here, you would typically fetch user information from your backend or database
        // For demonstration purposes, create a dummy UserDto
        UserDto user = new UserDto(username, "password", "user@example.com", new Date());
        return user;
    }

    public void updateUserInfo(UserDto username) {
        // Here, you would typically fetch user information from your backend or database
        // For demonstration purposes, create a dummy UserDto
    }

    
}
