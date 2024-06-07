package ServiceLayer;
import Domain.Alerts.Alert;

// class deals with alert notifications
public class AlertService {
    private static AlertService instance;
    private static UserService userService; // Reference to UserService

    private AlertService(UserService userService) {
        AlertService.userService = userService;
    }

    public static void initialize(UserService userService) {
        if (AlertService.userService == null) {
            AlertService.userService = userService;
        } else {
            throw new IllegalStateException("AlertService is already initialized.");
        }
    }

    public static AlertService getInstance() {
        if (instance == null) {
            synchronized (AlertService.class) {
                if (instance == null) {
                    if (userService == null) {
                        throw new IllegalStateException("AlertService is not initialized with UserService.");
                    }
                    instance = new AlertService(userService);
                }
            }
        }
        return instance;
    }

    public void sendAlert(String userName ,Alert alert) {
        userService.sendAlert(userName, alert);
    }

}
