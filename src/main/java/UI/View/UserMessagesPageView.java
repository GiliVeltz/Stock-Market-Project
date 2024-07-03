package UI.View;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import UI.Message;
import UI.Presenter.UserMessagesPagePresenter;

@PageTitle("User Messages Page")
@Route(value = "user_messages")
public class UserMessagesPageView extends VerticalLayout {

    private UserMessagesPagePresenter presenter;
    private String _username;
    private VerticalLayout messagesLayout;
    private ProgressBar loadingIndicator;
    private List<Message> allMessages = new ArrayList<>();
    private List<Message> allCurrentMessages = new ArrayList<>();

    public UserMessagesPageView() {
        // Retrieve the username from the session
        _username = (String) VaadinSession.getCurrent().getAttribute("username");

        // Initialize presenter
        presenter = new UserMessagesPagePresenter(this);

        // Create the header component
        H1 title = new H1("My Messages");
        title.addClassName("title");
        add(title);

        // Add filter options
        HorizontalLayout filterOptions = new HorizontalLayout();
        filterOptions.addClassName("filter-options");

        TextField searchField = new TextField();
        searchField.setPlaceholder("Search messages...");
        searchField.addValueChangeListener(e -> filterMessages(e.getValue()));
        filterOptions.add(searchField);

        Button showAllButton = new Button("Show All");
        showAllButton.addClassName("filter-button");
        showAllButton.addClickListener(e -> showAllMessages());
        filterOptions.add(showAllButton);

        Button showUnreadButton = new Button("Show Unread");
        showUnreadButton.addClassName("filter-button");
        showUnreadButton.addClickListener(e -> showUnreadMessages());
        filterOptions.add(showUnreadButton);

        add(filterOptions);

        // Add loading indicator
        loadingIndicator = new ProgressBar();
        loadingIndicator.setIndeterminate(true);
        loadingIndicator.addClassName("loading-indicator");
        add(loadingIndicator);

        // Initialize messages layout
        messagesLayout = new VerticalLayout();
        messagesLayout.addClassName("messages-layout");
        add(messagesLayout);

        // Fetch all messages initially
        presenter.fetchMessages(_username);
    }

    private void showAllMessages() {
        messagesLayout.removeAll(); // Clear existing messages
        createMessageTextArea(allCurrentMessages); // Display all messages
    }

    private void showUnreadMessages() {
        messagesLayout.removeAll(); // Clear existing messages
        List<Message> unreadMessages = allCurrentMessages.stream()
                .filter(message -> !message.isRead())
                .collect(Collectors.toList());
        createUnreadMessageTextArea(unreadMessages); // Display unread messages
    }


    public void createMessageTextArea(List<Message> messages) {
        messagesLayout.removeAll(); // Clear existing messages
        loadingIndicator.setVisible(false); // Hide loading indicator

        if (messages.isEmpty()) {
            Paragraph noMessagesParagraph = new Paragraph("No messages found");
            noMessagesParagraph.addClassName("no-messages");
            messagesLayout.add(noMessagesParagraph);
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (Message message : messages) {
            HorizontalLayout messageLayout = new HorizontalLayout();
            messageLayout.addClassName("message-layout");
            if (!message.isRead()) {
                messageLayout.addClassName("unread-message");
            }

            // Add double-click event listener to the entire message layout
            messageLayout.addClickListener(e -> {
                if (e.getClickCount() == 2) { // Double-click detected
                    showFullMessageDialog(message.getMessage());
                }
            });

            Span messageTextArea = new Span();
            messageTextArea.addClassName("message-text-area");
            messageTextArea.setText(truncateMessage(message.getMessage()));

            Paragraph timestampParagraph = new Paragraph(message.getTimestamp().format(formatter));
            timestampParagraph.addClassName("timestamp");

            Button toggleReadButton = new Button(message.isRead() ? "Mark as Unread" : "Mark as Read");
            toggleReadButton.addClickListener(e -> {
                message.setRead(!message.isRead());
                presenter.updateMessageStatus(message); // Assume this method updates the message status in the backend
                refreshMessages(); // Refresh messages to reflect changes
            });
            toggleReadButton.addClassName("toggle-read-button");

            if (message.getMessage().length() > 200) { // Adjust this value based on your requirement
                Span readMoreSpan = new Span(" Read More");
                readMoreSpan.addClassName("read-more-link");
                readMoreSpan.getElement().getStyle().set("color", "blue").set("cursor", "pointer");

                readMoreSpan.addClickListener(e -> {
                    Dialog dialog = new Dialog();
                    dialog.setWidth("400px");
                    dialog.setHeight("300px");

                    Span fullMessageSpan = new Span(message.getMessage());
                    fullMessageSpan.addClassName("full-message-span");

                    dialog.add(fullMessageSpan);
                    dialog.open();
                });

                messageTextArea.add(readMoreSpan);
            }

            messageLayout.add(messageTextArea, timestampParagraph, toggleReadButton);
            messagesLayout.add(messageLayout);
        }
    }

    public void createUnreadMessageTextArea(List<Message> messages) {
        messagesLayout.removeAll(); // Clear existing messages
        loadingIndicator.setVisible(false); // Hide loading indicator

        if (messages.isEmpty()) {
            Paragraph noMessagesParagraph = new Paragraph("No unread messages found");
            noMessagesParagraph.addClassName("no-messages");
            messagesLayout.add(noMessagesParagraph);
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (Message message : messages) {
            HorizontalLayout messageLayout = new HorizontalLayout();
            messageLayout.addClassName("message-layout");
            if (!message.isRead()) {
                messageLayout.addClassName("unread-message");
            }

            // Add double-click event listener to the entire message layout
            messageLayout.addClickListener(e -> {
                if (e.getClickCount() == 2) { // Double-click detected
                    showFullMessageDialog(message.getMessage());
                }
            });

            Span messageTextArea = new Span();
            messageTextArea.addClassName("message-text-area");
            messageTextArea.setText(truncateMessage(message.getMessage()));

            Paragraph timestampParagraph = new Paragraph(message.getTimestamp().format(formatter));
            timestampParagraph.addClassName("timestamp");

            Button toggleReadButton = new Button(message.isRead() ? "Mark as Unread" : "Mark as Read");
            toggleReadButton.addClickListener(e -> {
                message.setRead(!message.isRead());
                presenter.updateMessageStatus(message); // Assume this method updates the message status in the backend
                refreshMessages(); // Refresh messages to reflect changes
            });
            toggleReadButton.addClassName("toggle-read-button");

            if (message.getMessage().length() > 200) { // Adjust this value based on your requirement
                Span readMoreSpan = new Span(" Read More");
                readMoreSpan.addClassName("read-more-link");
                readMoreSpan.getElement().getStyle().set("color", "blue").set("cursor", "pointer");

                readMoreSpan.addClickListener(e -> {
                    Dialog dialog = new Dialog();
                    dialog.setWidth("400px");
                    dialog.setHeight("300px");

                    Span fullMessageSpan = new Span(message.getMessage());
                    fullMessageSpan.addClassName("full-message-span");

                    dialog.add(fullMessageSpan);
                    dialog.open();
                });

                messageTextArea.add(readMoreSpan);
            }

            messageLayout.add(messageTextArea, timestampParagraph, toggleReadButton);
            messagesLayout.add(messageLayout);
        }
    }

    private String truncateMessage(String message) {
        return message.length() > 200 ? message.substring(0, 200) + "..." : message;
    }

    private void refreshMessages() {
        messagesLayout.removeAll();
        presenter.fetchMessages(_username);
    }

    private void filterMessages(String keyword) {
        messagesLayout.removeAll(); // Clear existing messages
        
        if (keyword == null || keyword.isEmpty()) {
            createMessageTextArea(allCurrentMessages);
        } else {
            List<Message> filteredMessages = allCurrentMessages.stream()
                .filter(message -> message.getMessage().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
            
            if (filteredMessages.isEmpty()) {
                Paragraph noMessagesParagraph = new Paragraph("No messages found");
                noMessagesParagraph.addClassName("no-messages");
                messagesLayout.add(noMessagesParagraph);
            } else {
                createMessageTextArea(filteredMessages);
            }
        }
    }

    private void showFullMessageDialog(String message) {
        Dialog dialog = new Dialog();
        dialog.setWidth("400px");
        dialog.setHeight("300px");

        Span fullMessageSpan = new Span(message);
        fullMessageSpan.addClassName("full-message-span");

        dialog.add(fullMessageSpan);
        dialog.open();
    }

    public void setCurrentMessages(List<Message> messages) {
        this.allCurrentMessages = new ArrayList<Message>(messages);
    }
}
