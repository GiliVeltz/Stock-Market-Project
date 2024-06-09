// package Server;

// import org.springframework.messaging.handler.annotation.MessageMapping;
// import org.springframework.messaging.simp.SimpMessagingTemplate;
// import org.springframework.stereotype.Controller;

// import Domain.Alerts.Alert;
// import javax.validation.Valid;

// //TODO: change the prints to logging
// @Controller
// public class NotificationController {

//     private final SimpMessagingTemplate template;

//     public NotificationController(SimpMessagingTemplate template) {
//         this.template = template;
//     }

//     //should be a sending function to owner of shop
//     @MessageMapping("/notify")
//     public void sendNotification(@Valid Alert alert) {
//         try {
//             if (alert != null) {

//                 template.convertAndSend("/topic/shop/" + alert.getShopId(), alert);

//             } else {
//                 System.out.println("Invalid alert received: " + alert);
//             }
//         } catch (Exception e) {
//             System.out.println("Failed to send notification: " + e.getMessage());
//         }
//     }

//     @MessageMapping("/notifyUser")
//     public void sendUserNotification(@Valid Alert alert) {
//         try {
//             if (alert != null && alert.getUserId() != null) {

//                 template.convertAndSend("/topic/user/" + alert.getUserId(), alert);

//             } else {
//                 System.out.println("Invalid alert received: " + alert);
//             }
//         } catch (Exception e) {
//             System.out.println("Failed to send notification: " + e.getMessage());
//         }
//     }

// }
