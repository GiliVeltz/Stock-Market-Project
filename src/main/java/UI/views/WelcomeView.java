package UI.views;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;


@PageTitle("Home Page")
@Route(value = "")
@RouteAlias(value = "")
public class WelcomeView extends VerticalLayout {

    private final String serverPort = "8080";

    public WelcomeView() {
        // Create the header component
        HeaderComponent header = new HeaderComponent(serverPort);

        // Create the title
        H1 title = new H1("Welcome to Stock Market!!");

        // Create a horizontal layout for the title to center it
        HorizontalLayout titleLayout = new HorizontalLayout();
        titleLayout.setWidthFull(); // Make the layout take full width
        titleLayout.setJustifyContentMode(JustifyContentMode.CENTER); // Center the content
        titleLayout.add(title);

        // Add components to the vertical layout
        add(header, titleLayout);

        // Send the enterSystem request
        sendEnterSystemRequest();
    }

    private void sendEnterSystemRequest() {
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
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to parse response");
        }
    }
}
