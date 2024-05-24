package ServiceLayer;

import java.util.logging.Logger;
// import org.apache.catalina.servlets.DefaultServlet.SortManager.Order;
import org.springframework.stereotype.Service;

import java.util.List;
//import java.lang.module.ModuleDescriptor.Opens;
import java.util.logging.Level;

import Domain.Product;
import Domain.ShopFacade;
import Domain.ShopOrder;

@Service
public class ShopService {
    private ShopFacade _shopFacade;
    private TokenService _tokenService;
    private UserService _userService;
    private static final Logger logger = Logger.getLogger(ShopFacade.class.getName());

    public ShopService(ShopFacade shopFacade, TokenService tokenService, UserService userService){
       // _shopFacade = ShopFacade.getShopFacade();
        _shopFacade = shopFacade;
        _tokenService = tokenService;
        _userService = userService;
    }

    /**
     * Opens a new shop with the specified shop ID and user name.
     * 
     * @param shopId   The ID of the new shop to be opened.
     * @param userName The name of the user opening the shop (founder).
     * @return A response indicating the success or failure of the operation.
     */
    public Response openNewShop(String token, Integer shopId, String userName) {
        Response response = new Response();
        try {
            if (_tokenService.validateToken(token)) {
                if (_tokenService.isUserAndLoggedIn(userName)) {
                    _shopFacade.openNewShop(shopId, userName);
                    logger.info(String.format("New shop created by: %s with Shop ID: %d", userName, shopId));
                } else {
                    throw new Exception("Only register users can open shop.");
                }
            } else {
                throw new Exception("Invalid session token.");
            }

        } catch (Exception e) {
            response.setErrorMessage(
                    String.format("Failed to create shopID %d by user %s. Error: ", shopId, userName, e.getMessage()));
            logger.log(Level.SEVERE, e.getMessage(), e);
        }

        return response;
    }

    /**
     * Close a shop with the specified shop ID and user name.
     * 
     * @param shopId   The ID of the existing shop to be closed.
     * @param userName The name of the user closing the shop (founder).
     * @return A response indicating the success or failure of the operation.
     */
    public Response closeShop(String token, Integer shopId, String userName) {
        Response response = new Response();
        try {
            if (_tokenService.validateToken(token)) {
                if (_tokenService.isUserAndLoggedIn(userName)) {
                    _shopFacade.closeShop(shopId, userName);
                    logger.info(String.format("Shop closed by: %s with Shop ID: %d", userName, shopId));
                } else {
                    throw new Exception("User is not register.");
                }
            } else {
                throw new Exception("Invalid session token.");
            }

        } catch (Exception e) {
            response.setErrorMessage(
                    String.format("Failed to create shopID %d by user %s. Error: ", shopId, userName, e.getMessage()));
            logger.log(Level.SEVERE, e.getMessage(), e);
        }

        return response;
    }

    /**
     * Adds a product to the specified shop.
     * 
     * @param shopId   The ID of the shop to which the product will be added.
     * @param userName The name of the user adding the product.
     * @param product  The product to be added to the shop.
     * @return A response indicating the success or failure of the operation.
     */
    public Response addProductToShop(String token, Integer shopId, String userName, Product product) {
        Response response = new Response();
        try {
            if (_tokenService.validateToken(token)) {
                if (_tokenService.isUserAndLoggedIn(userName)) {
                    _shopFacade.addProductToShop(shopId, product, userName);
                    logger.info(String.format("New product %s :: %d added by: %s to Shop ID: %d",
                            product.getProductName(), product.getProductId(), userName, shopId));
                } else {
                    throw new Exception(String.format("User %s does not have permissions", userName));
                }
            } else {
                throw new Exception("Invalid session token.");
            }

        } catch (Exception e) {
            response.setErrorMessage(String.format("Failed to add product %s :: %d to shopID %d by user %s. Error: ",
                    product.getProductName(),
                    product.getProductId(), shopId, userName, e.getMessage()));
            logger.log(Level.SEVERE, e.getMessage(), e);
        }

        return response;
    }

    // function to get a purchase from shopFacade by shop ID
    public Response getPurchaseHistory(Integer shopId) {
        Response response = new Response();
        try {
            List<ShopOrder> purchasHistory = _shopFacade.getPurchaseHistory(shopId);
            response.setReturnValue(purchasHistory);
            logger.info(String.format("Purchase history retrieved for Shop ID: %d", shopId));

        } catch (Exception e) {
            response.setErrorMessage(String.format("Failed to retrieve purchase history for shopID %d. Error: ", shopId,
                    e.getMessage()));
            logger.log(Level.SEVERE, e.getMessage(), e);
        }

        return response;
    }

    // function to check if shop exists
    public Response isShopIdExist(Integer shopId) {
        Response response = new Response();
        try {
            Boolean isExist = _shopFacade.isShopIdExist(shopId);
            response.setReturnValue(isExist);
            logger.info(String.format("Shop ID: %d exists: %b", shopId, isExist));

        } catch (Exception e) {
            response.setErrorMessage(
                    String.format("Failed to check if shopID %d exists. Error: ", shopId, e.getMessage()));
            logger.log(Level.SEVERE, e.getMessage(), e);
        }

        return response;
    }

    public Response isShopOwner(Integer shopId, String userId) {
        Response response = new Response();
        try {
            Boolean isOwner = _shopFacade.isShopOwner(shopId, userId);
            response.setReturnValue(isOwner);
            logger.info(String.format("User %s is owner of Shop ID: %d: %b", userId, shopId, isOwner));

        } catch (Exception e) {
            response.setErrorMessage(String.format("Failed to check if user %s is owner of shopID %d. Error: ", userId,
                    shopId, e.getMessage()));
            logger.log(Level.SEVERE, e.getMessage(), e);
        }

        return response;
    }



  /**
     * Retrieves the purchase history of a shop as an admin or a shop owner with checking with token that the token belongs to the this shop owner or he is an admin
     *
     * @param token  The session token of the admin user.
     * @param shopId The ID of the shop whose purchase history is to be retrieved.
     * @return A Response object containing the purchase history if successful, or
     *         an error message if not. () List<shopOrder>)
     * @throws Exception If the session token is invalid.
     */
    public Response getShopPurchaseHistory(String token, Integer shopId) {
            Response response = new Response();
    
            try {
                if (_tokenService.validateToken(token)) {
                    if (!_tokenService.isUserAndLoggedIn(token)) {
                        response.setErrorMessage("User is not logged in");
                        logger.log(Level.SEVERE, "User is not logged in");
                        return response;
                    }
                     // check if the shop exist with
                     Response isShopExistResponse = isShopIdExist(shopId);
                     if (isShopExistResponse.getErrorMessage() != null) {
                         response.setErrorMessage("Shop not found");
                         logger.log(Level.SEVERE, "Shop not found");
                         return response;
                     }

                    String userId = _tokenService.extractUsername(token);
                    Response isAdminResponse = _userService.isSystemAdmin(userId);
                    if (_shopFacade.isShopOwner(shopId, userId) ||isAdminResponse.getErrorMessage() != null){ 
                        response.setErrorMessage("User has no permission to access the shop purchase history");
                        logger.log(Level.SEVERE, "User has no permission to access the shop purchase history");
                        return response;
                    }
                    else{
                        // get purchase history of a shop
                        response = getPurchaseHistory(shopId);
                        if (response.getErrorMessage() != null) {
                            response.setErrorMessage("Failed to get purchase history from shop: " + shopId);
                            logger.log(Level.SEVERE, "Failed to get purchase history from shop: " + shopId);
                            return response;
                        }

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
    }