package UI.Presenter;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class ResponseHandler {
    public ResponseHandler() {
    }

    // Adjust the method signature as per your requirement
    public static void handleResponse(HttpStatusCode status_code) {
            if (status_code == HttpStatus.UNAUTHORIZED) {
                unauthorizedHandler();
            } 
        }

private static void unauthorizedHandler() {
    UI.getCurrent().access(() -> {
        // Create a dialog/modal
        Dialog dialog = new Dialog();
        dialog.setCloseOnEsc(false);
        dialog.setCloseOnOutsideClick(false);

        // Set content for the dialog
        Text message1 = new Text("Session Expired.");
        Text message2 = new Text("Please log in again if necessary.");
        
        // Add messages to separate lines
        Div messageDiv = new Div();
        messageDiv.add(message1, new Html("<br>"), message2);
        dialog.add(messageDiv);

        // Add "Got It" button
        Button okButton = new Button("Got It", event -> {
            // Delete authToken from localStorage
            UI.getCurrent().getPage().executeJs("localStorage.removeItem('authToken');");
            dialog.close();
            UI.getCurrent().getPage().executeJs("window.location.href = '/';");
        });

        // Vertical layout for message and button
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSpacing(true);
        verticalLayout.add(messageDiv, okButton);

        dialog.add(verticalLayout);

        // Open the dialog
        dialog.open();
    });
}
}