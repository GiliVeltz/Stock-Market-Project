package UI.Presenter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;

import UI.WebSocketClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;


public class BasePresenter {

    private static final String SERVER_PORT = "8080";
    private static final String SERVER_URL = "http://localhost:" + SERVER_PORT;

    
    @Autowired
    private WebSocketClient webSocketClient;

    public BasePresenter() {
        this.webSocketClient = new WebSocketClient();
    }

    // For the first time client enter the system [[If client has nothing in localStorage => It's first time]]
    public void checkToken() {
        UI.getCurrent().getPage().executeJs(
            "return localStorage.getItem('authToken');"
        ).then(String.class, existingToken -> {
            if (existingToken == null || existingToken.isEmpty()) {
                // No token in local storage, fetch a new one
                fetchAndStoreToken();
            }
        });
    }

    private void fetchAndStoreToken() {
        RestTemplate restTemplate = new RestTemplate();
        String serverUrl = "http://localhost:" + SERVER_PORT + "/api/system/enterSystem";

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(serverUrl, String.class);
            String responseBody = response.getBody();

            // Parse the response to extract the token
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode responseJson = objectMapper.readTree(responseBody);
            String token = responseJson.get("returnValue").asText();

            // Store the token in local storage using JavaScript
            UI.getCurrent().getPage().executeJs("localStorage.setItem('authToken', $0);", token);
            webSocketClient.connect(token);

            // Optionally, you can handle the response here
        } catch (Exception e) {
            e.printStackTrace();
            Notification.show("Failed to initialize system.");
        }
    }
}
