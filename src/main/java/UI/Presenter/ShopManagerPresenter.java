package UI.Presenter;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.vaadin.flow.component.UI;

import UI.Model.Permission;
import UI.Model.ProductDto;
import UI.Model.Response;
import UI.Model.ShopDiscountDto;
import UI.Model.ShopDto;
import UI.Model.ShopManagerDto;
import UI.Model.ProductPolicy.ProductPolicyRuleList;
import UI.Model.ShopOrderDto;
import UI.Model.ProductPolicy.UserRuleDto;
import UI.Model.ShopPolicy.MinBasketPriceRuleDto;
import UI.Model.ShopPolicy.MinProductAmountRuleDto;
import UI.Model.ShopPolicy.ShopPolicyRulesList;
import UI.Model.ShopPolicy.ShoppingBasketRuleDto;
import UI.View.ShopManagerView;
import UI.Model.Category;

@SuppressWarnings({"rawtypes" , "deprecation"})
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
                            int startIndex = e.getMessage().indexOf("\"errorMessage\":\"") + 16;
                            int endIndex = e.getMessage().indexOf("\",", startIndex);
                            view.showErrorMessage("Failed to appoint manager: " + e.getMessage().substring(startIndex, endIndex));
                            e.printStackTrace();
                        }
                    } else {
                        view.showErrorMessage("Authorization token not found. Please log in.");
                    }
                });
    }

    public void fetchShopManagers(Consumer<List<ShopManagerDto>> callback){
        RestTemplate restTemplate = new RestTemplate();
        UI.getCurrent().getPage().executeJs("return localStorage.getItem('authToken');")
                .then(String.class, token -> {
                    if (token != null && !token.isEmpty()) {
                        HttpHeaders headers = new HttpHeaders();
                        headers.add("Authorization", token);

                        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

                        ResponseEntity<Response> response = restTemplate.exchange(
                                "http://localhost:" + view.getServerPort() + "/api/shop/getShopManagers?shopId="+view.getShopId(),
                                HttpMethod.GET,
                                requestEntity,
                                Response.class);

                        try{
                            if (response.getStatusCode().is2xxSuccessful()) {
                                Response responseBody = response.getBody();
                                view.showSuccessMessage("Managers loaded successfully");
                                if (responseBody.getErrorMessage() == null) {
                                    ObjectMapper objectMapper = new ObjectMapper();
                                    List<ShopManagerDto> managers = objectMapper.convertValue(
                                        responseBody.getReturnValue(),
                                        TypeFactory.defaultInstance().constructCollectionType(List.class, ShopManagerDto.class));
                                    callback.accept(managers);
                                }else {
                                    view.showErrorMessage("Managers loading failed");
                                    //view.getUI().ifPresent(ui -> ui.navigate("user"));
                                }
                            }
                            else {
                                view.showErrorMessage("Managers loading failed with status code: " + response.getStatusCodeValue());
                            }
                        }catch (HttpClientErrorException e) {
                            ResponseHandler.handleResponse(e.getStatusCode());
                        }catch (Exception e) {
                            view.showErrorMessage("Failed to parse response");
                            e.printStackTrace();
                            //view.getUI().ifPresent(ui -> ui.navigate("user"));
                        }
                    } else {
                        view.showErrorMessage("Authorization token not found. Please log in.");
                    }
                });
    }


    public void fetchMySubordinates(Consumer<List<ShopManagerDto>> callback) {
        RestTemplate restTemplate = new RestTemplate();
        UI.getCurrent().getPage().executeJs("return localStorage.getItem('authToken');")
                .then(String.class, token -> {
                    if (token != null && !token.isEmpty()) {
                        HttpHeaders headers = new HttpHeaders();
                        headers.add("Authorization", token);

                        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

                        ResponseEntity<Response> response = restTemplate.exchange(
                                "http://localhost:" + view.getServerPort() + "/api/shop/getMySubordinates?shopId="+view.getShopId(),
                                HttpMethod.GET,
                                requestEntity,
                                Response.class);

                        try{
                            if (response.getStatusCode().is2xxSuccessful()) {
                                Response responseBody = response.getBody();
                                view.showSuccessMessage("Subordinates loaded successfully");
                                if (responseBody.getErrorMessage() == null) {
                                    ObjectMapper objectMapper = new ObjectMapper();
                                    List<ShopManagerDto> managers = objectMapper.convertValue(
                                        responseBody.getReturnValue(),
                                        TypeFactory.defaultInstance().constructCollectionType(List.class, ShopManagerDto.class));
                                    callback.accept(managers);
                                }else {
                                    view.showErrorMessage("Subordinates loading failed");
                                }
                            }
                            else {
                                view.showErrorMessage("Subordinates loading failed with status code: " + response.getStatusCodeValue());
                            }
                        }catch (HttpClientErrorException e) {
                            ResponseHandler.handleResponse(e.getStatusCode());
                        }catch (Exception e) {
                            view.showErrorMessage("Failed to parse response");
                            e.printStackTrace();
                        }
                    } else {
                        view.showErrorMessage("Authorization token not found. Please log in.");
                    }
                });
    }

    public void viewShopRoles() {

    }

    public void viewPurchases() {

    }

    public void appointOwner(String newOwnerUsername){
        RestTemplate restTemplate = new RestTemplate();
        UI.getCurrent().getPage().executeJs("return localStorage.getItem('authToken');")
                .then(String.class, token -> {
                    if (token != null && !token.isEmpty()) {
                        HttpHeaders headers = new HttpHeaders();
                        headers.add("Authorization", token);
                        headers.setContentType(MediaType.APPLICATION_JSON); // Set content type

                        // Create request body with permissions
                        Map<String, Object> requestBody = new HashMap<>();
                        requestBody.put("shopId", view.getShopId());
                        requestBody.put("newOwnerUsername", newOwnerUsername);

                        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

                        try {
                            ResponseEntity<String> response = restTemplate.exchange(
                                "http://localhost:" + view.getServerPort() + "/api/shop/addShopOwner",
                                HttpMethod.POST,
                                requestEntity,
                                String.class
                            );

                            if (response.getStatusCode().is2xxSuccessful()) {
                                ObjectMapper objectMapper = new ObjectMapper();
                                JsonNode responseJson = objectMapper.readTree(response.getBody());

                                if (responseJson.get("errorMessage").isNull()) {
                                    view.showSuccessMessage("Owner appointed successfully");
                                } else {
                                    view.showErrorMessage("Failed to appoint owner: " + responseJson.get("errorMessage").asText());
                                }
                            } else {
                                view.showErrorMessage("Failed to appoint owner with status code: " + response.getStatusCodeValue());
                            }
                        } catch (HttpClientErrorException e) {
                            view.showErrorMessage("HTTP error: " + e.getStatusCode());
                        } catch (Exception e) {
                            int startIndex = e.getMessage().indexOf("\"errorMessage\":\"") + 16;
                            int endIndex = e.getMessage().indexOf("\",", startIndex);
                            view.showErrorMessage("Failed to appoint owner: " + e.getMessage().substring(startIndex, endIndex));
                            e.printStackTrace();
                        }
                    } else {
                        view.showErrorMessage("Authorization token not found. Please log in.");
                    }
                });
    }

    public void addNewProduct(String productName, Category category, double price)
    {
        RestTemplate restTemplate = new RestTemplate();
        UI.getCurrent().getPage().executeJs("return localStorage.getItem('authToken');")
                .then(String.class, token -> {
                    if (token != null && !token.isEmpty()) {
                        ProductDto productDto = new ProductDto(productName, category, price, -1);
                        HttpHeaders headers = new HttpHeaders();
                        headers.add("Authorization", token);
                        headers.setContentType(MediaType.APPLICATION_JSON); // Set content type

                        // Create the request object
                        // dtoWrapper request = new dtoWrapper(view.getShopId(), productDto);

                        // Use a strongly typed HttpEntity
                        HttpEntity<ProductDto> requestEntity = new HttpEntity<>(productDto, headers);

                        // Map<String, Object> requestBody = new HashMap<>();
                        // requestBody.put("shopId", view.getShopId());
                        // requestBody.put("productDto", productDto);
                            
                        // HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

                        try {
                            ResponseEntity<String> response = restTemplate.exchange(
                                "http://localhost:" + view.getServerPort() + "/api/shop/addProductToShop?shopId=" + view.getShopId(),
                                HttpMethod.POST,
                                requestEntity,
                                String.class
                            );

                            if (response.getStatusCode().is2xxSuccessful()) {
                                ObjectMapper objectMapper = new ObjectMapper();
                                JsonNode responseJson = objectMapper.readTree(response.getBody());

                                if (responseJson.get("errorMessage").isNull()) {
                                    view.showSuccessMessage("Product added to shop successfully");
                                } else {
                                    view.showErrorMessage("Failed to add product to shop: " + responseJson.get("errorMessage").asText());
                                }
                            } else {
                                view.showErrorMessage("Failed to add product to shop with status code: " + response.getStatusCodeValue());
                            }
                        } catch (HttpClientErrorException e) {
                            view.showErrorMessage("HTTP error: " + e.getStatusCode());
                        } catch (Exception e) {
                            int startIndex = e.getMessage().indexOf("\"errorMessage\":\"") + 16;
                            int endIndex = e.getMessage().indexOf("\",", startIndex);
                            view.showErrorMessage(e.getMessage().substring(startIndex, endIndex));
                            e.printStackTrace();
                        }
                    } else {
                        view.showErrorMessage("Authorization token not found. Please log in.");
                    }
                });
    }

    public void addNewProduct(String productName, String category, double price)
    {

    }


    public void fetchShopDiscounts(Consumer<List<ShopDiscountDto>> callback){
        RestTemplate restTemplate = new RestTemplate();
        UI.getCurrent().getPage().executeJs("return localStorage.getItem('authToken');")
                .then(String.class, token -> {
                    if (token != null && !token.isEmpty()) {
                        HttpHeaders headers = new HttpHeaders();
                        headers.add("Authorization", token);

                        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

                    try{
                        ResponseEntity<Response> response = restTemplate.exchange(
                                "http://localhost:" + view.getServerPort() + "/api/shop/getShopDiscounts?shopId="+view.getShopId(),
                                HttpMethod.GET,
                                requestEntity,
                                Response.class);

                        
                            if (response.getStatusCode().is2xxSuccessful()) {
                                Response responseBody = response.getBody();
                                view.showSuccessMessage("Discounts loaded successfully");
                                if (responseBody.getErrorMessage() == null) {
                                    ObjectMapper objectMapper = new ObjectMapper();
                                    List<ShopDiscountDto> discounts = objectMapper.convertValue(
                                        responseBody.getReturnValue(),
                                        TypeFactory.defaultInstance().constructCollectionType(List.class, ShopDiscountDto.class));
                                    callback.accept(discounts);
                                }else {
                                    view.showErrorMessage("Discounts loading failed");
                                }
                            }
                            else {
                                view.showErrorMessage("Discounts loading failed with status code: " + response.getStatusCodeValue());
                            }
                        } catch (HttpClientErrorException e) {
                            view.showErrorMessage("HTTP error: " + e.getStatusCode());
                        } catch (Exception e) {
                            int startIndex = e.getMessage().indexOf("\"errorMessage\":\"") + 16;
                            int endIndex = e.getMessage().indexOf("\",", startIndex);
                            view.showErrorMessage("Failed to load discounts: " + e.getMessage().substring(startIndex, endIndex));
                            e.printStackTrace();
                        }
                    } else {
                        view.showErrorMessage("Authorization token not found. Please log in.");
                    }
                });
    }

    public void addDiscount(String discounType, boolean isPercentage, Double discountValue, Date expirationDate,
        Integer productId, Category category, Consumer<Boolean> callback){
        RestTemplate restTemplate = new RestTemplate();
        ShopDiscountDto discountDto = new ShopDiscountDto(productId, isPercentage, discountValue, expirationDate, category, -1); 
        UI.getCurrent().getPage().executeJs("return localStorage.getItem('authToken');")
                .then(String.class, token -> {
                    if (token != null && !token.isEmpty()) {
                        HttpHeaders headers = new HttpHeaders();
                        headers.add("Authorization", token);
                        HttpEntity<ShopDiscountDto> requestEntity = new HttpEntity<>(discountDto, headers);
                    try{
                        ResponseEntity<Response> response = restTemplate.exchange(
                                "http://localhost:" + view.getServerPort() + "/api/shop/addShopDiscount?shopId="+view.getShopId(),
                                HttpMethod.POST,
                                requestEntity,
                                Response.class);

                        
                            if (response.getStatusCode().is2xxSuccessful()) {
                                Response responseBody = response.getBody();
                                view.showSuccessMessage("Discount added loaded successfully");
                                if (responseBody.getErrorMessage() == null) {
                                    callback.accept(true);
                                }else {
                                    callback.accept(false);
                                    view.showErrorMessage("Discount adding failed");
                                }
                            }
                            else {
                                view.showErrorMessage("Discounts adding failed with status code: " + response.getStatusCodeValue());
                            }
                        } catch (HttpClientErrorException e) {
                            view.showErrorMessage("HTTP error: " + e.getStatusCode());
                        } catch (Exception e) {
                            int startIndex = e.getMessage().indexOf("\"errorMessage\":\"") + 16;
                            int endIndex = e.getMessage().indexOf("\",", startIndex);
                            view.showErrorMessage("Failed to add discount: " + e.getMessage().substring(startIndex, endIndex));
                            callback.accept(false);
                            e.printStackTrace();
                        }
                    } else {
                        view.showErrorMessage("Authorization token not found. Please log in.");
                    }
                });
    }

    public void deleteDiscount(ShopDiscountDto discountDto, Consumer<Boolean> callback){
        RestTemplate restTemplate = new RestTemplate(); 
        UI.getCurrent().getPage().executeJs("return localStorage.getItem('authToken');")
                .then(String.class, token -> {
                    if (token != null && !token.isEmpty()) {
                        HttpHeaders headers = new HttpHeaders();
                        headers.add("Authorization", token);
                        HttpEntity<ShopDiscountDto> requestEntity = new HttpEntity<>(discountDto, headers);
                    try{
                        ResponseEntity<Response> response = restTemplate.exchange(
                                "http://localhost:" + view.getServerPort() + "/api/shop/deleteShopDiscount?shopId="+view.getShopId(),
                                HttpMethod.POST,
                                requestEntity,
                                Response.class);

                        
                            if (response.getStatusCode().is2xxSuccessful()) {
                                Response responseBody = response.getBody();
                                view.showSuccessMessage("Discount deleted successfully");
                                if (responseBody.getErrorMessage() == null) {
                                    callback.accept(true);
                                }else {
                                    callback.accept(false);
                                    view.showErrorMessage("Discount deletion failed");
                                }
                            }
                            else {
                                view.showErrorMessage("Discount deletion failed with status code: " + response.getStatusCodeValue());
                            }
                        } catch (HttpClientErrorException e) {
                            view.showErrorMessage("HTTP error: " + e.getStatusCode());
                        } catch (Exception e) {
                            int startIndex = e.getMessage().indexOf("\"errorMessage\":\"") + 16;
                            int endIndex = e.getMessage().indexOf("\",", startIndex);
                            view.showErrorMessage("Failed to delete discount: " + e.getMessage().substring(startIndex, endIndex));
                            callback.accept(false);
                            e.printStackTrace();
                        }
                    } else {
                        view.showErrorMessage("Authorization token not found. Please log in.");
                    }
                });
    }

    public void updatePermissions(String managerUserName, Set<Permission> permissions, Consumer<Boolean> callback) {
        RestTemplate restTemplate = new RestTemplate();
        UI.getCurrent().getPage().executeJs("return localStorage.getItem('authToken');")
                .then(String.class, token -> {
                    if (token != null && !token.isEmpty()) {
                        HttpHeaders headers = new HttpHeaders();
                        headers.add("Authorization", token);
                        headers.setContentType(MediaType.APPLICATION_JSON); // Set content type

                        // Convert permissions to a set of strings
                        Set<String> permissionsList = permissions.stream()
                                .map(Enum::name)
                                .collect(Collectors.toSet());

                        // Create request body with permissions
                        Map<String, Object> requestBody = new HashMap<>();
                        requestBody.put("shopId", view.getShopId());
                        requestBody.put("managerUsername", managerUserName);
                        requestBody.put("permissions", permissionsList);

                        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

                        try {
                            ResponseEntity<String> response = restTemplate.exchange(
                                "http://localhost:" + view.getServerPort() + "/api/shop/updatePermissions",
                                HttpMethod.POST,
                                requestEntity,
                                String.class
                            );

                            if (response.getStatusCode().is2xxSuccessful()) {
                                ObjectMapper objectMapper = new ObjectMapper();
                                JsonNode responseJson = objectMapper.readTree(response.getBody());

                                if (responseJson.get("errorMessage").isNull()) {
                                    view.showSuccessMessage("Manager permissions changed successfully");
                                    callback.accept(true);
                                } else {
                                    view.showErrorMessage("Failed to change manager permissions: " + responseJson.get("errorMessage").asText());
                                    callback.accept(false);
                                }
                            } else {
                                view.showErrorMessage("Failed to change manager permissions with status code: " + response.getStatusCodeValue());
                                callback.accept(false);
                            }
                        } catch (HttpClientErrorException e) {
                            view.showErrorMessage("HTTP error: " + e.getStatusCode());
                            callback.accept(false);
                        } catch (Exception e) {
                            int startIndex = e.getMessage().indexOf("\"errorMessage\":\"") + 16;
                            int endIndex = e.getMessage().indexOf("\",", startIndex);
                            view.showErrorMessage("Failed to change manager permissions: " + e.getMessage().substring(startIndex, endIndex));
                            callback.accept(false);
                            e.printStackTrace();
                        }
                    } else {
                        view.showErrorMessage("Authorization token not found. Please log in.");
                        callback.accept(false);
                    }
                });
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

    public void reopenShop(String shopId)
    {
        RestTemplate restTemplate = new RestTemplate();
        UI.getCurrent().getPage().executeJs("return localStorage.getItem('authToken');")
                .then(String.class, token -> {
                    if (token != null && !token.isEmpty()) {
                        System.out.println("Token: " + token);

                        HttpHeaders headers = new HttpHeaders();
                        headers.add("Authorization", token);

                        HttpEntity<ShopDto> requestEntity = new HttpEntity<>(headers);

                        ResponseEntity<String> response = restTemplate.exchange(
                                "http://localhost:" + view.getServerPort() + "/api/shop/reopenShop?shopId=" + shopId ,
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


    public void fetchShopPolicy(Consumer<List<ShoppingBasketRuleDto>> callback){
        RestTemplate restTemplate = new RestTemplate();
        UI.getCurrent().getPage().executeJs("return localStorage.getItem('authToken');")
                .then(String.class, token -> {
                    if (token != null && !token.isEmpty()) {
                        HttpHeaders headers = new HttpHeaders();
                        headers.add("Authorization", token);

                        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

                    try{
                        ResponseEntity<Response> response = restTemplate.exchange(
                                "http://localhost:" + view.getServerPort() + "/api/shop/getShopPolicy?shopId="+view.getShopId(),
                                HttpMethod.GET,
                                requestEntity,
                                Response.class);

                        
                            if (response.getStatusCode().is2xxSuccessful()) {
                                Response responseBody = response.getBody();
                                view.showSuccessMessage("Shop Policy loaded successfully");
                                if (responseBody.getErrorMessage() == null) {
                                    ObjectMapper objectMapper = new ObjectMapper();
                                    List<ShoppingBasketRuleDto> rules = objectMapper.convertValue(
                                        responseBody.getReturnValue(),
                                        TypeFactory.defaultInstance().constructCollectionType(List.class, ShoppingBasketRuleDto.class));
                                    callback.accept(rules);
                                }else {
                                    view.showErrorMessage("Shop Policy loading failed");
                                }
                            }
                            else {
                                view.showErrorMessage("Shop Policy loading failed with status code: " + response.getStatusCodeValue());
                            }
                        } catch (HttpClientErrorException e) {
                            view.showErrorMessage("HTTP error: " + e.getStatusCode());
                        } catch (Exception e) {
                            int startIndex = e.getMessage().indexOf("\"errorMessage\":\"") + 16;
                            int endIndex = e.getMessage().indexOf("\",", startIndex);
                            view.showErrorMessage("Failed to load shop policy: " + e.getMessage().substring(startIndex, endIndex));
                            e.printStackTrace();
                        }
                    } else {
                        view.showErrorMessage("Authorization token not found. Please log in.");
                    }
                });
    }

    public void updateShopPolicy(List<ShoppingBasketRuleDto> newRules, Consumer<Boolean> callback) {
        RestTemplate restTemplate = new RestTemplate();
        UI.getCurrent().getPage().executeJs("return localStorage.getItem('authToken');")
                .then(String.class, token -> {
                    if (token != null && !token.isEmpty()) {
                        HttpHeaders headers = new HttpHeaders();
                        headers.add("Authorization", token);
                        headers.setContentType(MediaType.APPLICATION_JSON); // Set content type

                        // Prepare the request body with permissions
                        ShopPolicyRulesList requestBody = new ShopPolicyRulesList();
                        for(ShoppingBasketRuleDto rule : newRules){
                            requestBody.add(rule);
                        }

                        ObjectMapper objectMapper = new ObjectMapper();
                        // DEBUG: Log request body before serialization
                        try {
                            String requestBodyJson = objectMapper.writeValueAsString(requestBody);
                            System.out.println("Request Body JSON: " + requestBodyJson);
                        } catch (JsonProcessingException e) {
                            System.err.println("Failed to convert request body to JSON: " + e.getMessage());
                        }

                        HttpEntity<ShopPolicyRulesList> requestEntity = new HttpEntity<>(requestBody, headers);


                        try {
                            ResponseEntity<String> response = restTemplate.exchange(
                                    "http://localhost:" + view.getServerPort() + "/api/shop/updateShopPolicy?shopId="+view.getShopId(),
                                    HttpMethod.POST,
                                    requestEntity,
                                    String.class
                            );

                            if (response.getStatusCode().is2xxSuccessful()) {
                                ObjectMapper objectMapper2 = new ObjectMapper();
                                JsonNode responseJson = objectMapper2.readTree(response.getBody());

                                if (responseJson.get("errorMessage").isNull()) {
                                    view.showSuccessMessage("Shop policy changed successfully");
                                    callback.accept(true);
                                } else {
                                    view.showErrorMessage("Failed to change shop policy: " + responseJson.get("errorMessage").asText());
                                    callback.accept(false);
                                }
                            } else {
                                view.showErrorMessage("Failed to change shop policy with status code: " + response.getStatusCodeValue());
                                callback.accept(false);
                            }
                        } catch (HttpClientErrorException e) {
                            view.showErrorMessage("HTTP error: " + e.getStatusCode());
                            callback.accept(false);
                        } catch (Exception e) {
                            int startIndex = e.getMessage().indexOf("\"errorMessage\":\"") + 16;
                            int endIndex = e.getMessage().indexOf("\",", startIndex);
                            view.showErrorMessage("Failed to change shop policy: " + e.getMessage().substring(startIndex, endIndex));
                            callback.accept(false);
                            e.printStackTrace();
                        }
                    } else {
                        view.showErrorMessage("Authorization token not found. Please log in.");
                        callback.accept(false);
                    }
                });
    }

    public void updateProductPolicy(List<UserRuleDto> newRules, ProductDto product, Consumer<Boolean> callback) {
        RestTemplate restTemplate = new RestTemplate();
        UI.getCurrent().getPage().executeJs("return localStorage.getItem('authToken');")
                .then(String.class, token -> {
                    if (token != null && !token.isEmpty()) {
                        HttpHeaders headers = new HttpHeaders();
                        headers.add("Authorization", token);
                        headers.setContentType(MediaType.APPLICATION_JSON); // Set content type

                        // Prepare the request body with permissions
                        ProductPolicyRuleList requestBody = new ProductPolicyRuleList();
                        for(UserRuleDto rule : newRules){
                            requestBody.add(rule);
                        }

                        ObjectMapper objectMapper = new ObjectMapper();
                        // DEBUG: Log request body before serialization
                        try {
                            String requestBodyJson = objectMapper.writeValueAsString(requestBody);
                            System.out.println("Request Body JSON: " + requestBodyJson);
                        } catch (JsonProcessingException e) {
                            System.err.println("Failed to convert request body to JSON: " + e.getMessage());
                        }

                        HttpEntity<ProductPolicyRuleList> requestEntity = new HttpEntity<>(requestBody, headers);


                        try {
                            ResponseEntity<String> response = restTemplate.exchange(
                                    "http://localhost:" + view.getServerPort() + "/api/shop/updateProductPolicy?shopId="+view.getShopId() + "&productId=" + product.getProductId(),
                                    HttpMethod.POST,
                                    requestEntity,
                                    String.class
                            );

                            if (response.getStatusCode().is2xxSuccessful()) {
                                ObjectMapper objectMapper2 = new ObjectMapper();
                                JsonNode responseJson = objectMapper2.readTree(response.getBody());

                                if (responseJson.get("errorMessage").isNull()) {
                                    view.showSuccessMessage("Product "+product.getProductId()+" policy changed successfully");
                                    callback.accept(true);
                                } else {
                                    view.showErrorMessage("Failed to change "+product.getProductId()+" policy: " + responseJson.get("errorMessage").asText());
                                    callback.accept(false);
                                }
                            } else {
                                view.showErrorMessage("Failed to change "+product.getProductId()+" policy with status code: " + response.getStatusCodeValue());
                                callback.accept(false);
                            }
                        } catch (HttpClientErrorException e) {
                            view.showErrorMessage("HTTP error: " + e.getStatusCode());
                            callback.accept(false);
                        } catch (Exception e) {
                            int startIndex = e.getMessage().indexOf("\"errorMessage\":\"") + 16;
                            int endIndex = e.getMessage().indexOf("\",", startIndex);
                            view.showErrorMessage("Failed to change "+product.getProductId()+" policy: " + e.getMessage().substring(startIndex, endIndex));
                            callback.accept(false);
                            e.printStackTrace();
                        }
                    } else {
                        view.showErrorMessage("Authorization token not found. Please log in.");
                        callback.accept(false);
                    }
                });
    }
    


    public void fetchProductPolicy(ProductDto product, Consumer<List<UserRuleDto>> callback){
        RestTemplate restTemplate = new RestTemplate();
        UI.getCurrent().getPage().executeJs("return localStorage.getItem('authToken');")
                .then(String.class, token -> {
                    if (token != null && !token.isEmpty()) {
                        HttpHeaders headers = new HttpHeaders();
                        headers.add("Authorization", token);

                        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

                    try{
                        ResponseEntity<Response> response = restTemplate.exchange(
                                "http://localhost:" + view.getServerPort() + "/api/shop/getProductPolicy?shopId="+view.getShopId() + "&productId=" + product.getProductId(),
                                HttpMethod.GET,
                                requestEntity,
                                Response.class);

                        
                            if (response.getStatusCode().is2xxSuccessful()) {
                                Response responseBody = response.getBody();
                                view.showSuccessMessage("Product with id "+product.getProductId()+" policy loaded successfully");
                                if (responseBody.getErrorMessage() == null) {
                                    ObjectMapper objectMapper = new ObjectMapper();
                                    List<UserRuleDto> rules = objectMapper.convertValue(
                                        responseBody.getReturnValue(),
                                        TypeFactory.defaultInstance().constructCollectionType(List.class, UserRuleDto.class));
                                    callback.accept(rules);
                                }else {
                                    view.showErrorMessage("Product with id "+product.getProductId()+" policy loading failed");
                                }
                            }
                            else {
                                view.showErrorMessage("Product with id "+product.getProductId()+" policy loading failed with status code: " + response.getStatusCodeValue());
                            }
                        } catch (HttpClientErrorException e) {
                            view.showErrorMessage("HTTP error: " + e.getStatusCode());
                        } catch (Exception e) {
                            int startIndex = e.getMessage().indexOf("\"errorMessage\":\"") + 16;
                            int endIndex = e.getMessage().indexOf("\",", startIndex);
                            view.showErrorMessage("Failed to load Product with id "+product.getProductId()+" policy: " + e.getMessage().substring(startIndex, endIndex));
                            e.printStackTrace();
                        }
                    } else {
                        view.showErrorMessage("Authorization token not found. Please log in.");
                    }
                });
    }



    public void updateProductQuantity(Integer shopId, Integer productId, Integer quantity)
    {
        RestTemplate restTemplate = new RestTemplate();
        UI.getCurrent().getPage().executeJs("return localStorage.getItem('authToken');")
                .then(String.class, token -> {
                    if (token != null && !token.isEmpty()) {
                        System.out.println("Token: " + token);

                        HttpHeaders headers = new HttpHeaders();
                        headers.add("Authorization", token);
                
                        try{
                            HttpEntity<ShopDto> requestEntity = new HttpEntity<>(headers);

                            ResponseEntity<String> response = restTemplate.exchange(
                                    "http://localhost:" + view.getServerPort() + "/api/shop/updateProductQuantity?shopId=" + 
                                shopId + "&productId=" + productId + "&quantity=" + quantity,
                                    HttpMethod.POST,
                                    requestEntity,
                                    String.class);

                            if (response.getStatusCode().is2xxSuccessful()) {
                                view.showSuccessMessage("The product quantity has been updated successfully.");
                                System.out.println(response.getBody());
                            } else {
                                view.showErrorMessage("Failed to update the product");
                            }
                        }
                        catch (HttpClientErrorException e) {
                            view.showErrorMessage("HTTP error: " + e.getStatusCode());
                        } catch (Exception e) {
                            String errorMessage = "An error occurred";
                            try {
                                String message = e.getMessage();
                                int startIndex = message.indexOf("\"errorMessage\":\"") + 16;
                                int endIndex = message.indexOf("\",", startIndex);
                                
                                if (startIndex >= 16 && endIndex > startIndex) {
                                    errorMessage = message.substring(startIndex, endIndex);
                                }
                            } catch (Exception ex) {
                                // Handle any unexpected errors in extracting the error message
                                errorMessage = "An unexpected error occurred while processing the error message.";
                            }
                            
                            view.showErrorMessage("Error: " + errorMessage);
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("Token not found in local storage.");
                        view.showErrorMessage("Failed to update the product");
                    }
                });
    }

    public void updateProductPrice(Integer shopId, Integer productId, Double price)
    {
        RestTemplate restTemplate = new RestTemplate();
        UI.getCurrent().getPage().executeJs("return localStorage.getItem('authToken');")
                .then(String.class, token -> {
                    if (token != null && !token.isEmpty()) {
                        System.out.println("Token: " + token);

                        HttpHeaders headers = new HttpHeaders();
                        headers.add("Authorization", token);
                
                        try{
                            HttpEntity<ShopDto> requestEntity = new HttpEntity<>(headers);

                            ResponseEntity<String> response = restTemplate.exchange(
                                    "http://localhost:" + view.getServerPort() + "/api/shop/updateProductPrice?shopId=" + 
                                shopId + "&productId=" + productId + "&price=" + price,
                                    HttpMethod.POST,
                                    requestEntity,
                                    String.class);

                            if (response.getStatusCode().is2xxSuccessful()) {
                                view.showSuccessMessage("The product price has been updated successfully.");
                                System.out.println(response.getBody());
                            } else {
                                view.showErrorMessage("Failed to update the product");
                            }
                        }
                        catch (HttpClientErrorException e) {
                            view.showErrorMessage("HTTP error: " + e.getStatusCode());
                        } catch (Exception e) {
                            String errorMessage = "An error occurred";
                            try {
                                String message = e.getMessage();
                                int startIndex = message.indexOf("\"errorMessage\":\"") + 16;
                                int endIndex = message.indexOf("\",", startIndex);
                                
                                if (startIndex >= 16 && endIndex > startIndex) {
                                    errorMessage = message.substring(startIndex, endIndex);
                                }
                            } catch (Exception ex) {
                                // Handle any unexpected errors in extracting the error message
                                errorMessage = "An unexpected error occurred while processing the error message.";
                            }
                            
                            view.showErrorMessage("Error: " + errorMessage);
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("Token not found in local storage.");
                        view.showErrorMessage("Failed to update the product");
                    }
                });
    }

    public void updateProductName(Integer shopId, Integer productId, String name)
    {
        RestTemplate restTemplate = new RestTemplate();
        UI.getCurrent().getPage().executeJs("return localStorage.getItem('authToken');")
                .then(String.class, token -> {
                    if (token != null && !token.isEmpty()) {
                        System.out.println("Token: " + token);

                        HttpHeaders headers = new HttpHeaders();
                        headers.add("Authorization", token);
                
                        try{
                            HttpEntity<ShopDto> requestEntity = new HttpEntity<>(headers);

                            ResponseEntity<String> response = restTemplate.exchange(
                                    "http://localhost:" + view.getServerPort() + "/api/shop/updateProductName?shopId=" + 
                                shopId + "&productId=" + productId + "&name=" + name,
                                    HttpMethod.POST,
                                    requestEntity,
                                    String.class);

                            if (response.getStatusCode().is2xxSuccessful()) {
                                view.showSuccessMessage("The product name has been updated successfully.");
                                System.out.println(response.getBody());
                            } else {
                                view.showErrorMessage("Failed to update the product");
                            }
                        }
                        catch (HttpClientErrorException e) {
                            view.showErrorMessage("HTTP error: " + e.getStatusCode());
                        } catch (Exception e) {
                            String errorMessage = "An error occurred";
                            try {
                                String message = e.getMessage();
                                int startIndex = message.indexOf("\"errorMessage\":\"") + 16;
                                int endIndex = message.indexOf("\",", startIndex);
                                
                                if (startIndex >= 16 && endIndex > startIndex) {
                                    errorMessage = message.substring(startIndex, endIndex);
                                }
                            } catch (Exception ex) {
                                // Handle any unexpected errors in extracting the error message
                                errorMessage = "An unexpected error occurred while processing the error message.";
                            }
                            
                            view.showErrorMessage("Error: " + errorMessage);
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("Token not found in local storage.");
                        view.showErrorMessage("Failed to update the product");
                    }
                });
    }

    public void updateProductCategory(Integer shopId, Integer productId, Category category)
    {
        RestTemplate restTemplate = new RestTemplate();
        UI.getCurrent().getPage().executeJs("return localStorage.getItem('authToken');")
                .then(String.class, token -> {
                    if (token != null && !token.isEmpty()) {
                        System.out.println("Token: " + token);

                        HttpHeaders headers = new HttpHeaders();
                        headers.add("Authorization", token);
                
                        try{
                            HttpEntity<ShopDto> requestEntity = new HttpEntity<>(headers);

                            ResponseEntity<String> response = restTemplate.exchange(
                                    "http://localhost:" + view.getServerPort() + "/api/shop/updateProductCategory?shopId=" + 
                                shopId + "&productId=" + productId + "&category=" + category,
                                    HttpMethod.POST,
                                    requestEntity,
                                    String.class);

                            if (response.getStatusCode().is2xxSuccessful()) {
                                view.showSuccessMessage("The product category has been updated successfully.");
                                System.out.println(response.getBody());
                            } else {
                                view.showErrorMessage("Failed to update the product");
                            }
                        }
                        catch (HttpClientErrorException e) {
                            view.showErrorMessage("HTTP error: " + e.getStatusCode());
                        } catch (Exception e) {
                            String errorMessage = "An error occurred";
                            try {
                                String message = e.getMessage();
                                int startIndex = message.indexOf("\"errorMessage\":\"") + 16;
                                int endIndex = message.indexOf("\",", startIndex);
                                
                                if (startIndex >= 16 && endIndex > startIndex) {
                                    errorMessage = message.substring(startIndex, endIndex);
                                }
                            } catch (Exception ex) {
                                // Handle any unexpected errors in extracting the error message
                                errorMessage = "An unexpected error occurred while processing the error message.";
                            }
                            
                            view.showErrorMessage("Error: " + errorMessage);
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("Token not found in local storage.");
                        view.showErrorMessage("Failed to update the product");
                    }
                });
    }

    @SuppressWarnings("rawtypes")
public void getShopPurchaseHistory(Integer shopId) {
    RestTemplate restTemplate = new RestTemplate();

    UI.getCurrent().getPage().executeJs("return localStorage.getItem('authToken');")
            .then(String.class, token -> {
                if (token != null && !token.isEmpty()) {
                    System.out.println("Token: " + token);

                    HttpHeaders headers = new HttpHeaders();
                    headers.add("Authorization", token);

                    HttpEntity<String> requestEntity = new HttpEntity<>(headers);

                    ResponseEntity<Response> response = restTemplate.exchange(
                            "http://localhost:" + view.getServerPort() + "/api/shop/getShopPurchaseHistory?shopId="
                                    + shopId,
                            HttpMethod.GET,
                            requestEntity,
                            Response.class);

                    if (response.getStatusCode().is2xxSuccessful()) {
                        Response responseBody = response.getBody();

                        if (responseBody.getErrorMessage() == null) {
                            ObjectMapper objectMapper = new ObjectMapper();
                            List<ShopOrderDto> orderDtoList = objectMapper.convertValue(
                                    responseBody.getReturnValue(),
                                    TypeFactory.defaultInstance().constructCollectionType(List.class,
                                            ShopOrderDto.class));
                            view.showShopOrders(orderDtoList);
                            view.showSuccessMessage("Orders Showed successfully");
                        } else {
                            view.showErrorMessage(responseBody.getErrorMessage());
                        }
                    } else {
                        view.showErrorMessage("Failed to show Orders");
                    }
                } else {
                    System.out.println("Token not found in local storage.");
                    view.showErrorMessage("Failed to show Orders");
                }
            });
}
    
    public void fetchShopProducts(Consumer<List<ProductDto>> callback) {
        RestTemplate restTemplate = new RestTemplate();
        UI.getCurrent().getPage().executeJs("return localStorage.getItem('authToken');")
                .then(String.class, token -> {
                    if (token != null && !token.isEmpty()) {
                        HttpHeaders headers = new HttpHeaders();
                        headers.add("Authorization", token);

                        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

                        try{
                            ResponseEntity<Response> response = restTemplate.exchange(
                                    "http://localhost:" + view.getServerPort() + "/api/shop/getAllProductInShop?shopId="+view.getShopId(),
                                    HttpMethod.GET,
                                    requestEntity,
                                    Response.class);
                                    

                            if (response.getStatusCode().is2xxSuccessful()) {
                                Response responseBody = response.getBody();
                                view.showSuccessMessage("Shop products loaded successfully");
                                if (responseBody.getErrorMessage() == null) {
                                    ObjectMapper objectMapper = new ObjectMapper();
                                    List<ProductDto> productDtoList = objectMapper.convertValue(
                                                                        responseBody.getReturnValue(),
                                                                        TypeFactory.defaultInstance().constructCollectionType(List.class, ProductDto.class));
                                    callback.accept(productDtoList);
                                }else {
                                    view.showErrorMessage("Shop products loading failed");
                                }
                            }
                            else {
                                    view.showErrorMessage("Shop products loading failed with status code: " + response.getStatusCodeValue());
                            }
                        } catch (HttpClientErrorException e) {
                            view.showErrorMessage("HTTP error: " + e.getStatusCode());
                        } catch (Exception e) {
                            int startIndex = e.getMessage().indexOf("\"errorMessage\":\"") + 16;
                            int endIndex = e.getMessage().indexOf("\",", startIndex);
                            view.showErrorMessage("Failed to load shop products: " + e.getMessage().substring(startIndex, endIndex));
                            e.printStackTrace();
                        }
                    } else {
                        view.showErrorMessage("Authorization token not found. Please log in.");
                    }
                });
    }

}
