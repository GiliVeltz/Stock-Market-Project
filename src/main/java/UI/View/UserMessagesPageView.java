package UI.View;

import java.util.List;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import UI.WebSocketClient;
import UI.Presenter.UserMessagesPagePresenter;
import UI.Presenter.UserShopsPagePresenter;

@PageTitle("User Messages Page")
@Route(value = "user_messages")
public class UserMessagesPageView extends VerticalLayout implements ViewPageI {

    private UserMessagesPagePresenter presenter;
    // private JTextArea messagesTextArea;

    private String _username;

    public UserMessagesPageView() {
        // Retrieve the username from the session
        _username = (String) VaadinSession.getCurrent().getAttribute("username");

        // Initialize presenter
        presenter = new UserMessagesPagePresenter(this);
       

        // Create the header component
        Header header = new BrowsePagesHeader("8080");
        add(header);

        H1 title = new H1("My Messages");
        title.getStyle().set("margin", "20px 0");
        add(title);
        // Initialize your UI components here
        // messagesTextArea = new JTextArea();
        // updateMessages();
        // WebSocketClient.addListener(this);
        presenter.fetchMessages();

    }
    public void createMessageTextArea(List<String> messages) {
        if (messages.isEmpty()) {
            Paragraph noMessagesParagraph = new Paragraph("No messages found");
            noMessagesParagraph.getStyle().set("color", "red");
            this.add(noMessagesParagraph);
        }
          // Create a vertical layout for the messages
          VerticalLayout messagesLayout = new VerticalLayout();
          messagesLayout.setPadding(true);
          messagesLayout.setSpacing(true);

          for (String message : messages) {
            // Use TextArea for editable messages or Label for read-only messages
            TextArea messageTextArea = new TextArea();
            messageTextArea.setValue("Message: " + message);
            messageTextArea.setReadOnly(true); // Make it read-only if editing is not required
            messageTextArea.setWidthFull(); // Set width to fill the container
            messageTextArea.getStyle().set("margin-bottom", "10px");
            messageTextArea.getStyle().set("background-color", "#f0f0f0");
            messageTextArea.getStyle().set("border", "1px solid #ccc");
            messageTextArea.getStyle().set("border-radius", "5px");
            messageTextArea.getStyle().set("padding", "10px");

            // Add each message TextArea to the vertical layout
            messagesLayout.add(messageTextArea);
        }

        this.add(messagesLayout);
    }

    @Override
    public void showSuccessMessage(String message) {
        Notification.show(message);
    }

    @Override
    public void showErrorMessage(String message) {
        Notification.show(message);
    }


}
