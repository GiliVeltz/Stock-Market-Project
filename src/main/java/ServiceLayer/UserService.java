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

    public Response logOut(String token, String userName, String password) {
        Response response = new Response();
        try {
            if (tokenService.validateToken(token)) {
                userController.logOut(userName);
                logger.info("User logged out: " + userName);
                response.set_returnValue("Login Succeed");
            } else {
                response.set_errorMessage("Token is incorrect");
            }
        } catch (Exception e) {
            response.set_errorMessage("LogOut failed: " + e.getMessage());
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
