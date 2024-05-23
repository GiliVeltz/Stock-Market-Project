package ServiceLayer;

import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Level;

import Domain.Order;
import Domain.ShoppingBasket;
import Domain.UserController;
import Domain.ExternalServices.PaymentService.PaymentMethod;
import Domain.ExternalServices.SupplyService.SupplyMethod;

@Service
public class UserService {
    private UserController _userController;
    private TokenService _tokenService;
    private ShopService shopService;
    private static final Logger logger = Logger.getLogger(UserController.class.getName());

    public UserService() {
        _userController = new UserController();
        _tokenService = new TokenService();
    }

    public Response logIn(String token, String userName, String password) {
        Response response = new Response();
        try {
            if (_tokenService.validateToken(token)) {
                if (_userController.AreCredentialsCorrect(userName, password)) {
                    response.setReturnValue(_tokenService.generateUserToken(userName));
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
            if (_tokenService.validateToken(token)) {
                String userName = _tokenService.extractUsername(token);
                if (_userController.isUserNameExists(userName)) {
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

    public Response register(String token, String userName, String password, String email) {
        Response response = new Response();
        try {
            if (_tokenService.validateToken(token)) {
                if (!_userController.isUserNameExists(userName)) {
                    _userController.register(userName, password, email);
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

    public Response purchaseCart(List<Integer> busketsToBuy, PaymentMethod paymentMethod, SupplyMethod shippingMethod) {
        Response response = new Response();
        try {
            String username = _tokenService.getUsernameFromToken(token);
            if (_userController.isUserNameExists(username)) {
                _userController.getUserByUsername(username).purchaseCart(busketsToBuy, paymentMethod, shippingMethod);
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

    public Response isAdmin(String userId) {
        Response response = new Response();
        try {
            if (_userController.isAdmin(userId)) {
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
     * Retrieves the purchase history of a shop as an admin.
     *
     * @param token  The session token of the admin user.
     * @param shopId The ID of the shop whose purchase history is to be retrieved.
     * @return A Response object containing the purchase history if successful, or
     *         an error message if not. () List<ShoppingBasket>)
     * @throws Exception If the session token is invalid.
     */
    public Response getShopPurchaseHistoryAsAdmin(String token, Integer shopId) {
        Response response = new Response();
      
        try {
            if (_tokenService.validateToken(token)) {
                if (!_tokenService.isLoggedIn(token)) {
                    response.setErrorMessage("User is not logged in");
                    logger.log(Level.SEVERE, "User is not logged in");
                    return response;
                }
                String userId = _tokenService.extractUsername(token);
                Response isAdminResponse = isAdmin(userId);
                if (isAdminResponse.getErrorMessage() != null) {
                    response.setErrorMessage("User is not an admin");
                    logger.log(Level.SEVERE, "User is not an admin");
                    return response;
                }
                // check if the shop exist with
                Response isShopExistResponse = shopService.isShopIdExist(shopId);
                if (isShopExistResponse.getErrorMessage() != null) {
                    response.setErrorMessage("Shop not found");
                    logger.log(Level.SEVERE, "Shop not found");
                    return response;
                }
                // get purchase history of a shop
                response = shopService.getPurchaseHistory(shopId);
                if (response.getErrorMessage() != null) {
                    response.setErrorMessage("Failed to get purchase history from shop: " + shopId);
                    logger.log(Level.SEVERE, "Failed to get purchase history from shop: " + shopId);

                }

            } else {
                throw new Exception("Invalid session token.");
            }
        } catch (Exception e) {
            response.setErrorMessage("Failed to get purchase history: " + e.getMessage());
            logger.log(Level.SEVERE, "Failed to get purchase history: " + e.getMessage(), e);
        }
        // TODO: check with Spring how to return this response as a data object
        return response;
    }

    /**
     * Retrieves the purchase history of a shop as a shop owner.
     *
     * @param token  The session token of the a shop owner.
     * @param shopId The ID of the shop whose purchase history is to be retrieved.
     * @return A Response object containing the purchase history if successful, or
     *         an error message if not. () List<ShoppingBasket>)
     * @throws Exception If the session token is invalid.
     */
    public Response getShopPurchaseHistoryAsShopOwner(String token, Integer shopId) {
        Response response = new Response();
        try {
            
            if (_tokenService.validateToken(token)) {
                if (!_tokenService.isLoggedIn(token)) {
                    response.setErrorMessage("User is not logged in");
                    logger.log(Level.SEVERE, "User is not logged in");
                    return response;
                }
                // check if the shop exist
                Response isShopExistResponse = shopService.isShopIdExist(shopId);
                if (isShopExistResponse.getErrorMessage() != null) {
                    response.setErrorMessage("Shop not found");
                    logger.log(Level.SEVERE, "Shop not found");
                    return response;
                }
                String userId = _tokenService.extractUsername(token);
                // check if the user is the owner(or founder) of the shop
                Response isShopOwnerResponse = shopService.isShopOwner(shopId, userId);
                if (isShopOwnerResponse.getErrorMessage() != null) {
                    response.setErrorMessage("User is not the owner of the shop");
                    logger.log(Level.SEVERE, "User is not the owner of the shop");
                    return response;
                }
                // get purchase history of a shop
                response = shopService.getPurchaseHistory(shopId);
                if (response.getErrorMessage() != null) {
                    response.setErrorMessage("Failed to get purchase history from shop: " + shopId);
                    logger.log(Level.SEVERE, "Failed to get purchase history from shop: " + shopId);

                }

            } else {
                throw new Exception("Invalid session token.");
            }
        } catch (Exception e) {
            response.setErrorMessage("Failed to get purchase history: " + e.getMessage());
            logger.log(Level.SEVERE, "Failed to get purchase history: " + e.getMessage(), e);
        }

        // TODO: check with Spring how to return this response as a data object
        return response;
    }

    public Response getPersonalPurchaseHistory(String token) {
        Response response = new Response();
        try {
            if (_tokenService.validateToken(token)) {
                if (!_tokenService.isLoggedIn(token)) {
                    response.setErrorMessage("User is not logged in");
                    logger.log(Level.SEVERE, "User is not logged in");
                    return response;
                }
                String username = _tokenService.extractUsername(token);
                logger.info("Purchase history request for user: " + username);
                List<Order> purchaseHistory = _userController.getPurchaseHistory(username);
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

}
