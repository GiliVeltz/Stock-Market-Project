package ServiceLayer;

import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Level;
import Domain.UserController;
import Domain.ExternalServices.PaymentService.PaymentMethod;
import Domain.ExternalServices.SupplyService.SupplyMethod;

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
            response.setReturnValue(tokenService.generateToken(userName));
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

    public Response register(String userName, String password, String email) {
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

    public Response purchaseCart(String token, List<Integer> busketsToBuy, PaymentMethod paymentMethod, SupplyMethod shippingMethod) {
        Response response = new Response();
        try {
            String username = tokenService.getUsernameFromToken(token);
            if (userController.isUserNameExists(username)) {
                userController.getUserByUsername(username).purchaseCart(busketsToBuy, paymentMethod, shippingMethod);
                logger.info("User purchase cart successfully: " + username);
                response.setReturnValue("Purchase cart Succeed");
            } else {
                response.setErrorMessage("A user with the username given in the token does not exist.");
            }
        } catch (Exception e) {
            response.setErrorMessage("Token is invalid");
            logger.log(Level.SEVERE, "Purchase cart failed: " + e.getMessage(), e);
        }
        return response;
    }
}
