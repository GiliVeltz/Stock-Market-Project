package ServiceLayer;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import Domain.Facades.ShopFacade;
import Domain.Facades.UserFacade;
import Domain.Product;
import Domain.ShopOrder;
import Dtos.BasicDiscountDto;
import Dtos.ConditionalDiscountDto;
import Dtos.ProductDto;
import Dtos.ShopDto;
import Dtos.ShoppingBasketRuleDto;
import Exceptions.StockMarketException;
import enums.Category;

@Service
public class ShopService {
    private ShopFacade _shopFacade;
    private TokenService _tokenService;
    private UserFacade _userFacade;
    // private AlertService _alertService;

    private static final Logger logger = Logger.getLogger(ShopFacade.class.getName());

    public ShopService(ShopFacade shopFacade, TokenService tokenService, UserFacade userFacade) {
        // _shopFacade = ShopFacade.getShopFacade();
        _shopFacade = shopFacade;
        _tokenService = tokenService;
        _userFacade = userFacade;
    }
    // @Autowired
    // public ShopService(ShopFacade shopFacade, TokenService tokenService, UserFacade userFacade, AlertService alertService) {
    //     _shopFacade = shopFacade;
    //     _tokenService = tokenService;
    //     _userFacade = userFacade;
    //     _alertService = alertService;
    // }

  
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
    public ResponseEntity<Response> openNewShop(String token, ShopDto shopDto) {
        Response response = new Response();
        try {
            if (_tokenService.validateToken(token)) {
                if (_tokenService.isUserAndLoggedIn(token)) {
                    String founder = _tokenService.extractUsername(token);
                    int shopId = _shopFacade.openNewShop(founder, shopDto);
                    logger.info(String.format("New shop created by: %s with Shop ID: %d", founder, shopId));
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                    response.setErrorMessage("Only register users can open shop.");
                    return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
                }
            } else {
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }

        } catch (Exception e) {
            response.setErrorMessage(
                    String.format("Failed to create shop. Error: %s", e.getMessage()));
            logger.log(Level.SEVERE, e.getMessage(), e);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Close a shop with the specified shop ID and user name.
     * 
     * @param token  The session token of the user closing the shop.
     * @param shopId The ID of the existing shop to be closed.
     * @return A response indicating the success or failure of the operation.
     */
    public ResponseEntity<Response> closeShop(String token, Integer shopId) {
        Response response = new Response();
        try {
            if (_tokenService.validateToken(token)) {
                String userName = _tokenService.extractUsername(token);
                if (_tokenService.isUserAndLoggedIn(token)) {
                    _shopFacade.closeShop(shopId, userName);
                    logger.info(String.format("Shop closed by: %s with Shop ID: %d", userName, shopId));
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                    response.setErrorMessage("User is not registered or not logged in.");
                    return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
                }
            } else {
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }

        } catch (Exception e) {
            response.setErrorMessage(
                    String.format("Failed to close shopID %d. Error: %s", shopId, e.getMessage()));
            logger.log(Level.SEVERE, e.getMessage(), e);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * reopen a shop with the specified shop ID and user name.
     * 
     * @param token  The session token of the user reopening the shop.
     * @param shopId The ID of the existing shop to be reopen.
     * @return A response indicating the success or failure of the operation.
     */
    public ResponseEntity<Response> reOpenShop(String token, Integer shopId) {
        Response response = new Response();
        try {
            if (_tokenService.validateToken(token)) {
                String userName = _tokenService.extractUsername(token);
                if (_tokenService.isUserAndLoggedIn(token)) {
                    _shopFacade.reOpenShop(shopId, userName);

                    
                    logger.info(String.format("Shop reopen by: %s with Shop ID: %d", userName, shopId));
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                    response.setErrorMessage("User is not register.");
                    return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
                }
            } else {
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }

        } catch (Exception e) {
            response.setErrorMessage(
                    String.format("Failed to reopenn shopID %d. Error: ", shopId, e.getMessage()));
            logger.log(Level.SEVERE, e.getMessage(), e);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Adds a product to the specified shop.
     * 
     * @param shopId     The ID of the shop to which the product will be added.
     * @param productDto The product to be added to the shop.
     * @return A response indicating the success or failure of the operation.
     */
    public ResponseEntity<Response> addProductToShop(String token, Integer shopId, ProductDto productDto) {
        Response response = new Response();
        try {
            if (_tokenService.validateToken(token)) {
                String userName = _tokenService.extractUsername(token);
                if (_tokenService.isUserAndLoggedIn(token)) {
                    _shopFacade.addProductToShop(shopId, productDto, userName);
                    logger.info(String.format("New product %s :: added by: %s to Shop ID: %d",
                            productDto._productName, userName, shopId));
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                    response.setErrorMessage(String.format("User %s does not have permissions", userName));
                    return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
                }
            } else {
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }

        } catch (Exception e) {
            response.setErrorMessage(String.format("Failed to add product %s :: to shopID %d by user %s. Error: %s",
                    productDto._productName, shopId, _tokenService.extractUsername(token), e.getMessage()));
            logger.log(Level.SEVERE, String.format("Failed to add product %s :: to shopID %d by user %s. Error: %s",
                    productDto._productName, shopId, _tokenService.extractUsername(token), e.getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Removes a product from the specified shop.
     * 
     * @param shopId     The ID of the shop to which the product will be removed.
     * @param productDto The product to be removed from the shop.
     * @return A response indicating the success or failure of the operation.
     */
    public ResponseEntity<Response> removeProductFromShop(String token, Integer shopId, ProductDto productDto) {
        Response response = new Response();
        try {
            if (_tokenService.validateToken(token)) {
                String userName = _tokenService.extractUsername(token);
                if (_tokenService.isUserAndLoggedIn(token)) {
                    _shopFacade.removeProductFromShop(shopId, productDto, userName);
                    logger.info(String.format("The product %s :: removed by: %s from Shop ID: %d",
                            productDto._productName, userName, shopId));
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                    response.setErrorMessage(String.format("User %s does not have permissions", userName));
                    return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
                }
            } else {
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }

        } catch (Exception e) {
            response.setErrorMessage(String.format("Failed to remove product %s :: from shopID %d by user %s. Error: %s",
                    productDto._productName, shopId, _tokenService.extractUsername(token), e.getMessage()));
            logger.log(Level.SEVERE, String.format("Failed to remove product %s :: from shopID %d by user %s. Error: %s",
                productDto._productName, shopId, _tokenService.extractUsername(token), e.getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Edits a product in the specified shop.
     * 
     * @param shopId     The ID of the shop to which the product will be edited.
     * @param productDtoOld The product to be edit in the shop - the old vars of the product.
     * @param productDtoNew The product to be edit in the shop - the new vars of the product.
     * @return A response indicating the success or failure of the operation.
     */
    public ResponseEntity<Response> editProductInShop(String token, Integer shopId, ProductDto productDtoOld, ProductDto productDtoNew) {
        Response response = new Response();
        try {
            if (_tokenService.validateToken(token)) {
                String userName = _tokenService.extractUsername(token);
                if (_tokenService.isUserAndLoggedIn(token)) {
                    _shopFacade.editProductInShop(shopId, productDtoOld, productDtoNew, userName);
                    logger.info(String.format("The product %s :: edited by: %s in Shop ID: %d",
                            productDtoOld._productName, userName, shopId));
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                    response.setErrorMessage(String.format("User %s does not have permissions", userName));
                    return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
                }
            } else {
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }

        } catch (Exception e) {
            response.setErrorMessage(String.format("Failed to edit product %s :: from shopID %d by user %s. Error: %s",
                productDtoOld._productName, shopId, _tokenService.extractUsername(token), e.getMessage()));
            logger.log(Level.SEVERE, String.format("Failed to edit product %s :: from shopID %d by user %s. Error: %s",
                productDtoOld._productName, shopId, _tokenService.extractUsername(token), e.getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * searches products by their name.
     * 
     * @param token       The session token of the user performing the search.
     * @param shopId      The ID of the shop to search in OR null to search in all
     *                    shops.
     * @param productName he name of the product.
     * @return A response indicating the success of the operation, containing a dictionary of shopID and ProductDTOs, or indicating failure.
     */
    public ResponseEntity<Response> searchProductInShopByName(String token, Integer shopId, String productName) {
        Response response = new Response();
        String shopIDString = (shopId == null ? "all shops" : "shop ID " + shopId.toString());
        try {
            if (_tokenService.validateToken(token)) {
                Map<Integer, List<Product>> products = _shopFacade.getProductInShopByName(shopId, productName);
                if (products != null && !products.isEmpty()) {
                    Map<Integer, List<ProductDto>> productDtosPerShop = new HashMap<>();
                    for (Map.Entry<Integer, List<Product>> entry : products.entrySet()) {
                        List<ProductDto> productDtoList = new ArrayList<>();
                        for (Product product : entry.getValue()) {
                            ProductDto productDto = new ProductDto(product);
                            productDtoList.add(productDto);
                        }
                        productDtosPerShop.put(entry.getKey(), productDtoList);
                    }
                    response.setReturnValue(productDtosPerShop);
                    logger.info(String.format("Products named %s were found in %s", productName, shopIDString));
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                    response.setReturnValue(
                            String.format("Products named %s were not found in %s", productName, shopIDString));
                    logger.info(String.format("Products named %s were not found in %s", productName, shopIDString));
                    return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
                }
            } else {
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            response.setErrorMessage(String.format(String.format("Failed to search products named %s in %s . Error:",
                    productName, shopIDString, e.getMessage())));
            logger.log(Level.SEVERE, e.getMessage(), e);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * searches products by their Category.
     * 
     * @param shopId          The ID of the shop to search in OR null to search in
     *                        all shops.
     * @param productCategory The category of the product.
     * @return A response indicating the success of the operation, containing a dictionary of shopID and ProductDTOs, or indicating failure.
     */
    public ResponseEntity<Response> searchProductInShopByCategory(String token, Integer shopId, Category productCategory) {
        Response response = new Response();
        String shopIDString = (shopId == null ? "all shops" : "shop ID " + shopId.toString());
        try {
            if (_tokenService.validateToken(token)) {
                Map<Integer, List<Product>> products = _shopFacade.getProductInShopByCategory(shopId, productCategory);
                if (products != null && !products.isEmpty()) {
                    Map<Integer, List<ProductDto>> productDtosPerShop = new HashMap<>();
                    for (Map.Entry<Integer, List<Product>> entry : products.entrySet()) {
                        List<ProductDto> productDtoList = new ArrayList<>();
                        for (Product product : entry.getValue()) {
                            ProductDto productDto = new ProductDto(product);
                            productDtoList.add(productDto);
                        }
                        productDtosPerShop.put(entry.getKey(), productDtoList);
                    }
                    response.setReturnValue(productDtosPerShop);
                    logger.info(String.format("Products in the category of %s were found in %s",
                            productCategory.toString(), shopIDString));
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                    response.setReturnValue(String.format("Products in the category of %s were not found in %s",
                            productCategory.toString(), shopIDString));
                    logger.info(String.format("Products in the category of %s were not found in %s",
                            productCategory.toString(), shopIDString));
                    return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
                }
            } else {
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            response.setErrorMessage(
                    String.format(String.format("Failed to search products in the category of %s in %s . Error:",
                            productCategory.toString(), shopIDString, e.getMessage())));
            logger.log(Level.SEVERE, e.getMessage(), e);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * searches products by keyWords.
     * 
     * @param shopId   The ID of the shop to search in OR null to search in all
     *                 shops.
     * @param keywords The list of keywords.
     * @return A response indicating the success of the operation, containing a dictionary of shopID and ProductDTOs, or indicating failure.
     */
    public ResponseEntity<Response> searchProductsInShopByKeywords(String token, Integer shopId, List<String> keywords) {
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
                    Map<Integer, List<ProductDto>> productDtosPerShop = new HashMap<>();
                    for (Map.Entry<Integer, List<Product>> entry : products.entrySet()) {
                        List<ProductDto> productDtoList = new ArrayList<>();
                        for (Product product : entry.getValue()) {
                            ProductDto productDto = new ProductDto(product);
                            productDtoList.add(productDto);
                        }
                        productDtosPerShop.put(entry.getKey(), productDtoList);
                    }
                    response.setReturnValue(productDtosPerShop);
                    logger.info(String.format("Products taged by the keywords: %s were found in %s", keywordsString,
                            shopIDString));
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                    response.setReturnValue(String.format("Products taged by the keywords: %s were not found in %s",
                            keywordsString, shopIDString));
                    logger.info(String.format("Products taged by the keywords: %s were not found in %s", keywordsString,
                            shopIDString));
                    return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
                }
            } else {
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            response.setErrorMessage(
                    String.format(String.format("Failed to search products taged by the keywords: %s in %s . Error:",
                            keywordsString, shopIDString, e.getMessage())));
            logger.log(Level.SEVERE, e.getMessage(), e);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Temporary function - replace filter
     * searches products by pricing range.
     * 
     * @param shopId   The ID of the shop to search in OR null to search in all
     *                 shops.
     * @param minPrice The minimum price of the product.
     * @param maxPrice The maximum price of the product.
     * @return A response indicating the success of the operation, containing a dictionary of shopID and ProductDTOs, or indicating failure.
     */
    public ResponseEntity<Response> searchProductsInShopByPriceRange(String token, Integer shopId, Double minPrice, Double maxPrice) {
        Response response = new Response();
        String shopIDString = (shopId == null ? "all shops" : "shop ID " + shopId.toString());
        try {
            if (_tokenService.validateToken(token)) {
                Map<Integer, List<Product>> products = _shopFacade.getProductsInShopByPriceRange(shopId, minPrice,
                        maxPrice);
                if (products != null && !products.isEmpty()) {
                    Map<Integer, List<ProductDto>> productDtosPerShop = new HashMap<>();
                    for (Map.Entry<Integer, List<Product>> entry : products.entrySet()) {
                        List<ProductDto> productDtoList = new ArrayList<>();
                        for (Product product : entry.getValue()) {
                            ProductDto productDto = new ProductDto(product);
                            productDtoList.add(productDto);
                        }
                        productDtosPerShop.put(entry.getKey(), productDtoList);
                    }
                    response.setReturnValue(productDtosPerShop);
                    logger.info(String.format("Products in the price range of %d - %d were found in %s", minPrice,
                            maxPrice, shopIDString));
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                    response.setReturnValue(String.format("Products in the price range of %d - %d were not found in %s",
                            minPrice, maxPrice, shopIDString));
                    logger.info(String.format("Products in the price range of %d - %d were not found in %s", minPrice,
                            maxPrice, shopIDString));
                    return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
                }
            } else {
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            response.setErrorMessage(String
                    .format(String.format("Failed to search products in the price range of %d - %d in %s . Error:",
                            minPrice, maxPrice, shopIDString, e.getMessage())));
            logger.log(Level.SEVERE, e.getMessage(), e);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Checks if the given user is the owner of the given shop.
     * @param shopId
     * @param userId
     * @return
     */
    public ResponseEntity<Response> isShopOwner(Integer shopId, String userId) {
        Response response = new Response();
        try {
            Boolean isOwner = _shopFacade.isShopOwner(shopId, userId);
            response.setReturnValue(isOwner);
            logger.info(String.format("User %s is owner of Shop ID: %d: %b", userId, shopId, isOwner));
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            response.setErrorMessage(String.format("Failed to check if user %s is owner of shopID %d. Error: ", userId,
                    shopId, e.getMessage()));
            logger.log(Level.SEVERE, e.getMessage(), e);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
     */
    public ResponseEntity<Response> getShopPurchaseHistory(String token, Integer shopId) {
        Response response = new Response();

        try {
            if (_tokenService.validateToken(token)) {
                if (!_tokenService.isUserAndLoggedIn(token)) {
                    response.setErrorMessage("User is not logged in");
                    logger.log(Level.SEVERE, "User is not logged in");
                    return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
                }
                // check if the shop exist with
                if (!_shopFacade.isShopIdExist(shopId)) {
                    response.setErrorMessage("Shop not found");
                    logger.log(Level.SEVERE, "Shop not found");
                    return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
                }

                String userId = _tokenService.extractUsername(token);
                boolean isAdmin = _userFacade.isAdmin(userId);
                if (!_shopFacade.isShopOwner(shopId, userId) && !isAdmin) {
                    response.setErrorMessage("User has no permission to access the shop purchase history");
                    logger.log(Level.SEVERE, "User has no permission to access the shop purchase history");
                    return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
                } else {
                    // get purchase history of a shop
                    List<ShopOrder> purchasHistory = _shopFacade.getPurchaseHistory(shopId);
                    response.setReturnValue(purchasHistory);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }

            } else {
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }

        } catch (Exception e) {
            response.setErrorMessage("Failed to get purchase history: " + e.getMessage());
            logger.log(Level.SEVERE, "Failed to get purchase history: " + e.getMessage(), e);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
    public ResponseEntity<Response> addShopBasicDiscount(String token, int shopId, BasicDiscountDto basicDiscountDto) {
        Response response = new Response();
        try {
            // check for user validity
            if (!_tokenService.validateToken(token))
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            if (!_tokenService.isUserAndLoggedIn(token)){
                response.setErrorMessage("User is not logged in");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }

            // check validity of input parameters
            if (!_shopFacade.isShopIdExist(shopId)){
                response.setErrorMessage("Shop not found");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }
            if (basicDiscountDto.isPrecentage
                    && (basicDiscountDto.discountAmount < 0 || basicDiscountDto.discountAmount > 100)){
                    response.setErrorMessage("Invalid discount amount - precentage should be between 0% and 100%");
                    return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }
            if (!basicDiscountDto.isPrecentage && basicDiscountDto.discountAmount < 0){
                response.setErrorMessage("Invalid discount amount - fixed amount should be positive");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }

            Date currentDate = new Date();
            if (basicDiscountDto.expirationDate.getTime() - currentDate.getTime() < 86400000){
                response.setErrorMessage("Invalid expiration date - should be at least one day into the future");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }

            String username = _tokenService.extractUsername(token);
            int discountId = _shopFacade.addBasicDiscountToShop(shopId, username, basicDiscountDto);
            response.setReturnValue(discountId);
            logger.info("Added basic discount to shop: " + shopId + " with id " + discountId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        
        catch (StockMarketException e) {
            response.setErrorMessage("Failed to add discount to shop: " + e.getMessage());
            logger.log(Level.SEVERE, "Failed to add discount to shop: " + e.getMessage(), e);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
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
    public ResponseEntity<Response> addShopConditionalDiscount(String token, int shopId,
            ConditionalDiscountDto conditionalDiscountDto) {
        Response response = new Response();
        try {
            // check for user validity
            if (!_tokenService.validateToken(token))
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            if (!_tokenService.isUserAndLoggedIn(token)){
                response.setErrorMessage("User is not logged in");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }

            // check validity of input parameters
            if (!_shopFacade.isShopIdExist(shopId)){
                response.setErrorMessage("Shop not found");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }
            if (conditionalDiscountDto.isPrecentage
                    && (conditionalDiscountDto.discountAmount < 0 || conditionalDiscountDto.discountAmount > 100)){
                response.setErrorMessage("Invalid discount amount - precentage should be between 0% and 100%");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }
            if (!conditionalDiscountDto.isPrecentage && conditionalDiscountDto.discountAmount < 0){
                response.setErrorMessage("Invalid discount amount - fixed amount should be positive");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }

            Date currentDate = new Date();
            if (conditionalDiscountDto.expirationDate.before(currentDate)
                    || conditionalDiscountDto.expirationDate.getTime() - currentDate.getTime() < 86400000){
                response.setErrorMessage("Invalid expiration date - should be at least one day into the future");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }

            String username = _tokenService.extractUsername(token);
            int discountId = _shopFacade.addConditionalDiscountToShop(shopId, username, conditionalDiscountDto);
            response.setReturnValue(discountId);
            logger.info("Added conditional discount to shop: " + shopId + " with id " + discountId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

         catch (StockMarketException e) {
            response.setErrorMessage("Failed to add discount to shop: " + e.getMessage());
            logger.log(Level.SEVERE, "Failed to add discount to shop: " + e.getMessage(), e);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Removes a discount from a shop.
     * 
     * @param token      The session token of the user removing the discount.
     * @param shopId     The ID of the shop from which the discount will be removed.
     * @param discountId The ID of the discount to be removed.
     * @return A response indicating the success or failure of the operation.
     */
    public ResponseEntity<Response> removeDiscount(String token, int shopId, int discountId) {
        Response response = new Response();
        try {
            // check for user validity
            if (!_tokenService.validateToken(token))
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            if (!_tokenService.isUserAndLoggedIn(token)){
                response.setErrorMessage("User is not logged in");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }

            // check validity of input parameters
            if (!_shopFacade.isShopIdExist(shopId)){
                response.setErrorMessage("Shop not found");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }

            String username = _tokenService.extractUsername(token);
            _shopFacade.removeDiscountFromShop(shopId, discountId, username);
            response.setReturnValue("Removed discount");
            logger.info("Removed discount from shop: " + shopId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        
        catch (StockMarketException e) {
            response.setErrorMessage("Failed to remove discount from shop: " + e.getMessage());
            logger.log(Level.SEVERE, "Failed to remove discount from shop: " + e.getMessage(), e);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
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
    public ResponseEntity<Response> updateProductQuantity(String token, Integer shopId, Integer productId, Integer productAmount) {
        Response response = new Response();
        try {
            if (!_tokenService.validateToken(token))
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            if (!_tokenService.isUserAndLoggedIn(token)){
                response.setErrorMessage("User is not logged in");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }
            if (!_shopFacade.isShopIdExist(shopId)){
                response.setErrorMessage(String.format("Shop Id: %d not found", shopId));
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }

            String userName = _tokenService.extractUsername(token);
            _shopFacade.updateProductQuantity(userName, shopId, productId, productAmount);
            logger.info(String.format("Update product: %d quantity amont in shop: %d", productId, shopId));
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        
        catch (Exception e) {
            response.setErrorMessage("Failed to add discount to shop: " + e.getMessage());
            logger.log(Level.SEVERE, String.format("Failed to update product: %d quantity to shop: %d . Error: %s",
                    productId, shopId, e.getMessage()), e);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Adds a new owner to a shop.
     * 
     * @param token            The session token of the user performing the update.
     * @param shopId           The ID of the shop where the new owner is being
     *                         added.
     * @param newOwnerUsername The username of the new owner being added to the
     *                         shop.
     * @return A Response object indicating the success or failure of the operation.
     */
    public ResponseEntity<Response> addShopOwner(String token, Integer shopId, String newOwnerUsername) {
        Response response = new Response();
        try {
            if (_tokenService.validateToken(token)) {
                if (_tokenService.isUserAndLoggedIn(token)) {
                    String username = _tokenService.extractUsername(token);
                    if (_userFacade.doesUserExist(username)) {
                        if (_userFacade.doesUserExist(newOwnerUsername)) {
                        _shopFacade.addShopOwner(username, shopId, newOwnerUsername);
                        response.setReturnValue(true);
                        logger.info(String.format("New owner %s added to Shop ID: %d", username, shopId));
                        return new ResponseEntity<>(response, HttpStatus.OK);
                        }
                        else {
                            response.setErrorMessage(String.format("newOwnerUsername %s does not exist.", newOwnerUsername));
                            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
                        }
                    } else {
                        response.setErrorMessage(String.format("User %s does not exist.", username));
                        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
                    }
                } else {
                    response.setErrorMessage("User is not logged in.");
                    return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
                }
            } else {
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }

        } catch (Exception e) {
            response.setErrorMessage(
                    String.format("Failed to add owner %s to shopID %d. Error: %s", newOwnerUsername, shopId,
                            e.getMessage()));
            logger.log(Level.SEVERE, e.getMessage(), e);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Adds a new manager to a shop.
     * 
     * @param token              The session token of the user performing the
     *                           update.
     * @param shopId             The ID of the shop where the new manager is being
     *                           added.
     * @param newManagerUsername The username of the new manager being added to the
     *                           shop.
     * @param permissions        The permissions granted to the new manager.
     * @return A Response object indicating the success or failure of the operation.
     */
    public ResponseEntity<Response> addShopManager(String token, Integer shopId, String newManagerUsername, Set<String> permissions) {
        Response response = new Response();
        try {
            if (_tokenService.validateToken(token)) {
                if (_tokenService.isUserAndLoggedIn(token)) {
                    String username = _tokenService.extractUsername(token);
                    if (_userFacade.doesUserExist(username)) {
                        if (_userFacade.doesUserExist(newManagerUsername)) {
                            _shopFacade.addShopManager(username, shopId, newManagerUsername, permissions);
                            response.setReturnValue(true);
                            logger.info(String.format("New manager %s added to Shop ID: %d", username, shopId));
                            return new ResponseEntity<>(response, HttpStatus.OK);
                        } else {
                            response.setErrorMessage(String.format("newManagerUsername: %s does not exist.", newManagerUsername));
                            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
                        }
                    } else {
                        response.setErrorMessage("User does not exist.");
                        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
                    }
                } else {
                    response.setErrorMessage("User is not logged in.");
                    return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
                }
            } else {
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }

        } catch (Exception e) {
            response.setErrorMessage(
                    String.format("Failed to add manager %s to shopID %d. Error: %s", newManagerUsername, shopId,
                            e.getMessage()));
            logger.log(Level.SEVERE, e.getMessage(), e);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Fires a manager from a shop.
     * 
     * @param token           The session token of the user performing the update.
     * @param shopId          The ID of the shop where the manager is being fired.
     * @param managerUsername The username of the manager being fired from the shop.
     * @return A Response object indicating the success and the set of usernames
     *         fired or failure of the operation.
     */
    public ResponseEntity<Response> fireShopManager(String token, Integer shopId, String managerUsername) {
        Response response = new Response();
        try {
            if (_tokenService.validateToken(token)) {
                if (_tokenService.isUserAndLoggedIn(token)) {
                    String username = _tokenService.extractUsername(token);
                    if (_userFacade.doesUserExist(username)) {
                        Set<String> fired = _shopFacade.fireShopManager(username, shopId, managerUsername);
                        response.setReturnValue(fired);
                        logger.info(String.format("Manager %s fired from Shop ID: %d", managerUsername, shopId));
                        logger.info(String.format("Managers " + fired + " were fired from Shop ID: %d", shopId));
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } else {
                        response.setErrorMessage("User does not exist.");
                        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
                    }
                } else {
                    response.setErrorMessage("User is not logged in.");
                    return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
                }
            } else {
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }

        } catch (Exception e) {
            response.setErrorMessage(
                    String.format("Failed to fire manager %s from shopID %d. Error: ", managerUsername, shopId,
                            e.getMessage()));
            logger.log(Level.SEVERE, e.getMessage(), e);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Resigns from a role in a shop.
     * 
     * @param token  The session token of the user performing the resignation.
     * @param shopId The ID of the shop where the user is resigning from the role.
     * @return A Response object indicating the success and the set of usernames
     *         resigned or failure of the operation.
     */
    public ResponseEntity<Response> resignFromRole(String token, Integer shopId) {
        Response response = new Response();
        try {
            if (_tokenService.validateToken(token)) {
                if (_tokenService.isUserAndLoggedIn(token)) {
                    String username = _tokenService.extractUsername(token);
                    if (_userFacade.doesUserExist(username)) {
                        Set<String> resigned = _shopFacade.resignFromRole(username, shopId);
                        response.setReturnValue(true);
                        logger.info(String.format("User %s resigned from Shop ID: %d", username, shopId));
                        logger.info(String.format("Subordinates " + resigned + " resigned too from Shop ID: %d",
                                username, shopId));
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } else {
                        response.setErrorMessage("User does not exist.");
                        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
                    }
                } else {
                    response.setErrorMessage("User is not logged in.");
                    return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
                }
            } else {
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }

        } catch (Exception e) {
            response.setErrorMessage(
                    String.format("Failed to resign from shopID %d. Error: ", shopId, e.getMessage()));
            logger.log(Level.SEVERE, e.getMessage(), e);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Modifies the permissions of a manager in a shop.
     * 
     * @param token           The session token of the user performing the
     *                        modification.
     * @param shopId          The ID of the shop where the manager's permissions are
     *                        being modified.
     * @param managerUsername The username of the manager whose permissions are
     *                        being modified.
     * @param permissions     The new set of permissions for the manager.
     * @return A Response object indicating the success or failure of the operation.
     */
    public ResponseEntity<Response> modifyManagerPermissions(String token, Integer shopId, String managerUsername, Set<String> permissions) {
        Response response = new Response();
        try {
            if (_tokenService.validateToken(token)) {
                if (_tokenService.isUserAndLoggedIn(token)) {
                    String username = _tokenService.extractUsername(token);
                    if (_userFacade.doesUserExist(username)) {
                        _shopFacade.modifyManagerPermissions(username, shopId, managerUsername, permissions);
                        response.setReturnValue(true);
                        logger.info(String.format("Manager %s permissions modified in Shop ID: %d", managerUsername,
                                shopId));
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } else {
                        response.setErrorMessage("User does not exist.");
                        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
                    }
                } else {
                    response.setErrorMessage("User is not logged in.");
                    return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
                }
            } else {
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }

        } catch (Exception e) {
            response.setErrorMessage(
                    String.format("Failed to modify manager %s permissions in shopID %d. Error: %s", managerUsername,
                            shopId, e.getMessage()));
            logger.log(Level.SEVERE, e.getMessage(), e);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Shows the shop policy information.
     * 
     * @param token  The session token of the user performing the operation.
     * @param shopId The ID of the desired shop.
     * @return A response containing the shop policy information.
     */
    public ResponseEntity<Response> displayShopPolicyInfo(String token, Integer shopId) {
        Response response = new Response();
        try {
            if (_tokenService.validateToken(token)) {
                String policy = _shopFacade.getShopPolicyInfo(shopId);
                if (policy != null && policy.length() > 0) {
                    response.setReturnValue(
                            String.format("Shop policy information: \n Shop ID: %d, \n Policy: %s", shopId, policy));
                    logger.info(String.format("Shop policy information for shop ID %d is displayed", shopId));
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                    response.setReturnValue(
                            String.format("Shop policy information for shop ID %d was not found", shopId));
                    logger.info(String.format("Shop policy information for shop ID %d was not found", shopId));
                    return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
                }
            } else {
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            response.setErrorMessage(
                    String.format(String.format("Failed to display shop policy information for shop ID %d . Error:",
                            shopId, e.getMessage())));
            logger.log(Level.SEVERE, e.getMessage(), e);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Shows the product policy information.
     * 
     * @param token     The session token of the user performing the operation.
     * @param shopId    The ID of the shop where the product is located.
     * @param productId The ID of the product.
     * @return A response containing the product policy information.
     */
    public ResponseEntity<Response> displayProductPolicyInfo(String token, Integer shopId, Integer productId) {
        // TODO: Decide on correct way to implement - Objects(discounts) or Strings(Policy)
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
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                    response.setReturnValue(
                            String.format("Product policy information for product ID %d, of shop ID %d was not found",
                                    productId, shopId));
                    logger.info(
                            String.format("Product policy information for product ID %d, of shop ID %d was not found",
                                    productId, shopId));
                    return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
                }
            } else {
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            response.setErrorMessage(
                    String.format(String.format(
                            "Failed to display product policy information for product: %d in shop: %d . Error:",
                            productId, shopId, e.getMessage())));
            logger.log(Level.SEVERE, e.getMessage(), e);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Shows the shop discounts information.
     * 
     * @param token  The session token of the user performing the operation.
     * @param shopId The ID of the desired shop.
     * @return A response containing the shop discounts information.
     */
    public ResponseEntity<Response> displayShopDiscountsInfo(String token, Integer shopId) {
        Response response = new Response();
        try {
            if (_tokenService.validateToken(token)) {

                String discounts = _shopFacade.getShopDiscountsInfo(shopId);
                if (discounts != null && discounts.length() > 0) {
                    response.setReturnValue(String
                            .format("Shop discounts information: \n Shop ID: %d, \n Discounts: %s", shopId, discounts));
                    logger.info(String.format("Shop discounts information for shop ID %d is displayed", shopId));
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                    response.setReturnValue(
                            String.format("Shop discounts information for shop ID %d was not found", shopId));
                    logger.info(String.format("Shop discounts information for shop ID %d was not found", shopId));
                    return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
                }
            } else {
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            response.setErrorMessage(
                    String.format(String.format("Failed to display shop discounts information for shop ID %d . Error:",
                            shopId, e.getMessage())));
            logger.log(Level.SEVERE, e.getMessage(), e);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Shows the product discounts information.
     * 
     * @param token     The session token of the user performing the operation.
     * @param shopId    The ID of the shop where the product is located.
     * @param productId The ID of the product.
     * @return A response containing the product discounts information.
     */
    public ResponseEntity<Response> displayProductDiscountsInfo(String token, Integer shopId, Integer productId) {
        // TODO: Decide on correct way to implement - Objects(discounts) or Strings(Policy)
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
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                    response.setReturnValue(String.format(
                            "Product discounts information for product ID %d, of shop ID %d was not found", productId,
                            shopId));
                    logger.info(String.format(
                            "Product discounts information for product ID %d, of shop ID %d was not found", productId,
                            shopId));
                    return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
                }
            } else {
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            response.setErrorMessage(
                    String.format(String.format(
                            "Failed to display product discounts information for product: %d in shop: %d . Error:",
                            productId, shopId, e.getMessage())));
            logger.log(Level.SEVERE, e.getMessage(), e);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Shows the shop general information.
     * 
     * @param token  The session token of the user performing the operation.
     * @param shopId The ID of the desired shop.
     * @return A response containing the shop General information.
     */
    public ResponseEntity<Response> displayShopGeneralInfo(String token, Integer shopId) {
        Response response = new Response();
        try {
            if (_tokenService.validateToken(token)) {
                if (_tokenService.isUserAndLoggedIn(token)) {
                    String username = _tokenService.extractUsername(token);
                    if (_userFacade.doesUserExist(username)) {
                        String info = _shopFacade.getShopGeneralInfo(shopId);
                        if (info != null && info.length() > 0) {
                            response.setReturnValue(String.format(
                                    "Shop general information: \n Shop ID: %d, \n General information: %s", shopId, info));
                            logger.info(String.format("Shop general information for shop ID %d is displayed", shopId));
                            return new ResponseEntity<>(response, HttpStatus.OK);
                        } else {
                            response.setReturnValue(
                                    String.format("Shop general information for shop ID %d was not found", shopId));
                            logger.info(String.format("Shop general information for shop ID %d was not found", shopId));
                            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
                        }
                    } else {
                        response.setErrorMessage(String.format("User name %s does not exist.", username));
                        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
                    }
                } else {
                    response.setErrorMessage(String.format("User is not logged in."));
                    return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
                }
            } else {
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            response.setErrorMessage(
                    String.format(String.format("Failed to display shop general information for shop ID %d . Error: %s",
                            shopId, e.getMessage())));
            logger.log(Level.SEVERE, e.getMessage(), e);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Shows the product general information.
     * 
     * @param token     The session token of the user performing the operation.
     * @param shopId    The ID of the shop where the product is located.
     * @param productId The ID of the product.
     * @return A response containing the product general information.
     */
    public ResponseEntity<Response> displayProductGeneralInfo(String token, Integer shopId, Integer productId) {
        // TODO: Decide on correct way to implement - Objects(discounts) or Strings(Policy)
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
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                    response.setReturnValue(
                            String.format("Product general information for product ID %d, of shop ID %d was not found",
                                    productId, shopId));
                    logger.info(
                            String.format("Product general information for product ID %d, of shop ID %d was not found",
                                    productId, shopId));
                    return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
                }
            } else {
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            response.setErrorMessage(
                    String.format(String.format(
                            "Failed to display product general information for product: %d in shop: %d . Error:",
                            productId, shopId, e.getMessage())));
            logger.log(Level.SEVERE, e.getMessage(), e);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    
    /**
     * Adds a rating to a product in a shop.
     * @param token
     * @param shopId
     * @param productId
     * @param rating
     * @return
     */
    public ResponseEntity<Response> addProductRating(String token, Integer shopId, Integer productId, Integer rating) {
        Response response = new Response();
        try {
            logger.log(Level.SEVERE,String.format("ShopService::addProductRating entring"));
            if (!_tokenService.validateToken(token)) 
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            if (!_tokenService.isUserAndLoggedIn(token)){
                response.setErrorMessage("User is not logged in.");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }

            String username = _tokenService.extractUsername(token);

            if (!_userFacade.doesUserExist(username)){
                response.setErrorMessage(String.format("User does not exist.",username));
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }

            _shopFacade.addProductRating(shopId, productId, rating);
            response.setReturnValue(String.format("Success to add rating to productID: %d in ShopID: %d .", productId,shopId));
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        catch(StockMarketException e){
            logger.log(Level.INFO, e.getMessage(), e);
            response.setErrorMessage(String.format(
                "ShopService::addProductRating failed to rate productId: %d in ShopId: %d with error %s", 
                productId, shopId, e.getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Adds a rating to a shop.
     * @param token
     * @param shopId
     * @param rating
     * @return
     */
    public ResponseEntity<Response> addShopRating(String token, Integer shopId, Integer rating) {
        Response response = new Response();
        try {
            logger.log(Level.SEVERE,String.format("ShopService::addShopRating entring"));
            if (!_tokenService.validateToken(token)) 
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            if (!_tokenService.isUserAndLoggedIn(token)){
                response.setErrorMessage("User is not logged in.");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }

            String username = _tokenService.extractUsername(token);

            if (!_userFacade.doesUserExist(username)){
                response.setErrorMessage(String.format("User does not exist.",username));
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }

            _shopFacade.addShopRating(shopId, rating);
            response.setReturnValue(String.format("Success to add rating to ShopID: %d .",shopId));
                return new ResponseEntity<>(response, HttpStatus.OK);
        }

        catch(StockMarketException e){
            logger.log(Level.INFO, e.getMessage(), e);
            response.setErrorMessage(String.format(
                "ShopService::addShopRating failed to rate ShopId: %d with error %s", shopId, e.getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * searches products by their name.
     * 
     * @param token       The session token of the user performing the search.
     * @param shopId      The ID of the shop to search 
     * @return A response indicating the success of the operation, containing a dictionary of shopDTO and ProductDTOs, or indicating failure.
     */
    public ResponseEntity<Response> searchAndDisplayShopByID(String token, Integer shopId) {
        Response response = new Response();
        try {
            if (_tokenService.validateToken(token)) {
                if (_shopFacade.isShopIdExist(shopId)) {
                    Map <ShopDto, List<ProductDto>> shopProductMapForResponse = new HashMap<>();
                    ShopDto shopDto = new ShopDto(_shopFacade.getShopName(shopId), _shopFacade.getShopBankDetails(shopId), _shopFacade.getShopAddress(shopId));
                    List<Product> products = _shopFacade.getAllProductsInShopByID(shopId);
                    if (products != null && !products.isEmpty()) {
                        List<ProductDto> productDtoList = new ArrayList<>();
                        for (Product product: products) {
                            ProductDto productDto = new ProductDto(product);
                            productDtoList.add(productDto);
                        }
                        shopProductMapForResponse.put(shopDto, productDtoList);
                        response.setReturnValue(shopProductMapForResponse);
                        logger.info(String.format("Shop with ID %s was found and all it's products were returned", shopId.toString()));
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } else {
                        response.setReturnValue(
                                String.format("Shop with ID %s was found but it contains no products", shopId.toString()));
                        logger.info(String.format("Shop with ID %s was found but it contains no products", shopId.toString()));
                        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
                    }
                }
                else {
                    response.setReturnValue(
                                String.format("Shop with ID %s doesn't exist", shopId.toString()));
                    logger.info(String.format("Shop with ID %s doesn't exist", shopId.toString()));
                    return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
                }
            }
            else {
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            response.setErrorMessage(String.format(String.format("Failed to search shop with ID %s . Error:",
                    shopId.toString(), e.getMessage())));
            logger.log(Level.SEVERE, e.getMessage(), e);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


        /**
     * searches products by their name.
     * 
     * @param token       The session token of the user performing the search.
     * @param shopName      The Name of the shop to search 
     * @return A response indicating the success of the operation, containing a dictionary of shopDTO and ProductDTOs, or indicating failure.
     */
    public ResponseEntity<Response> searchAndDisplayShopByName(String token, String shopName) {
        Response response = new Response();
        try {
            if (_tokenService.validateToken(token)) {
                Map <ShopDto, List<ProductDto>> shopProductMapForResponse = new HashMap<>();
                List<Integer> shopIds = _shopFacade.getShopIdsByName(shopName);
                if (!shopIds.isEmpty() && shopIds != null) {
                    for (Integer shopId: shopIds) {
                        ShopDto shopDto = new ShopDto(_shopFacade.getShopName(shopId), _shopFacade.getShopBankDetails(shopId), _shopFacade.getShopAddress(shopId));
                        List<Product> products = _shopFacade.getAllProductsInShopByID(shopId);
                        List<ProductDto> productDtoList = new ArrayList<>();
                        if (products != null && !products.isEmpty()) {
                            for (Product product: products) {
                                ProductDto productDto = new ProductDto(product);
                                productDtoList.add(productDto);
                            }
                        }
                        shopProductMapForResponse.put(shopDto, productDtoList);
                    }
                    response.setReturnValue(shopProductMapForResponse);
                    logger.info(String.format("Shops with Name %s were found and all their products were returned", shopName));
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
                else {
                    response.setReturnValue(
                        String.format("Shops with name %s don't exist", shopName));
                    logger.info(String.format("Shop with name %s don't exist", shopName));
                    return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
                }
            }
            else {
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            response.setErrorMessage(String.format(String.format("Failed to search shop with name %s . Error:",
                    shopName, e.getMessage())));
            logger.log(Level.SEVERE, e.getMessage(), e);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Receive the shops which the user has roles in.
     * @param token the users session token
     * @return the shops which the user has roles in.
     */
    public ResponseEntity<Response> getUserShops(String token) {
        Response response = new Response();
        try {
            logger.log(Level.SEVERE,String.format("ShopService::getUserShops entring"));
            if (!_tokenService.validateToken(token)) 
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            if (!_tokenService.isUserAndLoggedIn(token)){
                response.setErrorMessage("User is not logged in.");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }

            String username = _tokenService.extractUsername(token);

            if (!_userFacade.doesUserExist(username)){
                response.setErrorMessage(String.format("User does not exist.",username));
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }

            List<Integer> shopsIds = _shopFacade.getUserShops(username);
            response.setReturnValue(shopsIds);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        catch(StockMarketException e){
            logger.log(Level.INFO, e.getMessage(), e);
            response.setErrorMessage(String.format( "ShopService::getUserShops failed to get users shops. "+e.getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Receive the shops which the user has roles in.
     * @param token the users session token
     * @return the shops which the user has roles in.
     */
    public ResponseEntity<Response> changeShopPolicy(String token, int shopId, List<ShoppingBasketRuleDto> shopRules) {
        Response response = new Response();
        try {
            if (_tokenService.validateToken(token)) {
                String username = _tokenService.extractUsername(token);
                if (_userFacade.doesUserExist(username)) {
                    _shopFacade.changeShopPolicy(username, shopId, shopRules);
                    response.setReturnValue(true);
                    logger.info(String.format("Shop policy for shop ID %d was changed", shopId));
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                    response.setErrorMessage(String.format("User name %s does not exist.", username));
                    return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
                }
            } else {
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            response.setErrorMessage(
                    String.format("Failed to change shop policy for shop ID %d. Error: %s", shopId, e.getMessage()));
            logger.log(Level.SEVERE, e.getMessage(), e);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Receive the shops which the user has roles in.
     * @param token the users session token
     * @return the shops which the user has roles in.
     */
    public ResponseEntity<Response> getShopManagerPermissions(String token, Integer shopId) {
        Response response = new Response();
        try {
            if (_tokenService.validateToken(token)) {
                String username = _tokenService.extractUsername(token);
                if (_userFacade.doesUserExist(username)) {
                    if (!_tokenService.isUserAndLoggedIn(token)){
                        response.setErrorMessage("User is not logged in.");
                        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
                    }
                    List<String> permissions = _shopFacade.getShopManagerPermissions(username, shopId);
                    response.setReturnValue(permissions);
                    logger.info(String.format("Recieved successfuly shop permissions of shop with id ", shopId));
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                    response.setErrorMessage(String.format("User name %s does not exist.", username));
                    return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
                }
            } else {
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            response.setErrorMessage(
                    String.format("Failed to load permissions for user of shop with id %d. Error: %s", shopId, e.getMessage()));
            logger.log(Level.SEVERE, e.getMessage(), e);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Receive the shops names which the user has roles in.
     * @param token the users session token
     * @return the shops names which the user has roles in.
     */
    public ResponseEntity<Response> getUserShopsNames(String token) {
        Response response = new Response();
        try {
            logger.log(Level.SEVERE,String.format("ShopService::getUserShopsNames entring"));
            if (!_tokenService.validateToken(token)) 
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            if (!_tokenService.isUserAndLoggedIn(token)){
                response.setErrorMessage("User is not logged in.");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }

            String username = _tokenService.extractUsername(token);

            if (!_userFacade.doesUserExist(username)){
                response.setErrorMessage(String.format("User does not exist.",username));
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }

            List<String> shopsNames = _shopFacade.getUserShopsNames(username);
            response.setReturnValue(shopsNames);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        catch(StockMarketException e){
            logger.log(Level.INFO, e.getMessage(), e);
            response.setErrorMessage(String.format( "ShopService::getUserShopsNames failed to get users shops. "+e.getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    
}