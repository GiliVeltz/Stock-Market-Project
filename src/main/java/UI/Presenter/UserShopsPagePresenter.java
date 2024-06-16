package UI.Presenter;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import UI.View.UserShopsPageView;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.UI;


import java.util.List;

public class UserShopsPagePresenter {

    private final UserShopsPageView view;

    public UserShopsPagePresenter(UserShopsPageView view) {
        this.view = view;
    }

    public void fetchShops(String username) {
        RestTemplate restTemplate = new RestTemplate();
        UI.getCurrent().getPage().executeJs("return localStorage.getItem('authToken');")
                .then(String.class, token -> {
                    if (token != null && !token.isEmpty()) {
                        HttpHeaders headers = new HttpHeaders();
                        headers.add("Authorization", token);

                        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

                        ResponseEntity<String> response = restTemplate.exchange(
                                "http://localhost:" + view.getServerPort() + "/api/shop/getUserShops",
                                HttpMethod.GET,
                                requestEntity,
                                String.class);

                        ObjectMapper objectMapper = new ObjectMapper();

                        try{
                            JsonNode responseJson = objectMapper.readTree(response.getBody());
                            if (response.getStatusCode().is2xxSuccessful()) {
                                view.showSuccessMessage("User shops loaded successfully");
                                if (responseJson.get("errorMessage").isNull()) {
                                    // Create buttons for each shop
                                    List<Integer> shops = objectMapper.convertValue(responseJson.get("returnValue"), objectMapper.getTypeFactory().constructCollectionType(List.class, Integer.class));
                                    view.createShopButtons(shops);
                                }else {
                                    view.showErrorMessage("User shops loading failed");
                                    view.getUI().ifPresent(ui -> ui.navigate("user"));
                                }
                            }
                            else {
                                view.showErrorMessage("User shops loading failed with status code: " + response.getStatusCodeValue());
                            }
                        }catch (HttpClientErrorException e) {
                            ResponseHandler.handleResponse(e.getStatusCode());
                        }catch (Exception e) {
                            view.showErrorMessage("Failed to parse response");
                            e.printStackTrace();
                            view.getUI().ifPresent(ui -> ui.navigate("user"));
                        }
                    } else {
                        view.showErrorMessage("Authorization token not found. Please log in.");
                    }
                });
    }
    
}