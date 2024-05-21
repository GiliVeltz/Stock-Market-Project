package ServiceLayer;

import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import java.util.logging.Level;
import Domain.UserController;

@Service
public class UserService {
    private UserController userController;
    private TokenService tokenService;
    private static final Logger logger = Logger.getLogger(UserController.class.getName());

    public UserService() {
        userController = new UserController();
        tokenService = new TokenService();
    }

    public Response logIn(String userId, String password) {
        Response response = new Response();
        try {
            userController.logIn(userId, password);
            logger.info("User logged in: " + userId);
            response.setReturnValue(tokenService.generateToken(userId));
        } catch (Exception e) {
            response.setErrorMessage("LogIn failed: " + e.getMessage());
            logger.log(Level.SEVERE, "LogIn failed: " + e.getMessage(), e);
        }
        return response;
    }

    public Response logOut(String token) {
        Response response = new Response();
        try {
            String username = tokenService.getUsernameFromToken(token);
            if (userController.isUserNameExists(username)) {
                userController.logOut(username);
                logger.info("User logged out: " + username);
                response.setReturnValue("Logout Succeed");
            } else {
                response.setErrorMessage("A user with the username given in the token does not exist.");
            }
        } catch (Exception e) {
            response.setErrorMessage("Token is invalid");
            logger.log(Level.SEVERE, "LogOut failed: " + e.getMessage(), e);
        }
        return response;
    }

    public Response register(String userId, String password, String email) {
        Response response = new Response();
        try {
            userController.register(userId, password, email);
            logger.info("User registered: " + userId);
            response.setReturnValue("Registeration Succeed");
        } catch (Exception e) {
            response.setErrorMessage("Registeration failed: " + e.getMessage());
            logger.log(Level.SEVERE, "Registeration failed: " + e.getMessage(), e);
        }
        return response;
    }

    // function that check if the user is an admin using try catch and logging without checking for token
    public Response isAdmin(String userId){
        Response response = new Response();
        try {
            if (userController.isAdmin(userId)){
                logger.info("User is an admin: " + userId);
                response.setReturnValue("User is an admin");
            } else {
                logger.info("User is not an admin: " + userId);
                response.setReturnValue("User is not an admin");
            }
        } catch (Exception e) {
            response.setErrorMessage("Failed to check if user is an admin: " + e.getMessage());
            logger.log(Level.SEVERE, "Failed to check if user is an admin: " + e.getMessage(), e);
        }
        return response;
    }

    //check if user is logged in using try catch and logging
    public Response isLoggedIn(String userId){
        Response response = new Response();
        try {
            if (userController.isLoggedIn(userId)){
                logger.info("User is logged in: " + userId);
                response.setReturnValue("User is logged in");
            } else {
                logger.info("User is not logged in: " + userId);
                response.setReturnValue("User is not logged in");
            }
        } catch (Exception e) {
            response.setErrorMessage("Failed to check if user is logged in: " + e.getMessage());
            logger.log(Level.SEVERE, "Failed to check if user is logged in: " + e.getMessage(), e);
        }
        return response;
    }



}
