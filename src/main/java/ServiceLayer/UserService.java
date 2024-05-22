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

    public Response logIn(String token, String userName, String password) {
        Response response = new Response();
        try {
            if (tokenService.validateToken(token)) {
                if (userController.AreCredentialsCorrect(userName, password)) {
                    response.setReturnValue(tokenService.generateUserToken(userName));
                    logger.info("User " + userName + " Logged In Succesfully");
                } else {
                    throw new Exception("User Name Is Already Exists");
                }
            } else {
                throw new Exception("Invalid session token.");
            }
        } catch (Exception e) {
            response.setErrorMessage("LogIn Failed: " + e.getMessage());
            logger.log(Level.SEVERE, "LogIn Failed: " + e.getMessage(), e);
        }
        return response;
    }

    public Response logOut(String token) {
        Response response = new Response();
        try {
            if (tokenService.validateToken(token)) {
                String userName = tokenService.extractUsername(token);
                if (userController.isUserNameExists(userName)) {
                    String newToken = tokenService.generateGuestToken();
                    logger.info("User successfuly logged out: " + userName);
                    response.setReturnValue(newToken);
                } else {
                    response.setErrorMessage("A user with the username given in the token does not exist.");
                }
            } else {
                throw new Exception("Invalid session token.");
            }
        } catch (Exception e) {
            response.setErrorMessage("Token is invalid");
            logger.log(Level.SEVERE, "LogOut failed: " + e.getMessage(), e);
        }
        return response;
    }

    public Response register(String token, String userName, String password, String email) {
        Response response = new Response();
        try {
            if (tokenService.validateToken(token)) {
                if (!userController.isUserNameExists(userName)) {
                    userController.register(userName, password, email);
                    logger.info("User registered: " + userName);
                    response.setReturnValue("Registeration Succeed");
                } else {
                    throw new Exception("User Name Is Already Exists");
                }
            } else {
                throw new Exception("Invalid session token.");
            }
        } catch (Exception e) {
            response.setErrorMessage("Registeration failed: " + e.getMessage());
            logger.log(Level.SEVERE, "Registeration failed: " + e.getMessage(), e);
        }
        return response;
    }

    // function that check if the user is an admin using try catch and logging
    // without checking for token
    public Response isAdmin(String userId) {
        Response response = new Response();
        try {
            if (userController.isAdmin(userId)) {
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

    // check if user is logged in using try catch and logging
    // TODO: remove that function
    // public Response isLoggedIn(String userId){
    // Response response = new Response();
    // try {
    // if (userController.isLoggedIn(userId)){
    // logger.info("User is logged in: " + userId);
    // response.setReturnValue("User is logged in");
    // } else {
    // logger.info("User is not logged in: " + userId);
    // response.setReturnValue("User is not logged in");
    // }
    // } catch (Exception e) {
    // response.setErrorMessage("Failed to check if user is logged in: " +
    // e.getMessage());
    // logger.log(Level.SEVERE, "Failed to check if user is logged in: " +
    // e.getMessage(), e);
    // }
    // return response;
    // }

}
