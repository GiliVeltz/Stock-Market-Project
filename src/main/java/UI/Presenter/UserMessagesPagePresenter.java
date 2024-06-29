package UI.Presenter;

import UI.Message;
import UI.WebSocketClient;
import UI.View.UserMessagesPageView;
import java.util.List;

public class UserMessagesPagePresenter {

    private final UserMessagesPageView view;

    public UserMessagesPagePresenter(UserMessagesPageView view) {
        this.view = view;
    }

    public void fetchMessages(String username) {

        List<Message> messages = WebSocketClient.getMessages(username);
        // Assuming messagesTextArea is a JTextArea or similar
        //create a TextArea for each message
        
            view.createMessageTextArea(messages);

    }

    public void updateMessageStatus(Message message) {
       WebSocketClient.updateMessageStatus(message);
    }
    
}