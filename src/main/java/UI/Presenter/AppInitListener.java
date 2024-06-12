package UI.Presenter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;

@Component
public class AppInitListener implements VaadinServiceInitListener {
    private String serverPort;

    @Override
    public void serviceInit(ServiceInitEvent event) {
        RestTemplate restTemplate = new RestTemplate();
        String serverUrl = "http://localhost:" + serverPort + "/api/system/enterSystem";

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(serverUrl, String.class);
            String responseBody = response.getBody();

            // Parse the response to extract the token
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode responseJson = objectMapper.readTree(responseBody);
            String token = responseJson.get("returnValue").asText();

            // Store the token in local storage using JavaScript
            event.getSource().addUIInitListener(uiInitEvent -> {
                UI.getCurrent().getPage().executeJs("localStorage.setItem('authToken', $0);", token);
            });

            // Optionally, you can handle the response here
            System.out.println("Extracted Token: " + token);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            // Handle other exceptions like network issues, non-200 status codes, etc.
            e.printStackTrace();
        }
    }
}

