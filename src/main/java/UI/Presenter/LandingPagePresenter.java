package UI.Presenter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.UI;
import UI.View.LandingPageView;
import UI.WebSocketClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class LandingPagePresenter {

    private final LandingPageView view;
    private final String serverPort = "8080";
    private final WebSocketClient webSocketClient;

    @Autowired
    public LandingPagePresenter(LandingPageView view, WebSocketClient webSocketClient) {
        this.view = view;
        this.webSocketClient = webSocketClient;
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

            // Connect using WebSocketClient
            webSocketClient.connect(token);

            // Optionally, you can handle the response here
            System.out.println("Extracted Token: " + token);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            view.showErrorMessage("Failed to parse response");
        }
    }
}
