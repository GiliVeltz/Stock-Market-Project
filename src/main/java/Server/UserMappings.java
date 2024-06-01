package Server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import Dtos.PurchaseCartDetailsDto;
import Dtos.UserDto;
import ServiceLayer.*;

// Purpose: This class is mapping the user requests to the service layer functions.
@RestController
@RequestMapping(path = "/api/user")
public class UserMappings {
    private final UserService _userService;

    @Autowired
    public UserMappings(UserService userService) {
        this._userService = userService;
    }

    @PostMapping("/register")
    public Response register(
            @RequestBody UserDto userDto,
            @RequestHeader(value = "Authorization") String token) {
        // example request:
        // http://localhost:8080/api/user/register?username=test&password=test&email=test
        Response resp = _userService.register(token, userDto);
        return resp;
    }

    @GetMapping("/login")
    public Response login(
            @RequestParam String username,
            @RequestParam String password,
            @RequestHeader(value = "Authorization") String token) {
        // example request:
        // http://localhost:8080/api/user/login?username=test&password=test
        Response resp = _userService.logIn(token, username, password);
        return resp;
    }

    @GetMapping("/logout")
    public Response logout(@RequestHeader(value = "Authorization") String token) {
        // example request:
        // "http://localhost:8080/api/user/logout" -H "Authorization: user_token_here"
        Response resp = _userService.logOut(token);
        return resp;
    }

    @PostMapping("/purchaseCart")
    public Response purchaseCart(@RequestHeader(value = "Authorization") String token,
            @RequestBody(required = false) PurchaseCartDetailsDto details) {
        // example request:
        // "http://localhost:8080/api/user/purchaseCart" -H "
        // Authorization: user_token_here"
        Response resp = _userService.purchaseCart(token, details);
        return resp;
    }

    @GetMapping("/isSystemAdmin")
    public Response isSystemAdmin(@RequestHeader(value = "Authorization") String token) {
        // example request:
        // "http://localhost:8080/api/user/isSystemAdmin" -H "Authorization":
        // user_token_here"
        Response resp = _userService.isSystemAdmin(token);
        return resp;
    }

    @GetMapping("/getUserPurchaseHistory")
    public Response getUserPurchaseHistory(@RequestHeader(value = "Authorization") String token,
            @RequestParam String username) {
        // example request:
        // "http://localhost:8080/api/user/getUserPurchaseHistory" -H "Authorization":
        // user_token_here"
        Response resp = _userService.getUserPurchaseHistory(token, username);
        return resp;
    }

    @GetMapping("/getPersonalPurchaseHistory")
    public Response getPersonalPurchaseHistory(@RequestHeader(value = "Authorization") String token) {
        // example request:
        // "http://localhost:8080/api/user/getPersonalPurchaseHistory" -H
        // "Authorization":
        // user_token_here"
        Response resp = _userService.getPersonalPurchaseHistory(token);
        return resp;
    }

    @GetMapping("/addProductToShoppingCart")
    public Response addProductToShoppingCart(@RequestHeader(value = "Authorization") String token,
            @RequestParam int productID, @RequestParam int shopID) {
        // example request:
        // "http://localhost:8080/api/user/addProductToShoppingCart?productID=1&shopID=1"
        // -H "Authorization": user_token_here"
        Response resp = _userService.addProductToShoppingCart(token, productID, shopID);
        return resp;
    }

    @GetMapping("/removeProductFromShoppingCart")
    public Response removeProductFromShoppingCart(@RequestHeader(value = "Authorization") String token,
            @RequestParam int productID, @RequestParam int shopID) {
        // example request:
        // "http://localhost:8080/api/user/removeProductFromShoppingCart?productID=1&shopID=1"
        // -H "Authorization": user_token_here"
        Response resp = _userService.removeProductFromShoppingCart(token, productID, shopID);
        return resp;
    }
}
