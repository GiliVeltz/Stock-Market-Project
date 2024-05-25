package ServiceLayer;

import java.util.logging.Logger;
// import org.apache.catalina.servlets.DefaultServlet.SortManager.Order;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import Domain.Product;
import Domain.ShopFacade;
import Domain.ShopOrder;

import Domain.ShopFacade.Category;

import Exceptions.StockMarketException;

@Service
public class ShopService {
    private ShopFacade _shopFacade;
    private TokenService _tokenService;
    private UserService _userService;
    private static final Logger logger = Logger.getLogger(ShopFacade.class.getName());

    public ShopService(ShopFacade shopFacade, TokenService tokenService, UserService userService) {
        // _shopFacade = ShopFacade.getShopFacade();
        _shopFacade = shopFacade;
        _tokenService = tokenService;
        _userService = userService;
    }

    /**
     * Opens a new shop with the specified shop ID and user name.
     * 
     * @param shopId      The ID of the new shop to be opened.
     * @param userName    The name of the user opening the shop (founder).
     * @param bankDetails The bank details of the shop.
     * @return A response indicating the success or failure of the operation.
     */
    public Response openNewShop(String token, Integer shopId, String userName, String bankDetails, String shopAddress) {
        Response response = new Response();
        try {
            if (_tokenService.validateToken(token)) {
                if (_tokenService.isUserAndLoggedIn(userName)) {
                    _shopFacade.openNewShop(shopId, userName, bankDetails, shopAddress);
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


    /**
    * searches products by their name.
    * 
    * @param shopId    The ID of the shop to search in OR null to search in all shops.
    * @param productName  he name of the product.
    * @return          A response indicating the success of the operation and some products' deatails or failure.
    */
    public Response searchProductInShopByName(Integer shopId, String productName)
    //TODO: handle return products as a response and log them
    {
        Response response = new Response();
        try
        {
            Map<Integer, List<Product>> products = _shopFacade.getProductInShopByName(shopId, productName);
            if (products != null)
            {
                //TODO: handle return products as a response and log them 
                response.setReturnValue("Products found in shop");
                logger.info(String.format("Products found in Shop ID: %d", shopId));
            }
            else
            {
                response.setReturnValue("Product not found in shop");
                logger.info(String.format("Products not found in Shop ID: %d", shopId));
            }
        }
        catch (Exception e)
        {
            response.setErrorMessage(String.format("Failed to search products in shopID %d. Error: ", shopId, e.getMessage()));
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return response;        
    }

/**
    * searches products by their Category.
    * 
    * @param shopId    The ID of the shop to search in OR null to search in all shops.
    * @param productCategory  The category of the product.
    * @return          A response indicating the success of the operation and some products' deatails or failure.
    */
    public Response searchProductInShopByCategory(Integer shopId, Category productCategory)
    //TODO: handle return products as a response and log them
    {
        Response response = new Response();
        try
        {
            Map<Integer, List<Product>> products = _shopFacade.getProductInShopByCategory(shopId, productCategory);
            if (products != null)
            {
                //TODO: handle return products as a response and log them 
                response.setReturnValue("Products found in shop");
                logger.info(String.format("Products found in Shop ID: %d", shopId));
            }
            else
            {
                response.setReturnValue("Products not found in shop");
                logger.info(String.format("Products not found in Shop ID: %d", shopId));
            }
        }
        catch (Exception e)
        {
            response.setErrorMessage(String.format("Failed to search products in shopID %d. Error: ", shopId, e.getMessage()));
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return response;        
    }

    /**
    * searches products by keyWords.
    * 
    * @param shopId    The ID of the shop to search in OR null to search in all shops.
    * @param keywords  The list of keywords.
    * @return          A response indicating the success of the operation and some products' deatails or failure.
    */
    public Response searchProductsInShopByKeywords(Integer shopId, List<String> keywords)
    //TODO: handle return products as a response and log them
    {
        Response response = new Response();
        try
        {
            Map<Integer, List<Product>> products = _shopFacade.getProductsInShopByKeywords(shopId, keywords);
            if (products != null)
            {
                //TODO: handle return products as a response and log them 
                response.setReturnValue("Products found in shop");
                logger.info(String.format("Products found in Shop ID: %d", shopId));
            }
            else
            {
                response.setReturnValue("Product not found in shop");
                logger.info(String.format("Products not found in Shop ID: %d", shopId));
            }
        }
        catch (Exception e)
        {
            response.setErrorMessage(String.format("Failed to search products in shopID %d. Error: ", shopId, e.getMessage()));
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return response;        
    }



    /**
    * Temporary function - replace filter
    * searches products by pricing range.
    * 
    * @param shopId    The ID of the shop to search in OR null to search in all shops.
    * @param minPrice  The minimum price of the product.
    * @param maxPrice  The maximum price of the product.
    * @return          A response indicating the success of the operation and some products' deatails or failure.
    */
    public Response searchProductsInShopByPriceRange(Integer shopId, Double minPrice, Double maxPrice)
    //TODO: handle return products as a response and log them
    {
        Response response = new Response();
        try
        {
            Map<Integer, List<Product>> products = _shopFacade.getProductsInShopByPriceRange(shopId, minPrice, maxPrice);
            if (products != null)
            {
                //TODO: handle return products as a response and log them 
                response.setReturnValue("Products found in shop");
                logger.info(String.format("Products found in Shop ID: %d", shopId));
            }
            else
            {
                response.setReturnValue("Product not found in shop");
                logger.info(String.format("Products not found in Shop ID: %d", shopId));
            }
        }
        catch (Exception e)
        {
            response.setErrorMessage(String.format("Failed to search products in shopID %d. Error: ", shopId, e.getMessage()));
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
     * Retrieves the purchase history of a shop as an admin or a shop owner with
     * checking with token that the token belongs to the this shop owner or he is an
     * admin
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
                if (_shopFacade.isShopOwner(shopId, userId) || isAdminResponse.getErrorMessage() != null) {
                    response.setErrorMessage("User has no permission to access the shop purchase history");
                    logger.log(Level.SEVERE, "User has no permission to access the shop purchase history");
                    return response;
                } else {
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

    /**
     * Adds a basic discount to a shop.
     * 
     * @param token          The session token of the user adding the discount.
     * @param shopId         The ID of the shop to which the discount will be added.
     * @param isPrecentage   A boolean indicating whether the discount is a
     *                       percentage or a fixed amount.
     * @param discountAmount The amount of the discount.
     * @param expirationDate The date on which the discount will expire.
     * @return A response indicating the success or failure of the operation.
     */
    public Response addShopBasicDiscount(String token, int shopId, boolean isPrecentage, double discountAmount,
            Date expirationDate) {
        Response resp = new Response();
        try {
            // check for user validity
            if (!_tokenService.validateToken(token))
                throw new StockMarketException("Invalid session token.");
            if (!_tokenService.isUserAndLoggedIn(token))
                throw new StockMarketException("User is not logged in");

            // check validity of input parameters
            if (!_shopFacade.isShopIdExist(shopId))
                throw new StockMarketException("Shop not found");
            if (isPrecentage && (discountAmount < 0 || discountAmount > 100))
                throw new StockMarketException("Invalid discount amount - precentage should be between 0% and 100%");
            if (!isPrecentage && discountAmount < 0)
                throw new StockMarketException("Invalid discount amount - fixed amount should be positive");
            Date currentDate = new Date();
            if (expirationDate.before(currentDate) || expirationDate.getTime() - currentDate.getTime() < 86400000)
                throw new StockMarketException("Invalid expiration date - should be at least one day into the future");

            String username = _tokenService.extractUsername(token);
            _shopFacade.addBasicDiscountToShop(shopId, username, isPrecentage, discountAmount, expirationDate);
            resp.setReturnValue("Added basic discount");
            logger.info("Added basic discount to shop: " + shopId);
            return resp;

        } catch (StockMarketException e) {
            resp.setErrorMessage("Failed to add discount to shop: " + e.getMessage());
            logger.log(Level.SEVERE, "Failed to add discount to shop: " + e.getMessage(), e);
            return resp;

        }
    }

    /**
     * Adds a conditional discount to a shop.
     * 
     * @param token            The session token of the user adding the discount.
     * @param shopId           The ID of the shop to which the discount will be
     *                         added.
     * @param mustHaveProducts A list of product IDs that must be in the shopping
     *                         basket for the discount to apply.
     * @param isPrecentage     A boolean indicating whether the discount is a
     *                         percentage or a fixed amount.
     * @param discountAmount   The amount of the discount.
     * @param expirationDate   The date on which the discount will expire.
     * @return A response indicating the success or failure of the operation.
     */
    public Response addShopConditionalDiscount(String token, int shopId, List<Integer> mustHaveProducts,
            boolean isPrecentage, double discountAmount, Date expirationDate) {
        Response resp = new Response();
        try {
            // check for user validity
            if (!_tokenService.validateToken(token))
                throw new StockMarketException("Invalid session token.");
            if (!_tokenService.isUserAndLoggedIn(token))
                throw new StockMarketException("User is not logged in");

            // check validity of input parameters
            if (!_shopFacade.isShopIdExist(shopId))
                throw new StockMarketException("Shop not found");
            if (isPrecentage && (discountAmount < 0 || discountAmount > 100))
                throw new StockMarketException("Invalid discount amount - precentage should be between 0% and 100%");
            if (!isPrecentage && discountAmount < 0)
                throw new StockMarketException("Invalid discount amount - fixed amount should be positive");
            Date currentDate = new Date();
            if (expirationDate.before(currentDate) || expirationDate.getTime() - currentDate.getTime() < 86400000)
                throw new StockMarketException("Invalid expiration date - should be at least one day into the future");

            String username = _tokenService.extractUsername(token);
            _shopFacade.addConditionalDiscountToShop(shopId, username, mustHaveProducts, isPrecentage, discountAmount,
                    expirationDate);
            resp.setReturnValue("Added conditional discount");
            logger.info("Added conditional discount to shop: " + shopId);
            return resp;

        } catch (StockMarketException e) {
            resp.setErrorMessage("Failed to add discount to shop: " + e.getMessage());
            logger.log(Level.SEVERE, "Failed to add discount to shop: " + e.getMessage(), e);
            return resp;
        }
    }

    /**
     * Updates the quantity of a specified product in a shop.
     * 
     * @param token          The session token of the user performing the update.
     * @param userName       The username of the user performing the update.
     * @param shopId         The ID of the shop where the product quantity is being updated.
     * @param productId      The ID of the product whose quantity is being updated.
     * @param productAmount  The new quantity amount of the product.
     * @return A Response object indicating the success or failure of the operation.
     * @throws StockMarketException if the session token is invalid, the user is not logged in, or the shop ID does not exist.
     */
    public Response updateProductQuantity(String token, String userName, Integer shopId, Integer productId, Integer productAmount) throws StockMarketException
    {
        Response resp = new Response();
        if (!_tokenService.validateToken(token))
            throw new StockMarketException("Invalid session token.");
        if (!_tokenService.isUserAndLoggedIn(token))
            throw new StockMarketException("User is not logged in");
        if (!_shopFacade.isShopIdExist(shopId))
            throw new StockMarketException(String.format("Shop Id: %d not found",shopId));

        try
        {
            _shopFacade.updateProductQuantity(userName, shopId, productId, productAmount);
            logger.info(String.format("Update product: %d quantity amont in shop: %d",productId, shopId));
            return resp;
        }
        catch(Exception e)
        {
            resp.setErrorMessage("Failed to add discount to shop: " + e.getMessage());
            logger.log(Level.SEVERE, String.format("Failed to update product: %d quantity to shop: %d . Error: %s",userName, shopId, e.getMessage()) , e);
            return resp;
        }
    }
}