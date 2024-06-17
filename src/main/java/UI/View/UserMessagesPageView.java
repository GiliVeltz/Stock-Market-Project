package UI.View;

import java.time.format.DateTimeFormatter;
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

import UI.Presenter.UserMessagesPagePresenter;
import UI.Presenter.UserShopsPagePresenter;
import UI.clientNotifications.Message;
import UI.clientNotifications.WebSocketClient;

@PageTitle("User Messages Page")
@Route(value = "user_messages")
public class UserMessagesPageView extends VerticalLayout{

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
     public void createMessageTextArea(List<Message> messages) {
        if (messages.isEmpty()) {
            Paragraph noMessagesParagraph = new Paragraph("No messages found");
            noMessagesParagraph.getStyle().set("color", "red");
            this.add(noMessagesParagraph);
            return;
        }

        // Create a vertical layout for the messages
        VerticalLayout messagesLayout = new VerticalLayout();
        messagesLayout.setPadding(true);
        messagesLayout.setSpacing(true);

        // Define a date-time formatter
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (Message message : messages) {
            // Create the message layout
            HorizontalLayout messageLayout = new HorizontalLayout();
            messageLayout.setWidthFull();
            messageLayout.getStyle().set("border", "1px solid #ccc");
            messageLayout.getStyle().set("border-radius", "5px");
            messageLayout.getStyle().set("background-color", "#f0f0f0");
            messageLayout.getStyle().set("padding", "10px");
            messageLayout.getStyle().set("margin-bottom", "10px");

            // Create the message text area
            TextArea messageTextArea = new TextArea();
            messageTextArea.setValue(message.getMessage());
            messageTextArea.setReadOnly(true);
            messageTextArea.setWidth("80%");
            messageTextArea.getStyle().set("background-color", "transparent");
            messageTextArea.getStyle().set("border", "none");
            messageTextArea.getStyle().set("resize", "none");
            messageTextArea.getStyle().set("padding", "0");
            messageTextArea.getStyle().set("margin", "0");

            // Create the timestamp paragraph
            Paragraph timestampParagraph = new Paragraph(message.getTimestamp().format(formatter));
            timestampParagraph.getStyle().set("margin", "0");
            timestampParagraph.getStyle().set("color", "gray");
            timestampParagraph.getStyle().set("font-size", "smaller");
            timestampParagraph.getStyle().set("align-self", "center");
            timestampParagraph.getStyle().set("margin-left", "auto");

            // Add components to the message layout
            messageLayout.add(messageTextArea, timestampParagraph);

            // Add each message layout to the vertical layout
            messagesLayout.add(messageLayout);
        }

        this.add(messagesLayout);
    }

  


}
