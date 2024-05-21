package ServiceLayer;

import java.util.logging.Logger;
import java.util.logging.Level;
import Domain.ExternalServices.ExternalServiceHandler;

// Class that represents the system service and enables users (probably admins) to control the system.

public class SystemService {
    private UserService userService;
    private ExternalServiceHandler externalServiceHandler;
    private boolean isOpen = false;
    private static final Logger logger = Logger.getLogger(SystemService.class.getName());

    public SystemService(UserService userService, ExternalServiceHandler externalServiceHandler) {
        this.userService = userService;
        this.externalServiceHandler = externalServiceHandler;
    }

    /**
     * Opens the system.
     * 
     * @param userId   the user ID
     * @param password the user password
     * @return a response indicating the success or failure of opening the system
     */
    public Response openSystem(String userId) {
        Response response = new Response();
        try {
            // Check if the user is already logged in.
            Response loggedInResponse = userService.isLoggedIn(userId);
            if (loggedInResponse.getErrorMessage() != null) {
                response.setErrorMessage("User is not logged in");
                logger.log(Level.SEVERE, "User is not logged in");
                return response;
            }
            // Check if the user is an admin
            Response isAdResponse = userService.isAdmin(userId);
            if (isAdResponse.getErrorMessage() != null) {
                response.setErrorMessage("User is not an admin");
                logger.log(Level.SEVERE, "User is not an admin");
                return response;
            }
    
            // Check if the system is already open
            if (isSystemOpen()) {
                response.setErrorMessage("System is already open");
                logger.log(Level.SEVERE, "System is already open");
                return response;
            }

            // Connect to external services
            if (!externalServiceHandler.connectToServices()) {
                response.setErrorMessage("Failed to connect to external services");
                logger.log(Level.SEVERE, "Failed to connect to external services");
                return response;
            }

            // Open the system
            setSystemOpen(true);
            logger.info("System opened by admin: " + userId);
            response.setReturnValue("System Opened Successfully");
        } catch (Exception e) {
            response.setErrorMessage("Failed to open system: " + e.getMessage());
            logger.log(Level.SEVERE, "Failed to open system: " + e.getMessage(), e);
        }
        return response;
    }

    // Set system to open
    private void setSystemOpen(boolean isOpen) {
        this.isOpen = isOpen;
    }

    // Check if system is open
    public boolean isSystemOpen() {
        return isOpen;
    }
}
