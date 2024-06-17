package UI.Presenter;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import UI.WebSocketClient;
import UI.View.UserMessagesPageView;
import UI.View.UserShopsPageView;
import UI.View.ViewPageI;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.UI;
import java.util.List;

public class UserMessagesPagePresenter {

    private final UserMessagesPageView view;

    public UserMessagesPagePresenter(UserMessagesPageView view) {
        this.view = view;
    }

    public void fetchMessages() {

     
 
        List<String> messages = WebSocketClient.getMessages();
        // Assuming messagesTextArea is a JTextArea or similar
        //create a TextArea for each message
        
            view.createMessageTextArea(messages);
        
    
    

    }
    
}