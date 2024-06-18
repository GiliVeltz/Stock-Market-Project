package UI.Presenter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.UI;

import UI.Model.Permission;
import UI.View.ShopManagerView;

public class ShopManagerPresenter {
    private final ShopManagerView view;

    public ShopManagerPresenter(ShopManagerView view) {
        this.view = view;
    }

    public void fetchManagerPermissions(String username){
        // Fetch the permissions of the manager
        RestTemplate restTemplate = new RestTemplate();
        UI.getCurrent().getPage().executeJs("return localStorage.getItem('authToken');")
                .then(String.class, token -> {
                    if (token != null && !token.isEmpty()) {
                        HttpHeaders headers = new HttpHeaders();
                        headers.add("Authorization", token);

                        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

                        ResponseEntity<String> response = restTemplate.exchange(
                                "http://localhost:" + view.getServerPort() + "/api/shop/getShopManagerPermissions?shopId="+view.getShopId(),
                                HttpMethod.GET,
                                requestEntity,
                                String.class);

                        ObjectMapper objectMapper = new ObjectMapper();

                        try{
                            JsonNode responseJson = objectMapper.readTree(response.getBody());
                            if (response.getStatusCode().is2xxSuccessful()) {
                                view.showSuccessMessage("User permissions loaded successfully");
                                if (responseJson.get("errorMessage").isNull()) {
                                    List<String> permissions = objectMapper.convertValue(responseJson.get("returnValue"), objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));
                                    view.createPermissionButtons(permissions);
                                }else {
                                    view.showErrorMessage("User permissions loading failed");
                                    view.getUI().ifPresent(ui -> ui.navigate("user"));
                                }
                            }
                            else {
                                view.showErrorMessage("User permissions loading failed with status code: " + response.getStatusCodeValue());
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

    public void viewProducts() {
        
    }

    public void addDiscounts() {

    }

    public void changeProductPolicy() {
        
    }

    public void appointManager(String newManagerUsername, Set<Permission> selectedPermissions) {
    RestTemplate restTemplate = new RestTemplate();
    UI.getCurrent().getPage().executeJs("return localStorage.getItem('authToken');")
            .then(String.class, token -> {
                if (token != null && !token.isEmpty()) {
                    HttpHeaders headers = new HttpHeaders();
                    headers.add("Authorization", token);
                    headers.setContentType(MediaType.APPLICATION_JSON); // Set content type

                    // Convert permissions to a set of strings
                    Set<String> permissionsList = selectedPermissions.stream()
                            .map(Enum::name)
                            .collect(Collectors.toSet());

                    // Create request body with permissions
                    Map<String, Object> requestBody = new HashMap<>();
                    requestBody.put("shopId", view.getShopId());
                    requestBody.put("newManagerUsername", newManagerUsername);
                    requestBody.put("permissions", permissionsList);

                    HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

                    try {
                        ResponseEntity<String> response = restTemplate.exchange(
                            "http://localhost:" + view.getServerPort() + "/api/shop/addShopManager",
                            HttpMethod.POST,
                            requestEntity,
                            String.class
                        );

                        if (response.getStatusCode().is2xxSuccessful()) {
                            ObjectMapper objectMapper = new ObjectMapper();
                            JsonNode responseJson = objectMapper.readTree(response.getBody());

                            if (responseJson.get("errorMessage").isNull()) {
                                view.showSuccessMessage("Manager appointed successfully");
                            } else {
                                view.showErrorMessage("Failed to appoint manager: " + responseJson.get("errorMessage").asText());
                            }
                        } else {
                            view.showErrorMessage("Failed to appoint manager with status code: " + response.getStatusCodeValue());
                        }
                    } catch (HttpClientErrorException e) {
                        view.showErrorMessage("HTTP error: " + e.getStatusCode());
                    } catch (Exception e) {
                        view.showErrorMessage("Failed to appoint manager: " + e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    view.showErrorMessage("Authorization token not found. Please log in.");
                }
            });
}


    public void viewSubordinate() {

    }

    public void viewShopRoles() {

    }

    public void viewPurchases() {

    }

    public void appointOwner(){

    }
}
