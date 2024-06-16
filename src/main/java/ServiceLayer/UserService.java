package ServiceLayer;

import java.util.logging.Logger;

import org.springframework.boot.autoconfigure.graphql.GraphQlProperties.Websocket;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.vaadin.flow.component.notification.Notification;

import java.util.List;
import java.util.logging.Level;

import Domain.Order;
import Domain.User;
import Domain.Alerts.GeneralAlert;
import Domain.Facades.ShoppingCartFacade;
import Domain.Facades.UserFacade;
import Dtos.PurchaseCartDetailsDto;
import Dtos.UserDto;
import Server.notifications.NotificationHandler;
import Server.notifications.WebSocketServer;
import Exceptions.UnauthorizedException;
import Domain.Alerts.*;

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

    public UserService() {
        _userFacade = UserFacade.getUserFacade();
        _tokenService = TokenService.getTokenService();
        _shoppingCartFacade = ShoppingCartFacade.getShoppingCartFacade();
    }

    // this function is responsible for logging in a user to the system by checking
    // the credentials and generating a token for the user
    public ResponseEntity<Response> logIn(String token, String userName, String password) {
        Response response = new Response();
        try {
            if (_tokenService.validateToken(token)) {
                if (userName == null || userName.isEmpty() || password == null || password.isEmpty()) {
                    throw new Exception("Username or password is empty.");
                }
                if (_userFacade.AreCredentialsCorrect(userName, password)) {
                    User user = _userFacade.getUserByUsername(userName);
                    System.out.println("user"+user.getUserName()+"password"+user.getPassword());
                    _shoppingCartFacade.addCartForUser(_tokenService.extractGuestId(token), user);
                    //update the new token for the user
                    String newToken = _tokenService.generateUserToken(userName);
                    response.setReturnValue(newToken);
             
                    WebSocketServer.getInstance().replaceGuestTokenToUserToken(token, newToken, userName);
                    // WebSocketServer.getInstance().sendMessage(userName, "You have been logged in");

                    Alert alert = new GeneralAlert("system Administrator", userName, "hello new logged in user! Welcome to the system!");
                    NotificationHandler.getInstance().sendMessage(userName, alert);
                    
                    logger.info("User " + userName + " Logged In Succesfully");
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                    throw new Exception("User Name Is Not Registered Or Password Is Incorrect");
                }
            } else {
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            response.setErrorMessage("LogIn Failed: " + e.getMessage());
            logger.log(Level.SEVERE, "LogIn Failed: " + e.getMessage(), e);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Response> logOut(String token) {
        Response response = new Response();
        try {
            if (_tokenService.validateToken(token)) {
                String userName = _tokenService.extractUsername(token);
                if (_userFacade.doesUserExist(userName)) {
                    String newToken = _tokenService.generateGuestToken();
                    logger.info("User successfully logged out: " + userName);
                    response.setReturnValue(newToken);
                    //close this session
                    WebSocketServer.getInstance().closeSession(userName);
                    Alert alert = new GeneralAlert("system Administrator", userName, "hello AGAIN LOGGED IN USER THIS MESSAGE HAVE BEEN WAITING FOR YOU!!!!!");
                    NotificationHandler.getInstance().sendMessage(userName, alert);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                    response.setErrorMessage("A user with the username given in the token does not exist.");
                    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                }
            } else {
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            response.setErrorMessage("Token is invalid: " + e.getMessage());
            logger.log(Level.SEVERE, "LogOut failed: " + e.getMessage(), e);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // this function is responsible for registering a new user to the system
    public ResponseEntity<Response> register(String token, UserDto userDto) {
        Response response = new Response();
        try {
            if (_tokenService.validateToken(token)) {
                _userFacade.register(userDto);
                logger.info("User registered: " + userDto.username);
                response.setReturnValue("Registration Succeeded");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            response.setErrorMessage("Registration failed: " + e.getMessage());
            logger.log(Level.SEVERE, "Registration failed: " + e.getMessage(), e);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // this function is responsible for purchasing the cart of a user or a guest
    // by checking the token and the user type and then calling the purchaseCart
    // function
    public Response purchaseCart(String token, PurchaseCartDetailsDto details) {
        Response response = new Response();
        try {
            if (_tokenService.validateToken(token)) {
                if (_tokenService.isGuest(token)) {
                    logger.log(Level.INFO, "Start purchasing cart for guest.");
                    _shoppingCartFacade.purchaseCartGuest(token, details);
                    response.setReturnValue("Guest bought card succeed");
                } else {
                    String userName = _tokenService.extractUsername(token);
                    logger.log(Level.INFO, "Start purchasing cart for user: " + userName);
                    _shoppingCartFacade.purchaseCartUser(userName, details);
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
     * @param token    The session token of the admin user.
     * @param username The ID of the user whose purchase history is to be retrieved.
     * @return A Response object containing the purchase history if successful, or
     *         an error message if not. () List<Order>
     * @throws Exception If the session token is invalid.
     */
    public Response getUserPurchaseHistory(String token, String username) {
        Response response = new Response();
        try {
            if (_tokenService.validateToken(token)) {
                if (!_tokenService.isUserAndLoggedIn(token)) {
                    response.setErrorMessage("User is not logged in");
                    logger.log(Level.SEVERE, "User is not logged in");
                    return response;
                }
                String adminUsername = _tokenService.extractUsername(token);
                boolean isAdmin = _userFacade.isAdmin(adminUsername);
                if (!isAdmin) {
                    response.setErrorMessage("User is not an admin");
                    logger.log(Level.SEVERE, "User is not an admin");
                    return response;
                }
                // get purchase history of a user
                response.setReturnValue(_userFacade.getPurchaseHistory(username));

                if (response.getErrorMessage() != null) {
                    response.setErrorMessage("Failed to get purchase history from user: " + username);
                    logger.log(Level.SEVERE, "Failed to get purchase history from user: " + username);
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

    // this function is responsible for getting the purchase history of a user.
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
        return response;
    }

    // this function is responsible for getting the purchase history of a shop.
    public Response addProductToShoppingCart(String token, int productID, int shopID) {
        Response response = new Response();
        try {
            if (_tokenService.validateToken(token)) {
                if (_tokenService.isGuest(token)) {
                    _shoppingCartFacade.addProductToGuestCart(_tokenService.extractGuestId(token), productID, shopID);
                } else if (_tokenService.isUserAndLoggedIn(token)) {
                    _shoppingCartFacade.addProductToUserCart(_tokenService.extractUsername(token), productID, shopID);
                } else {
                    throw new Exception("Token is incorrect");
                }
            } else {
                throw new Exception("Invalid session token.");
            }
        } catch (Exception e) {
            response.setErrorMessage("Failed to add product: " + e.getMessage());
            logger.log(Level.SEVERE, "Failed to add product: " + e.getMessage(), e);
        }
        return response;
    }

    // this function is responsible for removing a product from the shopping cart.
    public Response removeProductFromShoppingCart(String token, int productID, int shopID) {
        Response response = new Response();
        try {
            if (_tokenService.validateToken(token)) {
                if (_tokenService.isGuest(token)) {
                    _shoppingCartFacade.removeProductFromGuestCart(_tokenService.extractGuestId(token), productID,
                            shopID);
                } else if (_tokenService.isUserAndLoggedIn(token)) {
                    _shoppingCartFacade.removeProductFromUserCart(_tokenService.extractUsername(token), productID,
                            shopID);
                } else {
                    throw new Exception("Token is incorrect");
                }
            } else {
                throw new Exception("Invalid session token.");
            }
        } catch (Exception e) {
            response.setErrorMessage("Failed to remove product: " + e.getMessage());
            logger.log(Level.SEVERE, "Failed to remove product: " + e.getMessage(), e);
        }
        return response;
    }

    // this function is responsible for getting the shopping cart of a user: returns a list of products in the cart.
    public Response getShoppingCart(String token) {
        Response response = new Response();
        try {
            if (_tokenService.validateToken(token)) {
                if (_tokenService.isGuest(token)) {
                    response.setReturnValue(_shoppingCartFacade.getGuestCart(_tokenService.extractGuestId(token)));
                } else if (_tokenService.isUserAndLoggedIn(token)) {
                    response.setReturnValue(_shoppingCartFacade.getUserCart(_tokenService.extractUsername(token)));
                } else {
                    throw new Exception("Token is incorrect");
                }
            } else {
                throw new Exception("Invalid session token.");
            }
        } catch (Exception e) {
            response.setErrorMessage("Failed to get shopping cart: " + e.getMessage());
            logger.log(Level.SEVERE, "Failed to get shopping cart: " + e.getMessage(), e);
        }
        return response;
    }
    
    // this function is responsible for changing the email of a user.
    public Response changeEmail(String username, String email){
        Response response = new Response();
        try {
            _userFacade.changeEmail(username, email);
        } catch (Exception e) {
            response.setErrorMessage("Failed to change email for user: " + e.getMessage());
            logger.log(Level.SEVERE, "Failed to change email for user: " + e.getMessage(), e);
        }
        return response;
    }

    // this function is responsible for write a review for a product that bought by the user (only after the purchase, and logged in user)
    public Response writeReview(String token, int productID, int shopID, String review){
        Response response = new Response();
        try {
            if (_tokenService.validateToken(token)) {
                if (_tokenService.isUserAndLoggedIn(token)) {
                    String username = _tokenService.extractUsername(token);
                    List<Order> purchaseHistory = _userFacade.getPurchaseHistory(username);
                    _shoppingCartFacade.writeReview(username, purchaseHistory, productID, shopID, review);
                } else {
                    throw new Exception("Token is incorrect");
                }
            } else {
                throw new Exception("Invalid session token.");
            }
        } catch (Exception e) {
            response.setErrorMessage("Failed to write review: " + e.getMessage());
            logger.log(Level.SEVERE, "Failed to write review: " + e.getMessage(), e);
        }
        return response;
    }
}
