package UI.Presenter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.UI;

import UI.View.LandingPageView;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;


public class LandingPagePresenter {

    private final LandingPageView view;
    private final String serverPort = "8080";

    public LandingPagePresenter(LandingPageView view) {
        this.view = view;
    }

    public void sendEnterSystemRequest() {
        RestTemplate restTemplate = new RestTemplate();
        String serverUrl = "http://localhost:" + serverPort + "/api/system/enterSystem";

        ResponseEntity<String> response = restTemplate.getForEntity(serverUrl, String.class);

        String responseBody = response.getBody();
        // Parse the response to extract the token
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode responseJson = objectMapper.readTree(responseBody);
            String token = responseJson.get("returnValue").asText();

            // Store the token in local storage using JavaScript
            UI.getCurrent().getPage().executeJs("localStorage.setItem('authToken', $0);", token);

            // Optionally, you can handle the response here
            System.out.println("Extracted Token: " + token);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            view.showErrorMessage("Failed to parse response");
        }
    }
}