package UI.Presenter;

import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.vaadin.flow.component.UI;

import UI.Model.OrderDto;
import UI.Model.Response;
import UI.View.UserOrderHistoryView;

public class UserOrderHistoryPresenter {
    private UserOrderHistoryView view;

    public UserOrderHistoryPresenter(UserOrderHistoryView view) {
        this.view = view;
    }

    @SuppressWarnings("rawtypes")
    public void viewOrderHistory() {
        RestTemplate restTemplate = new RestTemplate();

        UI.getCurrent().getPage().executeJs("return localStorage.getItem('authToken');")
                .then(String.class, token -> {
                    if (token != null && !token.isEmpty()) {
                        System.out.println("Token: " + token);

                        HttpHeaders headers = new HttpHeaders();
                        headers.add("Authorization", token);

                        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

                        String username = (String) UI.getCurrent().getSession().getAttribute("username");
                        
                        try{
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
                                    view.showOrders(orderDtoList);
                                    view.showSuccessMessage("Orders Showed successfully");
                                } else {
                                    view.showErrorMessage(responseBody.getErrorMessage());
                                }
                            } else {
                                view.showErrorMessage("Failed to show Orders");
                            }
                        }
                        catch (HttpClientErrorException e) {
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
}