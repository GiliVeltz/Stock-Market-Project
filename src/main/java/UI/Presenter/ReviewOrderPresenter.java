package UI.Presenter;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.vaadin.flow.component.UI;

import UI.Model.ShopDto;
import UI.View.UserOrderHistoryView;

public class ReviewOrderPresenter {
    
    private final UserOrderHistoryView view;

    public ReviewOrderPresenter(UserOrderHistoryView view) {
        this.view = view;
    }

    public void addShopRating(Integer shopId, Integer rating)
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
                                    "http://localhost:" + view.getServerPort() + "/api/shop/addShopRating?shopId=" + 
                                shopId + "&rating=" + rating,
                                    HttpMethod.POST,
                                    requestEntity,
                                    String.class);

                            if (response.getStatusCode().is2xxSuccessful()) {
                                view.showSuccessMessage("The shop rating has been updated successfully.");
                                System.out.println(response.getBody());
                            } else {
                                view.showErrorMessage("Failed to update shop's rating");
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
                        view.showErrorMessage("Failed to update the shop rating");
                    }
                });
    }



    public void addProductRatingAndReview(Integer shopId, Integer productId, Integer rating, String Review)
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
                                    "http://localhost:" + view.getServerPort() + "/api/shop/addProductRatingAndReview?shopId=" + 
                                shopId + "&productId=" + productId + "&rating=" + rating + "&review=" + Review,
                                    HttpMethod.POST,
                                    requestEntity,
                                    String.class);

                            if (response.getStatusCode().is2xxSuccessful()) {
                                view.showSuccessMessage("The Product rating and review have been updated successfully.");
                                System.out.println(response.getBody());
                            } else {
                                view.showErrorMessage("Failed to update products's rating and review");
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
                        view.showErrorMessage("Failed to update the shop rating");
                    }
                });
    }


}
