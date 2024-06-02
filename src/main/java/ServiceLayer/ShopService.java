package ServiceLayer;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.catalina.valves.rewrite.RewriteCond.Condition;
import org.springframework.stereotype.Service;

import Domain.Facades.ShopFacade;
import Domain.Facades.UserFacade;
import Dtos.ProductDto;
import Dtos.ProductDto;
import Dtos.BasicDiscountDto;
import Dtos.ConditionalDiscountDto;
import Dtos.ShopDto;
import Domain.Product;
import Domain.ShopOrder;
import Exceptions.StockMarketException;
import enums.Category;

@Service
public class ShopService {
    private ShopFacade _shopFacade;
    private TokenService _tokenService;
    private UserFacade _userFacade;
    private static final Logger logger = Logger.getLogger(ShopFacade.class.getName());

    public ShopService(ShopFacade shopFacade, TokenService tokenService, UserFacade userFacade) {
        // _shopFacade = ShopFacade.getShopFacade();
        _shopFacade = shopFacade;
        _tokenService = tokenService;
        _userFacade = userFacade;
    }

    public ShopService() {
        _shopFacade = ShopFacade.getShopFacade();
        _tokenService = TokenService.getTokenService();
        _userFacade = UserFacade.getUserFacade();
    }

    /**
     * Opens a new shop with the specified shop ID and user name.
     * 
     * @param token       The session token of the user opening the shop.
     * @param bankDetails The bank details of the shop.
     * @param shopAddress The address of the shop.
     * @return A response indicating the success or failure of the operation.
     */
    public Response openNewShop(String token, ShopDto shopDto) {
        Response response = new Response();
        try {
            if (_tokenService.validateToken(token)) {
                if (_tokenService.isUserAndLoggedIn(token)) {
                    String founder = _tokenService.extractUsername(token);
                    int shopId = _shopFacade.openNewShop(founder, shopDto);
                    logger.info(String.format("New shop created by: %s with Shop ID: %d", founder, shopId));
                } else {
                    throw new Exception("Only register users can open shop.");
                }
            } else {
                throw new Exception("Invalid session token.");
            }

        } catch (Exception e) {
            response.setErrorMessage(
                    String.format("Failed to create shop. Error: %s", e.getMessage()));
            logger.log(Level.SEVERE, e.getMessage(), e);
        }

        return response;
    }

    /**
     * Close a shop with the specified shop ID and user name.
     * 
     * @param token  The session token of the user closing the shop.
     * @param shopId The ID of the existing shop to be closed.
     * @return A response indicating the success or failure of the operation.
     */
    public Response closeShop(String token, Integer shopId) {
        Response response = new Response();
        try {
            if (_tokenService.validateToken(token)) {
                String userName = _tokenService.extractUsername(token);
                if (_tokenService.isUserAndLoggedIn(userName)) {
                    _shopFacade.closeShop(shopId, userName);
                    logger.info(String.format("Shop closed by: %s with Shop ID: %d", userName, shopId));
                } else {
                    throw new Exception("User is not registered or not logged in.");
                }
            } else {
                throw new Exception("Invalid session token.");
            }

        } catch (Exception e) {
            response.setErrorMessage(
                    String.format("Failed to close shopID %d. Error: ", shopId, e.getMessage()));
            logger.log(Level.SEVERE, e.getMessage(), e);
        }

        return response;
    }

    /**
     * reopen a shop with the specified shop ID and user name.
     * 
     * @param token  The session token of the user reopening the shop.
     * @param shopId The ID of the existing shop to be reopen.
     * @return A response indicating the success or failure of the operation.
     */
    public Response reOpenShop(String token, Integer shopId) {
        Response response = new Response();
        try {
            if (_tokenService.validateToken(token)) {
                String userName = _tokenService.extractUsername(token);
                if (_tokenService.isUserAndLoggedIn(userName)) {
                    _shopFacade.reOpenShop(shopId, userName);
                    logger.info(String.format("Shop reopen by: %s with Shop ID: %d", userName, shopId));
                } else {
                    throw new Exception("User is not register.");
                }
            } else {
                throw new Exception("Invalid session token.");
            }

        } catch (Exception e) {
            response.setErrorMessage(
                    String.format("Failed to reopenn shopID %d. Error: ", shopId, e.getMessage()));
            logger.log(Level.SEVERE, e.getMessage(), e);
        }

        return response;
    }

    /**
     * Adds a product to the specified shop.
     * 
     * @param shopId     The ID of the shop to which the product will be added.
     * @param userName   The name of the user adding the product.
     * @param productDto The product to be added to the shop.
     * @return A response indicating the success or failure of the operation.
     */
    public Response addProductToShop(String token, Integer shopId, String userName, ProductDto productDto) {
        Response response = new Response();
        try {
            if (_tokenService.validateToken(token)) {
                if (_tokenService.isUserAndLoggedIn(userName)) {
                    _shopFacade.addProductToShop(shopId, productDto, userName);
                    logger.info(String.format("New product %s :: %d added by: %s to Shop ID: %d",
                            productDto._productName, userName, shopId));
                } else {
                    throw new Exception(String.format("User %s does not have permissions", userName));
                }
            } else {
                throw new Exception("Invalid session token.");
            }

        } catch (Exception e) {
            response.setErrorMessage(String.format("Failed to add product %s :: %d to shopID %d by user %s. Error: ",
                    productDto._productName, shopId, userName, e.getMessage()));
            logger.log(Level.SEVERE, e.getMessage(), e);
        }

        return response;
    }

    /**
     * searches products by their name.
     * 
     * @param token       The session token of the user performing the search.
     * @param shopId      The ID of the shop to search in OR null to search in all
     *                    shops.
     * @param productName he name of the product.
     * @return A response indicating the success of the operation and some products'
     *         deatails or failure.
     */
    public Response searchProductInShopByName(String token, Integer shopId, String productName) {
        // TODO: return product dtos here instead of just a string (so it will be easy
        // to render in UI)
        Response response = new Response();
        String shopIDString = (shopId == null ? "all shops" : "shop ID " + shopId.toString());
        try {
            if (_tokenService.validateToken(token)) {
                Map<Integer, List<Product>> products = _shopFacade.getProductInShopByName(shopId, productName);
                if (products != null && !products.isEmpty()) {
                    StringBuilder result = new StringBuilder();
                    result.append("Products named ").append(productName).append(" were found!:");
                    for (Map.Entry<Integer, List<Product>> entry : products.entrySet()) {
                        Integer shopID = entry.getKey();
                        List<Product> productList = entry.getValue();
                        result.append("\n").append("Shop ID: ").append(shopID).append("\n");
                        for (Product product : productList) {
                            result.append(product.toString()).append("\n");
                        }
                    }
                    response.setReturnValue(result.toString());
                    logger.info(String.format("Products named %s were found in %s", productName, shopIDString));
                } else {
                    response.setReturnValue(
                            String.format("Products named %s were not found in %s", productName, shopIDString));
                    logger.info(String.format("Products named %s were not found in %s", productName, shopIDString));
                }
            } else {
                throw new Exception("Invalid session token.");
            }
        } catch (Exception e) {
            response.setErrorMessage(String.format(String.format("Failed to search products named %s in %s . Error:",
                    productName, shopIDString, e.getMessage())));
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return response;
    }

    /**
     * searches products by their Category.
     * 
     * @param shopId          The ID of the shop to search in OR null to search in
     *                        all shops.
     * @param productCategory The category of the product.
     * @return A response indicating the success of the operation and some products'
     *         deatails or failure.
     */
    public Response searchProductInShopByCategory(String token, Integer shopId, Category productCategory) {
        Response response = new Response();
        String shopIDString = (shopId == null ? "all shops" : "shop ID " + shopId.toString());
        try {
            if (_tokenService.validateToken(token)) {
                Map<Integer, List<Product>> products = _shopFacade.getProductInShopByCategory(shopId, productCategory);
                if (products != null && !products.isEmpty()) {
                    StringBuilder result = new StringBuilder();
                    result.append("Products in the category of ").append(productCategory.toString())
                            .append(" were found!:");
                    for (Map.Entry<Integer, List<Product>> entry : products.entrySet()) {
                        Integer shopID = entry.getKey();
                        List<Product> productList = entry.getValue();
                        result.append("\n").append("Shop ID: ").append(shopID).append("\n");
                        for (Product product : productList) {
                            result.append(product.toString()).append("\n");
                        }
                    }
                    response.setReturnValue(result.toString());
                    logger.info(String.format("Products in the category of %s were found in %s",
                            productCategory.toString(), shopIDString));
                } else {
                    response.setReturnValue(String.format("Products in the category of %s were not found in %s",
                            productCategory.toString(), shopIDString));
                    logger.info(String.format("Products in the category of %s were not found in %s",
                            productCategory.toString(), shopIDString));
                }
            } else {
                throw new Exception("Invalid session token.");
            }
        } catch (Exception e) {
            response.setErrorMessage(
                    String.format(String.format("Failed to search products in the category of %s in %s . Error:",
                            productCategory.toString(), shopIDString, e.getMessage())));
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return response;
    }

    /**
     * searches products by keyWords.
     * 
     * @param shopId   The ID of the shop to search in OR null to search in all
     *                 shops.
     * @param keywords The list of keywords.
     * @return A response indicating the success of the operation and some products'
     *         deatails or failure.
     */
    public Response searchProductsInShopByKeywords(String token, Integer shopId, List<String> keywords) {
        Response response = new Response();
        // Setting strings of shop ID and keywords for logging
        String shopIDString = (shopId == null ? "all shops" : "shop ID " + shopId.toString());
        StringBuilder keywordsBuilder = new StringBuilder();
        for (String keyword : keywords) {
            keywordsBuilder.append(keyword).append(", ");
        }
        String keywordsString = keywordsBuilder.toString();
        try {
            if (_tokenService.validateToken(token)) {
                Map<Integer, List<Product>> products = _shopFacade.getProductsInShopByKeywords(shopId, keywords);
                if (products != null && !products.isEmpty()) {
                    StringBuilder result = new StringBuilder();
                    result.append("Products taged by one of the keywords: ").append(keywordsString)
                            .append(" were found!:");
                    for (Map.Entry<Integer, List<Product>> entry : products.entrySet()) {
                        Integer shopID = entry.getKey();
                        List<Product> productList = entry.getValue();
                        result.append("\n").append("Shop ID: ").append(shopID).append("\n");
                        for (Product product : productList) {
                            result.append(product.toString()).append("\n");
                        }
                    }
                    response.setReturnValue(result.toString());
                    logger.info(String.format("Products taged by the keywords: %s were found in %s", keywordsString,
                            shopIDString));
                } else {
                    response.setReturnValue(String.format("Products taged by the keywords: %s were not found in %s",
                            keywordsString, shopIDString));
                    logger.info(String.format("Products taged by the keywords: %s were not found in %s", keywordsString,
                            shopIDString));
                }
            } else {
                throw new Exception("Invalid session token.");
            }
        } catch (Exception e) {
            response.setErrorMessage(
                    String.format(String.format("Failed to search products taged by the keywords: %s in %s . Error:",
                            keywordsString, shopIDString, e.getMessage())));
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return response;
    }

    /**
     * Temporary function - replace filter
     * searches products by pricing range.
     * 
     * @param shopId   The ID of the shop to search in OR null to search in all
     *                 shops.
     * @param minPrice The minimum price of the product.
     * @param maxPrice The maximum price of the product.
     * @return A response indicating the success of the operation and some products'
     *         deatails or failure.
     */
    public Response searchProductsInShopByPriceRange(String token, Integer shopId, Double minPrice, Double maxPrice) {
        Response response = new Response();
        String shopIDString = (shopId == null ? "all shops" : "shop ID " + shopId.toString());
        try {
            if (_tokenService.validateToken(token)) {
                Map<Integer, List<Product>> products = _shopFacade.getProductsInShopByPriceRange(shopId, minPrice,
                        maxPrice);
                if (products != null && !products.isEmpty()) {
                    StringBuilder result = new StringBuilder();
                    result.append("Products in the price range of ")
                            .append(minPrice.toString() + " - " + maxPrice.toString()).append(" were found!:");
                    for (Map.Entry<Integer, List<Product>> entry : products.entrySet()) {
                        Integer shopID = entry.getKey();
                        List<Product> productList = entry.getValue();
                        result.append("\n").append("Shop ID: ").append(shopID).append("\n");
                        for (Product product : productList) {
                            result.append(product.toString()).append("\n");
                        }
                    }
                    response.setReturnValue(result.toString());
                    logger.info(String.format("Products in the price range of %d - %d were found in %s", minPrice,
                            maxPrice, shopIDString));
                } else {
                    response.setReturnValue(String.format("Products in the price range of %d - %d were not found in %s",
                            minPrice, maxPrice, shopIDString));
                    logger.info(String.format("Products in the price range of %d - %d were not found in %s", minPrice,
                            maxPrice, shopIDString));
                }
            } else {
                throw new Exception("Invalid session token.");
            }
        } catch (Exception e) {
            response.setErrorMessage(String
                    .format(String.format("Failed to search products in the price range of %d - %d in %s . Error:",
                            minPrice, maxPrice, shopIDString, e.getMessage())));
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
                if (!_shopFacade.isShopIdExist(shopId)) {
                    response.setErrorMessage("Shop not found");
                    logger.log(Level.SEVERE, "Shop not found");
                    return response;
                }

                String userId = _tokenService.extractUsername(token);
                boolean isAdmin = _userFacade.isAdmin(userId);
                if (!_shopFacade.isShopOwner(shopId, userId) && !isAdmin) {
                    response.setErrorMessage("User has no permission to access the shop purchase history");
                    logger.log(Level.SEVERE, "User has no permission to access the shop purchase history");
                    return response;
                } else {
                    // get purchase history of a shop
                    List<ShopOrder> purchasHistory = _shopFacade.getPurchaseHistory(shopId);
                    response.setReturnValue(purchasHistory);
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
     * @param productId      The ID of the product to which the discount will be
     *                       applied.
     * @param isPrecentage   A boolean indicating whether the discount is a
     *                       percentage or a fixed amount.
     * @param discountAmount The amount of the discount.
     * @param expirationDate The date on which the discount will expire.
     * @return A response indicating the success (discount id) or failure (error
     *         message) of the operation.
     */
    public Response addShopBasicDiscount(String token, int shopId, BasicDiscountDto basicDiscountDto) {
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
            if (basicDiscountDto.isPrecentage
                    && (basicDiscountDto.discountAmount < 0 || basicDiscountDto.discountAmount > 100))
                throw new StockMarketException("Invalid discount amount - precentage should be between 0% and 100%");
            if (!basicDiscountDto.isPrecentage && basicDiscountDto.discountAmount < 0)
                throw new StockMarketException("Invalid discount amount - fixed amount should be positive");
            Date currentDate = new Date();
            if (basicDiscountDto.expirationDate.getTime() - currentDate.getTime() < 86400000)
                throw new StockMarketException("Invalid expiration date - should be at least one day into the future");

            String username = _tokenService.extractUsername(token);
            int discountId = _shopFacade.addBasicDiscountToShop(shopId, username, basicDiscountDto);
            resp.setReturnValue(discountId);
            logger.info("Added basic discount to shop: " + shopId + " with id " + discountId);
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
     * @param productId        The ID of the product to which the discount will be
     *                         applied.
     * @param mustHaveProducts A list of product IDs that must be in the shopping
     *                         basket for the discount to apply.
     * @param isPrecentage     A boolean indicating whether the discount is a
     *                         percentage or a fixed amount.
     * @param discountAmount   The amount of the discount.
     * @param expirationDate   The date on which the discount will expire.
     * @return A response indicating the success (discount id) or failure (error
     *         message) of the operation.
     */
    public Response addShopConditionalDiscount(String token, int shopId,
            ConditionalDiscountDto conditionalDiscountDto) {
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
            if (conditionalDiscountDto.isPrecentage
                    && (conditionalDiscountDto.discountAmount < 0 || conditionalDiscountDto.discountAmount > 100))
                throw new StockMarketException("Invalid discount amount - precentage should be between 0% and 100%");
            if (!conditionalDiscountDto.isPrecentage && conditionalDiscountDto.discountAmount < 0)
                throw new StockMarketException("Invalid discount amount - fixed amount should be positive");
            Date currentDate = new Date();
            if (conditionalDiscountDto.expirationDate.before(currentDate)
                    || conditionalDiscountDto.expirationDate.getTime() - currentDate.getTime() < 86400000)
                throw new StockMarketException("Invalid expiration date - should be at least one day into the future");

            String username = _tokenService.extractUsername(token);
            int discountId = _shopFacade.addConditionalDiscountToShop(shopId, username, conditionalDiscountDto);
            resp.setReturnValue(discountId);
            logger.info("Added conditional discount to shop: " + shopId + " with id " + discountId);
            return resp;

        } catch (StockMarketException e) {
            resp.setErrorMessage("Failed to add discount to shop: " + e.getMessage());
            logger.log(Level.SEVERE, "Failed to add discount to shop: " + e.getMessage(), e);
            return resp;
        }
    }

    public Response removeDiscount(String token, int shopId, int discountId) {
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

            String username = _tokenService.extractUsername(token);
            _shopFacade.removeDiscountFromShop(shopId, discountId, username);
            resp.setReturnValue("Removed discount");
            logger.info("Removed discount from shop: " + shopId);
            return resp;

        } catch (StockMarketException e) {
            resp.setErrorMessage("Failed to remove discount from shop: " + e.getMessage());
            logger.log(Level.SEVERE, "Failed to remove discount from shop: " + e.getMessage(), e);
            return resp;
        }
    }

    /**
     * Updates the quantity of a specified product in a shop.
     * 
     * @param token         The session token of the user performing the update.
     * @param shopId        The ID of the shop where the product quantity is being
     *                      updated.
     * @param productId     The ID of the product whose quantity is being updated.
     * @param productAmount The new quantity amount of the product.
     * @return A Response object indicating the success or failure of the operation.
     */
    public Response updateProductQuantity(String token, Integer shopId, Integer productId, Integer productAmount) {
        Response resp = new Response();
        try {
            if (!_tokenService.validateToken(token))
                throw new StockMarketException("Invalid session token.");
            if (!_tokenService.isUserAndLoggedIn(token))
                throw new StockMarketException("User is not logged in");
            if (!_shopFacade.isShopIdExist(shopId))
                throw new StockMarketException(String.format("Shop Id: %d not found", shopId));

            String userName = _tokenService.extractUsername(token);
            _shopFacade.updateProductQuantity(userName, shopId, productId, productAmount);
            logger.info(String.format("Update product: %d quantity amont in shop: %d", productId, shopId));
            return resp;
        } catch (Exception e) {
            resp.setErrorMessage("Failed to add discount to shop: " + e.getMessage());
            logger.log(Level.SEVERE, String.format("Failed to update product: %d quantity to shop: %d . Error: %s",
                    productId, shopId, e.getMessage()), e);
            return resp;
        }
    }

    /**
     * Adds a new owner to a shop.
     * 
     * @param token            The session token of the user performing the update.
     * @param username         The username of the user performing the update.
     * @param shopId           The ID of the shop where the new owner is being
     *                         added.
     * @param newOwnerUsername The username of the new owner being added to the
     *                         shop.
     * @return A Response object indicating the success or failure of the operation.
     */
    public Response addShopOwner(String token, String username, Integer shopId, String newOwnerUsername) {
        Response response = new Response();
        try {
            if (_tokenService.validateToken(token)) {
                if (_tokenService.isUserAndLoggedIn(username)) {
                    if (_userFacade.doesUserExist(username)) {
                        _shopFacade.addShopOwner(username, shopId, newOwnerUsername);
                        response.setReturnValue(true);
                        logger.info(String.format("New owner %s added to Shop ID: %d", username, shopId));
                    } else {
                        throw new StockMarketException("User does not exist.");
                    }
                } else {
                    throw new StockMarketException("User is not logged in.");
                }
            } else {
                throw new StockMarketException("Invalid session token.");
            }

        } catch (Exception e) {
            response.setErrorMessage(
                    String.format("Failed to add owner %s to shopID %d. Error: ", username, shopId, e.getMessage()));
            logger.log(Level.SEVERE, e.getMessage(), e);
        }

        return response;
    }

    /**
     * Adds a new manager to a shop.
     * 
     * @param token              The session token of the user performing the
     *                           update.
     * @param username           The username of the user performing the
     *                           appointment.
     * @param shopId             The ID of the shop where the new manager is being
     *                           added.
     * @param newManagerUsername The username of the new manager being added to the
     *                           shop.
     * @param permissions        The permissions granted to the new manager.
     * @return A Response object indicating the success or failure of the operation.
     */
    public Response addShopManager(String token, String username, Integer shopId, String newManagerUsername,
            Set<String> permissions) {
        Response response = new Response();
        try {
            if (_tokenService.validateToken(token)) {
                if (_tokenService.isUserAndLoggedIn(username)) {
                    if (_userFacade.doesUserExist(username)) {
                        _shopFacade.addShopManager(username, shopId, newManagerUsername, permissions);
                        response.setReturnValue(true);
                        logger.info(String.format("New manager %s added to Shop ID: %d", username, shopId));
                    } else {
                        throw new StockMarketException("User does not exist.");
                    }
                } else {
                    throw new StockMarketException("User is not logged in.");
                }
            } else {
                throw new StockMarketException("Invalid session token.");
            }

        } catch (Exception e) {
            response.setErrorMessage(
                    String.format("Failed to add manager %s to shopID %d. Error: ", username, shopId, e.getMessage()));
            logger.log(Level.SEVERE, e.getMessage(), e);
        }

        return response;
    }

    /**
     * Fires a manager from a shop.
     * 
     * @param token           The session token of the user performing the update.
     * @param username        The username of the user performing the fire.
     * @param shopId          The ID of the shop where the manager is being fired.
     * @param managerUsername The username of the manager being fired from the shop.
     * @return A Response object indicating the success and the set of usernames
     *         fired or failure of the operation.
     */
    public Response fireShopManager(String token, String username, Integer shopId, String managerUsername) {
        Response response = new Response();
        try {
            if (_tokenService.validateToken(token)) {
                if (_tokenService.isUserAndLoggedIn(username)) {
                    if (_userFacade.doesUserExist(username)) {
                        Set<String> fired = _shopFacade.fireShopManager(username, shopId, managerUsername);
                        response.setReturnValue(fired);
                        logger.info(String.format("Manager %s fired from Shop ID: %d", managerUsername, shopId));
                        logger.info(String.format("Managers " + fired + " were fired from Shop ID: %d", shopId));
                    } else {
                        throw new StockMarketException("User does not exist.");
                    }
                } else {
                    throw new StockMarketException("User is not logged in.");
                }
            } else {
                throw new StockMarketException("Invalid session token.");
            }

        } catch (Exception e) {
            response.setErrorMessage(
                    String.format("Failed to fire manager %s from shopID %d. Error: ", managerUsername, shopId,
                            e.getMessage()));
            logger.log(Level.SEVERE, e.getMessage(), e);
        }

        return response;
    }

    /**
     * Resigns from a role in a shop.
     * 
     * @param token    The session token of the user performing the resignation.
     * @param username The username of the user resigning from the role.
     * @param shopId   The ID of the shop where the user is resigning from the role.
     * @return A Response object indicating the success and the set of usernames
     *         resigned or failure of the operation.
     */
    public Response resignFromRole(String token, String username, Integer shopId) {
        Response response = new Response();
        try {
            if (_tokenService.validateToken(token)) {
                if (_tokenService.isUserAndLoggedIn(username)) {
                    if (_userFacade.doesUserExist(username)) {
                        Set<String> resigned = _shopFacade.resignFromRole(username, shopId);
                        response.setReturnValue(true);
                        logger.info(String.format("User %s resigned from Shop ID: %d", username, shopId));
                        logger.info(String.format("Subordinates " + resigned + " resigned too from Shop ID: %d",
                                username, shopId));
                    } else {
                        throw new StockMarketException("User does not exist.");
                    }
                } else {
                    throw new StockMarketException("User is not logged in.");
                }
            } else {
                throw new StockMarketException("Invalid session token.");
            }

        } catch (Exception e) {
            response.setErrorMessage(
                    String.format("Failed to resign from shopID %d. Error: ", shopId, e.getMessage()));
            logger.log(Level.SEVERE, e.getMessage(), e);
        }

        return response;
    }

    /**
     * Modifies the permissions of a manager in a shop.
     * 
     * @param token           The session token of the user performing the
     *                        modification.
     * @param username        The username of the user performing the modification.
     * @param shopId          The ID of the shop where the manager's permissions are
     *                        being modified.
     * @param managerUsername The username of the manager whose permissions are
     *                        being modified.
     * @param permissions     The new set of permissions for the manager.
     * @return A Response object indicating the success or failure of the operation.
     */
    public Response modifyManagerPermissions(String token, String username, Integer shopId, String managerUsername,
            Set<String> permissions) {
        Response response = new Response();
        try {
            if (_tokenService.validateToken(token)) {
                if (_tokenService.isUserAndLoggedIn(username)) {
                    if (_userFacade.doesUserExist(username)) {
                        _shopFacade.modifyManagerPermissions(username, shopId, managerUsername, permissions);
                        response.setReturnValue(true);
                        logger.info(String.format("Manager %s permissions modified in Shop ID: %d", managerUsername,
                                shopId));
                    } else {
                        throw new StockMarketException("User does not exist.");
                    }
                } else {
                    throw new StockMarketException("User is not logged in.");
                }
            } else {
                throw new StockMarketException("Invalid session token.");
            }

        } catch (Exception e) {
            response.setErrorMessage(
                    String.format("Failed to modify manager %s permissions in shopID %d. Error: ", managerUsername,
                            shopId, e.getMessage()));
            logger.log(Level.SEVERE, e.getMessage(), e);
        }

        return response;
    }

    /**
     * Shows the shop policy information.
     * 
     * @param token  The session token of the user performing the operation.
     * @param shopId The ID of the desired shop.
     * @return A response containing the shop policy information.
     */
    public Response displayShopPolicyInfo(String token, Integer shopId) {
        Response response = new Response();
        try {
            if (_tokenService.validateToken(token)) {
                String policy = _shopFacade.getShopPolicyInfo(shopId);
                if (policy != null && policy.length() > 0) {
                    response.setReturnValue(
                            String.format("Shop policy information: \n Shop ID: %d, \n Policy: %s", shopId, policy));
                    logger.info(String.format("Shop policy information for shop ID %d is displayed", shopId));
                } else {
                    response.setReturnValue(
                            String.format("Shop policy information for shop ID %d was not found", shopId));
                    logger.info(String.format("Shop policy information for shop ID %d was not found", shopId));
                }
            } else {
                throw new Exception("Invalid session token.");
            }
        } catch (Exception e) {
            response.setErrorMessage(
                    String.format(String.format("Failed to display shop policy information for shop ID %d . Error:",
                            shopId, e.getMessage())));
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return response;
    }

    /**
     * Shows the product policy information.
     * 
     * @param token     The session token of the user performing the operation.
     * @param shopId    The ID of the shop where the product is located.
     * @param productId The ID of the product.
     * @return A response containing the product policy information.
     */
    public Response displayProductPolicyInfo(String token, Integer shopId, Integer productId) {
        // TODO: Decide on correct way to implement - Objects(discounts) or
        // Strings(Policy)
        Response response = new Response();
        try {
            if (_tokenService.validateToken(token)) {
                String policy = _shopFacade.getProductPolicyInfo(shopId, productId);
                if (policy != null && policy.length() > 0) {
                    response.setReturnValue(
                            String.format("Product policy information: \n Product ID: %d, Shop ID: %d, \n Policy: %s",
                                    productId, shopId, policy));
                    logger.info(
                            String.format("Product policy information for product ID %d, of shop ID %d is displayed",
                                    productId, shopId));
                } else {
                    response.setReturnValue(
                            String.format("Product policy information for product ID %d, of shop ID %d was not found",
                                    productId, shopId));
                    logger.info(
                            String.format("Product policy information for product ID %d, of shop ID %d was not found",
                                    productId, shopId));
                }
            } else {
                throw new Exception("Invalid session token.");
            }
        } catch (Exception e) {
            response.setErrorMessage(
                    String.format(String.format(
                            "Failed to display product policy information for product: %d in shop: %d . Error:",
                            productId, shopId, e.getMessage())));
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return response;
    }

    /**
     * Shows the shop discounts information.
     * 
     * @param token  The session token of the user performing the operation.
     * @param shopId The ID of the desired shop.
     * @return A response containing the shop discounts information.
     */
    public Response displayShopDiscountsInfo(String token, Integer shopId) {
        Response response = new Response();
        try {
            if (_tokenService.validateToken(token)) {

                String discounts = _shopFacade.getShopDiscountsInfo(shopId);
                if (discounts != null && discounts.length() > 0) {
                    response.setReturnValue(String
                            .format("Shop discounts information: \n Shop ID: %d, \n Discounts: %s", shopId, discounts));
                    logger.info(String.format("Shop discounts information for shop ID %d is displayed", shopId));
                } else {
                    response.setReturnValue(
                            String.format("Shop discounts information for shop ID %d was not found", shopId));
                    logger.info(String.format("Shop discounts information for shop ID %d was not found", shopId));
                }
            } else {
                throw new Exception("Invalid session token.");
            }
        } catch (Exception e) {
            response.setErrorMessage(
                    String.format(String.format("Failed to display shop discounts information for shop ID %d . Error:",
                            shopId, e.getMessage())));
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return response;
    }

    /**
     * Shows the product discounts information.
     * 
     * @param token     The session token of the user performing the operation.
     * @param shopId    The ID of the shop where the product is located.
     * @param productId The ID of the product.
     * @return A response containing the product discounts information.
     */
    public Response displayProductDiscountsInfo(String token, Integer shopId, Integer productId) {
        // TODO: Decide on correct way to implement - Objects(discounts) or
        // Strings(Policy)
        Response response = new Response();
        try {
            if (_tokenService.validateToken(token)) {
                String discounts = _shopFacade.getProductDiscountsInfo(shopId, productId);
                if (discounts != null && discounts.length() > 0) {
                    response.setReturnValue(String.format(
                            "Product discounts information: \n Product ID: %d, Shop ID: %d, \n Discounts: %s",
                            productId, shopId, discounts));
                    logger.info(
                            String.format("Product discounts information for product ID %d, of shop ID %d is displayed",
                                    productId, shopId));
                } else {
                    response.setReturnValue(String.format(
                            "Product discounts information for product ID %d, of shop ID %d was not found", productId,
                            shopId));
                    logger.info(String.format(
                            "Product discounts information for product ID %d, of shop ID %d was not found", productId,
                            shopId));
                }
            } else {
                throw new Exception("Invalid session token.");
            }
        } catch (Exception e) {
            response.setErrorMessage(
                    String.format(String.format(
                            "Failed to display product discounts information for product: %d in shop: %d . Error:",
                            productId, shopId, e.getMessage())));
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return response;
    }

    /**
     * Shows the shop general information.
     * 
     * @param token  The session token of the user performing the operation.
     * @param shopId The ID of the desired shop.
     * @return A response containing the shop General information.
     */
    public Response displayShopGeneralInfo(String token, Integer shopId) {
        Response response = new Response();
        try {
            if (_tokenService.validateToken(token)) {

                String info = _shopFacade.getShopGeneralInfo(shopId);
                if (info != null && info.length() > 0) {
                    response.setReturnValue(String.format(
                            "Shop general information: \n Shop ID: %d, \n General information: %s", shopId, info));
                    logger.info(String.format("Shop general information for shop ID %d is displayed", shopId));
                } else {
                    response.setReturnValue(
                            String.format("Shop general information for shop ID %d was not found", shopId));
                    logger.info(String.format("Shop general information for shop ID %d was not found", shopId));
                }
            } else {
                throw new Exception("Invalid session token.");
            }
        } catch (Exception e) {
            response.setErrorMessage(
                    String.format(String.format("Failed to display shop general information for shop ID %d . Error:",
                            shopId, e.getMessage())));
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return response;
    }

    /**
     * Shows the product general information.
     * 
     * @param token     The session token of the user performing the operation.
     * @param shopId    The ID of the shop where the product is located.
     * @param productId The ID of the product.
     * @return A response containing the product general information.
     */
    public Response displayProductGeneralInfo(String token, Integer shopId, Integer productId) {
        // TODO: Decide on correct way to implement - Objects(discounts) or
        // Strings(Policy)
        Response response = new Response();
        try {
            if (_tokenService.validateToken(token)) {
                String info = _shopFacade.getProductGeneralInfo(shopId, productId);
                if (info != null && info.length() > 0) {
                    response.setReturnValue(String.format(
                            "Product general information: \n Product ID: %d, Shop ID: %d, \n General information: %s",
                            productId, shopId, info));
                    logger.info(
                            String.format("Product general information for product ID %d, of shop ID %d is displayed",
                                    productId, shopId));
                } else {
                    response.setReturnValue(
                            String.format("Product general information for product ID %d, of shop ID %d was not found",
                                    productId, shopId));
                    logger.info(
                            String.format("Product general information for product ID %d, of shop ID %d was not found",
                                    productId, shopId));
                }
            } else {
                throw new Exception("Invalid session token.");
            }
        } catch (Exception e) {
            response.setErrorMessage(
                    String.format(String.format(
                            "Failed to display product general information for product: %d in shop: %d . Error:",
                            productId, shopId, e.getMessage())));
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return response;
    }

}