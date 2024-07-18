package UI.Presenter;

import java.util.List;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.UI;

import UI.Model.SearchShopResponseDto;
import UI.Model.ShopDto;
import UI.View.Header;
import UI.View.SearchShopResultsView;

public class SearchShopPresenter {

    private final String _serverPort;
    private final Header headerView;
    private SearchShopResultsView searchShopsResultsView;

    public SearchShopPresenter(Header headerView, String serverPort) {
        this.headerView = headerView;
        this._serverPort = serverPort;
    }

    public void setSearchShopsResultsView(SearchShopResultsView searchShopsResultsView) {
        this.searchShopsResultsView = searchShopsResultsView;
    }


    @SuppressWarnings("deprecation")
    public void searchShop(Integer shopId, String shopName) {
        RestTemplate restTemplate = new RestTemplate();
        UI.getCurrent().getPage().executeJs("return localStorage.getItem('authToken');")
                .then(String.class, token -> {
                    if (token != null && !token.isEmpty()) {
                        HttpHeaders headers = new HttpHeaders();
                        headers.add("Authorization", token);
                        String url = "";
                        if (shopId != null) {
                            url = "http://localhost:" + _serverPort + "/api/shop/searchAndDisplayShopByID?shopId=" + shopId;
                        }
                        if (shopName != null && !shopName.isEmpty()) {
                            url = "http://localhost:" + _serverPort + "/api/shop/searchAndDisplayShopByName?shopName=" + shopName;
                        }

                        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
                        ObjectMapper objectMapper = new ObjectMapper();

                        try {
                            ResponseEntity<String> response = restTemplate.exchange(
                                url,
                                HttpMethod.GET,
                                requestEntity,
                                String.class);
                            
                            String responseBody = response.getBody();
                                if (response.getStatusCode().is2xxSuccessful()) {
                                    // convert to ResponseDTO
                                    SearchShopResponseDto responseDto = objectMapper.readValue(responseBody, SearchShopResponseDto.class);
                                    List<ShopDto> shopDtosList = responseDto.getReturnValue();
            
                                    if (shopDtosList == null) {
                                        searchShopsResultsView.showErrorMessage("Searched shops loading failed");
                                        searchShopsResultsView.getUI().ifPresent(ui -> ui.navigate("user"));
                                    }
                                    else if (shopDtosList.isEmpty()) {
                                        searchShopsResultsView.displayResponseShopNotFound(shopId, shopName);
                                    } 
                                    else {
                                        searchShopsResultsView.displayResponseShops(shopDtosList);
                                    }
                                }   
                                else {
                                    headerView.showErrorMessage("Search Results loading failed with status code: " + response.getStatusCodeValue());
                                }
                        } catch (HttpClientErrorException e) {
                            ResponseHandler.handleResponse(e.getStatusCode());
                            if (e.getMessage().contains("not exist")) {
                                searchShopsResultsView.displayResponseShopNotFound(shopId, shopName);
                            }
                        } catch (Exception e) {
                            headerView.showErrorMessage(ErrorMessageGenerator.generateGenericErrorMessage(e.getMessage()));
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("Token not found in local storage.");
                        headerView.showErrorMessage("The shop search failed");
                    }
                });
    }
    

}
