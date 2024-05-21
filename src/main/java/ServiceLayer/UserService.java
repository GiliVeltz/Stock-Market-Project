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

    public Response logIn(String userName, String password) {
        Response response = new Response();
        try {
            userController.logIn(userName, password);
            logger.info("User logged in: " + userName);
            response.set_returnValue(tokenService.generateToken(userName));
        } catch (Exception e) {
            response.set_errorMessage("LogIn failed: " + e.getMessage());
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
                response.set_returnValue("Logout Succeed");
            } else {
                response.set_errorMessage("A user with the username given in the token does not exist.");
            }
        } catch (Exception e) {
            response.set_errorMessage("Token is invalid");
            logger.log(Level.SEVERE, "LogOut failed: " + e.getMessage(), e);
        }
        return response;
    }

    public Response register(String userName, String password, String email) {
        Response response = new Response();
        try {
            userController.register(userName, password, email);
            logger.info("User registered: " + userName);
            response.set_returnValue("Registeration Succeed");
        } catch (Exception e) {
            response.set_errorMessage("Registeration failed: " + e.getMessage());
            logger.log(Level.SEVERE, "Registeration failed: " + e.getMessage(), e);
        }
        return response;
    }
}
