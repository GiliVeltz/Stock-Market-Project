package ServiceLayer;

import java.util.logging.Logger;
import java.util.logging.Level;
import Domain.UserController;

public class UserService {
    private UserController userController;
    private TokenService tokenService;
    private static final Logger logger = Logger.getLogger(UserController.class.getName());

    public UserService(){
        userController = new UserController();
        tokenService = new TokenService();
    }

    public Response logIn(String userName, String password) throws Exception {
        Response response = new Response();
        try {
            userController.logIn(userName, password);
            logger.info("User logged in: " + userName);
            response.setReturnValue(tokenService.generateToken(userName));
        } catch (Exception e) {
            response.setErrorMessage("LogIn failed: " + e.getMessage());
            logger.log(Level.SEVERE, "LogIn failed: " + e.getMessage(), e);
        }
        return response;
    }

    public Response logOut(String token, String userName, String password){
        Response response = new Response();
        try {
            if (tokenService.validateToken(token)){
                userController.logOut(userName);
                logger.info("User logged out: " + userName);
                response.setReturnValue("Login Succeed");
            } else {
                response.setErrorMessage("Token is incorrect");
            }
        } catch (Exception e) {
            response.setErrorMessage("LogOut failed: " + e.getMessage());
            logger.log(Level.SEVERE, "LogOut failed: " + e.getMessage(), e);
        }
        return response;
    }

    public Response register(String userName, String password, String email){
        Response response = new Response();
        try {
            userController.register(userName, password, email);
            logger.info("User registered: " + userName);
            response.setReturnValue("Registeration Succeed");
        } catch (Exception e) {
            response.setErrorMessage("Registeration failed: " + e.getMessage());
            logger.log(Level.SEVERE, "Registeration failed: " + e.getMessage(), e);
        }
        return response;
    }

    // function that check if the user is an admin using try catch and logging without checking for token
    public Response isAdmin(String userName, String password){
        Response response = new Response();
        try {
            if (userController.isAdmin(userName)){
                logger.info("User is an admin: " + userName);
                response.setReturnValue("User is an admin");
            } else {
                logger.info("User is not an admin: " + userName);
                response.setReturnValue("User is not an admin");
            }
        } catch (Exception e) {
            response.setErrorMessage("Failed to check if user is an admin: " + e.getMessage());
            logger.log(Level.SEVERE, "Failed to check if user is an admin: " + e.getMessage(), e);
        }
        return response;
    }



}
