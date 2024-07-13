package UI.Presenter;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.vaadin.flow.component.UI;
import java.util.List;

import UI.Model.OrderDto;
import UI.Model.ShopDto;
import UI.Model.ShopOrderDto;
import UI.View.SystemAdminPageView;
import UI.Model.Response;
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
                        try
                        {
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
                        }  catch (HttpClientErrorException e) {
                            view.showErrorMessage("HTTP error: " + e.getStatusCode());
                        } catch (Exception e) {
                            int startIndex = e.getMessage().indexOf("\"errorMessage\":\"") + 16;
                            int endIndex = e.getMessage().indexOf("\",", startIndex);
                            view.showErrorMessage("Failed to close the shop: " + e.getMessage().substring(startIndex, endIndex));
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("Token not found in local storage.");
                        view.showErrorMessage("Failed to close the shop");
                    }
                });
    }

    public void getShopPurchaseHistory(String shopId) {
       RestTemplate restTemplate = new RestTemplate();
    }

 
    @SuppressWarnings("rawtypes")
    public void getUserPurchaseHistory(String username) {
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
                                    "http://localhost:" + view.getServerPort() + "/api/user/viewOrderHistory?username="
                                            + username,
                                    HttpMethod.GET,
                                    requestEntity,
                                    Response.class);

                            if (response.getStatusCode().is2xxSuccessful()) {
                                Response responseBody = response.getBody();

                                if (responseBody.getErrorMessage() == null) {
                                    ObjectMapper objectMapper = new ObjectMapper();
                                    List<OrderDto> orderDtoList = objectMapper.convertValue(
                                            responseBody.getReturnValue(),
                                            TypeFactory.defaultInstance().constructCollectionType(List.class,
                                                    OrderDto.class));
                                    view.showUserOrders(orderDtoList);
                                    view.showSuccessMessage("Orders Showed successfully");
                                } else {
                                    view.showErrorMessage(responseBody.getErrorMessage());
                                }
                            } else {
                                view.showErrorMessage("Failed to show Orders");
                            }
                        } catch (HttpClientErrorException e) {
                            view.showErrorMessage("HTTP error: " + e.getStatusCode());
                        } catch (Exception e) {
                            int startIndex = e.getMessage().indexOf("\"errorMessage\":\"") + 16;
                            int endIndex = e.getMessage().indexOf("\",", startIndex);
                            view.showErrorMessage("Failed to show Orders: " + e.getMessage().substring(startIndex, endIndex));
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("Token not found in local storage.");
                        view.showErrorMessage("Failed to show Orders");
                    }
                });
    }

    public void sendAlertNotification(String targetUser, String message) {
        RestTemplate restTemplate = new RestTemplate();
        UI.getCurrent().getPage().executeJs("return localStorage.getItem('authToken');")
                .then(String.class, token -> {
                    if (token != null && !token.isEmpty()) {
                        try
                        {
                            System.out.println("Token: " + token);

                            HttpHeaders headers = new HttpHeaders();
                            headers.add("Authorization", token);

                            HttpEntity<ShopDto> requestEntity = new HttpEntity<>(headers);

                            ResponseEntity<String> response = restTemplate.exchange(
                                    "http://localhost:" + view.getServerPort() + "/api/system/sendAlertNotification?targetUser=" + targetUser + "&message=" + message ,
                                    HttpMethod.POST,
                                    requestEntity,
                                    String.class);

                            if (response.getStatusCode().is2xxSuccessful()) {
                                view.showSuccessMessage("Alert sent successfully.");
                                System.out.println(response.getBody());
                            } else {
                                view.showErrorMessage("Failed to send alert");
                            }
                        }  catch (HttpClientErrorException e) {
                            view.showErrorMessage("HTTP error: " + e.getStatusCode());
                        } catch (Exception e) {
                            int startIndex = e.getMessage().indexOf("\"errorMessage\":\"") + 16;
                            int endIndex = e.getMessage().indexOf("\",", startIndex);
                            view.showErrorMessage("Failed to send alert: " + e.getMessage().substring(startIndex, endIndex));
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("Token not found in local storage.");
                        view.showErrorMessage("Failed to send alert");
                    }
                });
    }

}
