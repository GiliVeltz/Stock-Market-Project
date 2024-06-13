package UI.Presenter;

import java.util.stream.Collectors;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import UI.Model.UserDto;
import UI.View.UserMainPageView;
import UI.View.UserShopsPageView;
import UI.View.ViewPageI;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import java.util.List;

public class UserShopsPagePresenter {

    private final UserShopsPageView view;

    public UserShopsPagePresenter(UserShopsPageView view) {
        this.view = view;
    }

    private void fetchShops(String username) {
        RestTemplate restTemplate = new RestTemplate();
        UI.getCurrent().getPage().executeJs("return localStorage.getItem('authToken');")
                .then(String.class, token -> {
                    if (token != null && !token.isEmpty()) {
                        HttpHeaders headers = new HttpHeaders();
                        headers.add("Authorization", token);

                        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

                        ResponseEntity<String> response = restTemplate.exchange(
                                "http://localhost:" + ViewPageI._serverPort + "/api/shop/getUserShops",
                                HttpMethod.GET,
                                requestEntity,
                                String.class);

                        ObjectMapper objectMapper = new ObjectMapper();
                        String responseBody = response.getBody();

                        try{
                            JsonNode responseJson = objectMapper.readTree(responseBody);
                            if (response.getStatusCode().is2xxSuccessful() && responseJson.get("errorMessage").isNull()) {
                                view.showSuccessMessage("User shops loaded successfuly");
                            } else {
                                // Create buttons for each shop
                                List<Integer> shops = objectMapper.convertValue(responseJson.get("returnValue"), objectMapper.getTypeFactory().constructCollectionType(List.class, Integer.class));
                                view.createShopButtons(shops);
                                view.showErrorMessage("User shops loading failed");
                            }
                        }catch (Exception e) {
                            view.showErrorMessage("Failed to parse response");
                            e.printStackTrace();
                            return;
                        }
                    } else {
                        System.out.println("Token not found in local storage.");
                        view.showErrorMessage("User shops loading failed");
                    }
                });
    }
    
}