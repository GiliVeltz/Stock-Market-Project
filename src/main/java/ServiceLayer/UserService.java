package ServiceLayer;

import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Level;

import Domain.Order;

import Domain.ShoppingCartFacade;
import Domain.UserFacade;

@Service
public class UserService {
    private UserFacade _userFacade;
    private TokenService _tokenService;

    private ShoppingCartFacade _shoppingCartFacade;
    private static final Logger logger = Logger.getLogger(UserService.class.getName());

    public UserService(UserFacade userFacade, TokenService tokenService,
            ShoppingCartFacade shoppingCartFacade) {
                _userFacade = userFacade;
        _tokenService = tokenService;
        _shoppingCartFacade = shoppingCartFacade;
    }

    // this function is responsible for logging in a user to the system by checking the credentials and generating a token for the user
    public Response logIn(String token, String userName, String password) {
        Response response = new Response();
        try {
            if (_tokenService.validateToken(token)) {
                if(userName == null || userName.isEmpty() || password == null || password.isEmpty()){
                    throw new Exception("Username or password is empty.");
                }
                if (_userFacade.AreCredentialsCorrect(userName, password)) {
                    response.setReturnValue(_tokenService.generateUserToken(userName));
                    logger.info("User " + userName + " Logged In Succesfully");
                } else {
                    throw new Exception("User Name Is Not Registered Or Password Is Incorrect");
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

    // this function is responsible for logging out a user from the system by returning a new token for a guest in the system
    public Response logOut(String token) {
        Response response = new Response();
        try {
            if (_tokenService.validateToken(token)) {
                String userName = _tokenService.extractUsername(token);
                if (_userFacade.isUserNameExists(userName)) {
                    String newToken = _tokenService.generateGuestToken();
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

    // this function is responsible for registering a new user to the system
    public Response register(String token, String userName, String password, String email) {
        Response response = new Response();
        try {
            if (_tokenService.validateToken(token)) {
                if(userName == null || userName.isEmpty()){
                    throw new Exception("UserName is empty.");
                }
                if (!_userFacade.isUserNameExists(userName)) {
                    _userFacade.register(userName, password, email);
                    _shoppingCartFacade.addCartForUser(token, userName);
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

    // this function is responsible for purchasing the cart of a user or a guest
    // by checking the token and the user type and then calling the purchaseCart function
    public Response purchaseCart(String token, List<Integer> busketsToBuy, String cardNumber, String address) {
        Response response = new Response();
        try {
            if (_tokenService.validateToken(token)) {
                if (_tokenService.isGuest(token)) {
                    logger.log(Level.INFO, "Start purchasing cart for guest.");
                    _shoppingCartFacade.purchaseCartGuest(token, cardNumber, address);
                    response.setReturnValue("Guest bought card succeed");
                } else {
                    String userName = _tokenService.extractUsername(token);
                    logger.log(Level.INFO, "Start purchasing cart for user: " + userName);
                    _shoppingCartFacade.purchaseCartUser(userName, busketsToBuy, cardNumber, address);
                    response.setReturnValue("User bought card succeed");
                }
            } else {
                throw new Exception("Invalid session token.");
            }
        } catch (Exception e) {
            response.setErrorMessage("Cart bought has been failed: " + e.getMessage());
            logger.log(Level.SEVERE, "Cart bought has been failed: " + e.getMessage(), e);
        }
        return response;
    }

    // this function is responsible for checking if a user is a system admin
    public Response isSystemAdmin(String userId) {
        Response response = new Response();
        try {
            if (_userFacade.isAdmin(userId)) {
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

    /**
     * Retrieves the purchase history of a specific user as an admin.
     *
     * @param token  The session token of the admin user.
     * @param userId The ID of the user whose purchase history is to be retrieved.
     * @return A Response object containing the purchase history if successful, or
     *         an error message if not. () List<Order>
     * @throws Exception If the session token is invalid.
     */
    public Response getUserPurchaseHistoryAsAdmin(String token, String userId) {
        Response response = new Response();
        try {
            if (_tokenService.validateToken(token)) {
                if (!_tokenService.isUserAndLoggedIn(token)) {
                    response.setErrorMessage("User is not logged in");
                    logger.log(Level.SEVERE, "User is not logged in");
                    return response;
                }
                String adminId = _tokenService.extractUsername(token);
                Response isAdminResponse = isSystemAdmin(adminId);
                if (isAdminResponse.getErrorMessage() != null) {
                    response.setErrorMessage("User is not an admin");
                    logger.log(Level.SEVERE, "User is not an admin");
                    return response;
                }
                // get purchase history of a user
                response.setReturnValue(_userFacade.getPurchaseHistory(userId));
                if (response.getErrorMessage() != null) {
                    response.setErrorMessage("Failed to get purchase history from user: " + userId);
                    logger.log(Level.SEVERE, "Failed to get purchase history from user: " + userId);

                }

            } else {
                throw new Exception("Invalid session token.");
            }
        } catch (Exception e) {
            response.setErrorMessage("Failed to get purchase history: " + e.getMessage());
            logger.log(Level.SEVERE, "Failed to get purchase history: " + e.getMessage(), e);
        }
        return response;
    }

    public Response getPersonalPurchaseHistory(String token) {
        Response response = new Response();
        try {
            if (_tokenService.validateToken(token)) {
                if (!_tokenService.isUserAndLoggedIn(token)) {
                    response.setErrorMessage("User is not logged in");
                    logger.log(Level.SEVERE, "User is not logged in");
                    return response;
                }
                String username = _tokenService.extractUsername(token);
                logger.info("Purchase history request for user: " + username);
                List<Order> purchaseHistory = _userFacade.getPurchaseHistory(username);
                logger.info("Purchase history retrieved for user: " + username);
                response.setReturnValue(purchaseHistory);

            } else {
                throw new Exception("Invalid session token.");
            }
        } catch (Exception e) {
            response.setErrorMessage("Failed to retrieve purchase history: " + e.getMessage());
            logger.log(Level.SEVERE, "Failed to retrieve purchase history: " + e.getMessage(), e);
        }
        // TODO: check with Spring how to return this response as a data object
        return response;
    }

    public UserFacade getUserFacade() {
        return _userFacade;
    }
}
