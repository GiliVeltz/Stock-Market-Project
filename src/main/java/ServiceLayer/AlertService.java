// package ServiceLayer;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;

// import Domain.Alerts.Alert;
// import Server.NotificationController;

// @Service
// public class AlertService {
    
//     private final NotificationController notificationController;

//     @Autowired
//     public AlertService(NotificationController notificationController) {
//         this.notificationController = notificationController;
//     }

//     //TODO: WebSocket - Add the logic to process the alert.
//     public void processAlert(Alert alert) {
//         // Add business logic here. For example, we might want to save the alert to a database.

//         // Then after processing the alert, send a notification.(taken from the NotificationController class)
//         notificationController.sendNotification(alert);
//     }
    
// }
