package Server;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import Dtos.BasicDiscountDto;
import Dtos.ConditionalDiscountDto;
import Dtos.ShopDto;
import ServiceLayer.Response;
import ServiceLayer.ShopService;

@RestController
@RequestMapping(path = "/api/shop")
public class ShopController {
    private final ShopService _shopService;

    @Autowired
    public ShopController(ShopService shopService) {
        this._shopService = shopService;
    }

    @PostMapping("/openNewShop")
    public Response openNewShop( @RequestBody ShopDto shopDto,
            @RequestHeader(value = "Authorization") String token) {
        // example request:
        // http://localhost:8080/api/user/register?shopName=test&bankDetails=test&shopAddress=test
        System.out.println("Client: token is: " + token);
        Response resp =_shopService.openNewShop(token, shopDto);
        return resp;
    }

    @GetMapping("/closeShop")
    public Response closeShop(@RequestHeader("Authorization") String token, @RequestParam Integer shopId) {
        return _shopService.closeShop(token, shopId);
    }

    @GetMapping("/reopenShop")
    public Response reopenShop(@RequestHeader("Authorization") String token, @RequestParam Integer shopId) {
        return _shopService.reOpenShop(token, shopId);
    }

    @GetMapping("/searchProductInShopByName")
    public Response searchProductInShopByName(@RequestHeader("Authorization") String token,
            @RequestParam(required = false) Integer shopId,
            @RequestParam String query) {
        return _shopService.searchProductInShopByName(token, shopId, query);
    }

    // TODO: uncomment this after merging Or's changes (category enum)
    // @GetMapping("/searchProductInShopByCategory")
    // public Response searchByCategory(@RequestHeader("Authorization") String
    // token,
    // @RequestParam(required = false) Integer shopId,
    // @RequestParam Category category) {
    // return _shopService.searchProductInShopByCategory(token, shopId, category);
    // }

    @GetMapping("/searchProductsInShopByKeywords")
    public Response searchProducstInShopByKeywords(@RequestHeader("Authorization") String token,
            @RequestParam(required = false) Integer shopId,
            @RequestParam List<String> keywords) {
        return _shopService.searchProductsInShopByKeywords(token, shopId, keywords);
    }

    @GetMapping("/searchProductsInShopByPriceRange")
    public Response searchProductsInShopByPriceRange(@RequestHeader("Authorization") String token,
            @RequestParam(required = false) Integer shopId,
            @RequestParam Double minPrice,
            @RequestParam Double maxPrice) {
        return _shopService.searchProductsInShopByPriceRange(token, shopId, minPrice, maxPrice);
    }

    @GetMapping("/getShopPurchaseHistory")
    public Response getShopPurchaseHistory(@RequestHeader("Authorization") String token, @RequestParam Integer shopId) {
        return _shopService.getShopPurchaseHistory(token, shopId);
    }

    @PostMapping("/addShopBasicDiscount")
    public Response addShopBasicDiscount(@RequestHeader("Authorization") String token, @RequestParam Integer shopId,
            @RequestBody BasicDiscountDto discountDto) {
        return _shopService.addShopBasicDiscount(token, shopId, discountDto);
    }

    @PostMapping("/addShopConditionalDiscount")
    public Response addShopConditionalDiscount(@RequestHeader("Authorization") String token,
            @RequestParam Integer shopId,
            @RequestBody ConditionalDiscountDto discountDto) {
        return _shopService.addShopConditionalDiscount(token, shopId, discountDto);
    }

    @GetMapping("/removeShopDiscount")
    public Response removeShopDiscount(@RequestHeader("Authorization") String token, @RequestParam Integer shopId,
            @RequestParam Integer discountId) {
        return _shopService.removeDiscount(token, shopId, discountId);
    }

    @PostMapping("/updateProductQuantity")
    public Response updateProductQuantity(@RequestHeader("Authorization") String token, @RequestParam Integer shopId,
            @RequestParam Integer productId, @RequestParam Integer quantity) {
        return _shopService.updateProductQuantity(token, shopId, productId, quantity);
    }

    @PostMapping("/addShopOwner")
    public Response addShopOwner(@RequestHeader("Authorization") String token, @RequestBody Map<String, Object> body,
            @RequestParam Integer shopId, @RequestParam String newOwnerUsername) {
        return _shopService.addShopOwner(token, shopId, newOwnerUsername);
    }

    @PostMapping("/addShopManager")
    public Response addShopManager(@RequestHeader("Authorization") String token,
            @RequestParam Integer shopId,
            @RequestParam String newManagerUsername,
            @RequestBody Set<String> permissions) {
        return _shopService.addShopManager(token, shopId, newManagerUsername, permissions);
    }

    @PostMapping("/fireShopManager")
    public Response fireShopManager(@RequestHeader("Authorization") String token, @RequestParam Integer shopId,
            @RequestParam String managerUsername) {
        return _shopService.fireShopManager(token, shopId, managerUsername);
    }

    @PostMapping("/resignFromRole")
    public Response resignFromRole(@RequestHeader("Authorization") String token, @RequestParam Integer shopId) {
        return _shopService.resignFromRole(token, shopId);
    }

    @PostMapping("/modifyManagerPermissions")
    public Response modifyManagerPermissions(@RequestHeader("Authorization") String token, @RequestParam Integer shopId,
            @RequestParam String managerUsername, @RequestBody Set<String> permissions) {
        return _shopService.modifyManagerPermissions(token, shopId, managerUsername, permissions);
    }

    @GetMapping("/displayShopPolicyInfo")
    public Response displayShopPolicyInfo(@RequestHeader("Authorization") String token, @RequestParam Integer shopId) {
        return _shopService.displayShopPolicyInfo(token, shopId);
    }

    @GetMapping("/displayProductPolicyInfo")
    public Response displayProductPolicyInfo(@RequestHeader("Authorization") String token, @RequestParam Integer shopId,
            @RequestParam Integer productId) {
        return _shopService.displayProductPolicyInfo(token, shopId, productId);
    }

    @GetMapping("/displayShopDiscountsInfo")
    public Response displayShopDiscountsInfo(@RequestHeader("Authorization") String token,
            @RequestParam Integer shopId) {
        return _shopService.displayShopDiscountsInfo(token, shopId);
    }

    @GetMapping("/displayProductDiscountsInfo")
    public Response displayProductDiscountsInfo(@RequestHeader("Authorization") String token,
            @RequestParam Integer shopId, @RequestParam Integer productId) {
        return _shopService.displayProductDiscountsInfo(token, shopId, productId);
    }

    @GetMapping("/displayShopGeneralInfo")
    public Response displayShopGeneralInfo(@RequestHeader("Authorization") String token, @RequestParam Integer shopId) {
        return _shopService.displayShopGeneralInfo(token, shopId);
    }

    @GetMapping("/displayProductGeneralInfo")
    public Response displayProductGeneralInfo(@RequestHeader("Authorization") String token,
            @RequestParam Integer shopId, @RequestParam Integer productId) {
        return _shopService.displayProductGeneralInfo(token, shopId, productId);
    }

    @GetMapping("/getUserShops")
    public Response getUserShops(@RequestHeader("Authorization") String token) {
        return _shopService.getUserShops(token);
    }
}
