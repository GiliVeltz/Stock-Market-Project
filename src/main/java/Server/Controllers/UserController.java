package Server.Controllers;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
@SuppressWarnings("rawtypes")
@RequestMapping(path = "/api/user")
public class UserController {
    private final UserService _userService;

    @Autowired
    public UserController(UserService userService) {
        this._userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<Response> register(
            @RequestBody UserDto userDto,
            @RequestHeader(value = "Authorization") String token) {
        return _userService.register(token, userDto);
    }

    @PostMapping("/login")
    public ResponseEntity<Response> login(
            @RequestParam String username,
            @RequestParam String password,
            @RequestHeader(value = "Authorization") String token) {
        return _userService.logIn(token, username, password);
    }

    @PostMapping("/logout")
    public ResponseEntity<Response> logout(@RequestHeader(value = "Authorization") String token) {
        return _userService.logOut(token);
    }

    @GetMapping("/viewShoppingCart")
    public ResponseEntity<Response> viewShoppingCart(
            @RequestParam String username,
            @RequestHeader(value = "Authorization") String token) {
        return _userService.viewShoppingCart(token, username);
    }

    @GetMapping("/viewOrderHistory")
    public ResponseEntity<Response> viewOrderHistory(
            @RequestParam String username,
            @RequestHeader(value = "Authorization") String token) {
        return _userService.viewOrderHistory(token, username);
    }

    @PostMapping("/purchaseCart")
    public ResponseEntity<Response> purchaseCart(@RequestHeader(value = "Authorization") String token,
            @RequestBody(required = false) PurchaseCartDetailsDto details) {
        // example request:
        // "http://localhost:8080/api/user/purchaseCart" -H "
        // Authorization: user_token_here"
        ResponseEntity<Response> resp = _userService.purchaseCart(token, details);
        return resp;
    }

    @GetMapping("/isSystemAdmin")
    public ResponseEntity<Response> isSystemAdmin(@RequestHeader(value = "Authorization") String token) {
        // example request:
        // "http://localhost:8080/api/user/isSystemAdmin" -H "Authorization":
        // user_token_here"
        ResponseEntity<Response> resp = _userService.isSystemAdmin(token);
        return resp;
    }

    @GetMapping("/getUserPurchaseHistory")
    public ResponseEntity<Response> getUserPurchaseHistory(@RequestHeader(value = "Authorization") String token,
            @RequestParam String username) {
        // example request:
        // "http://localhost:8080/api/user/getUserPurchaseHistory" -H "Authorization":
        // user_token_here"
        ResponseEntity<Response> resp = _userService.getUserPurchaseHistory(token, username);
        return resp;
    }

    @GetMapping("/getUserDetails")
    public ResponseEntity<Response> getUserDetails(@RequestHeader(value = "Authorization") String token) {
        // example request:
        // "http://localhost:8080/api/user/getUserDetails" -H "Authorization":
        // user_token_here"
        ResponseEntity<Response> resp = _userService.getUserDetails(token);
        return resp;
    }

    @PostMapping("/setUserDetails")
    public ResponseEntity<Response> setUserDetails(@RequestBody UserDto userDto,
                                                @RequestHeader(value = "Authorization") String token) {
        ResponseEntity<Response> resp = _userService.setUserDetails(token, userDto);
        return resp;
    }

    @GetMapping("/getPersonalPurchaseHistory")
    public ResponseEntity<Response> getPersonalPurchaseHistory(@RequestHeader(value = "Authorization") String token) {
        // example request:
        // "http://localhost:8080/api/user/getPersonalPurchaseHistory" -H
        // "Authorization":
        // user_token_here"
        ResponseEntity<Response> resp = _userService.getPersonalPurchaseHistory(token);
        return resp;
    }

    @PostMapping("/addProductToShoppingCart")
    public ResponseEntity<Response> addProductToShoppingCart(@RequestHeader(value = "Authorization") String token,
            @RequestParam int productID, @RequestParam int shopID) {
        // example request:
        // "http://localhost:8080/api/user/addProductToShoppingCart?productID=1&shopID=1"
        // -H "Authorization": user_token_here"
        ResponseEntity<Response> resp = _userService.addProductToShoppingCart(token, productID, shopID);
        return resp;
    }

    @PostMapping("/removeProductFromShoppingCart")
    public ResponseEntity<Response> removeProductFromShoppingCart(@RequestHeader(value = "Authorization") String token,
            @RequestParam int productID, @RequestParam int shopID) {
        // example request:
        // "http://localhost:8080/api/user/removeProductFromShoppingCart?productID=1&shopID=1"
        // -H "Authorization": user_token_here"
        ResponseEntity<Response> resp = _userService.removeProductFromShoppingCart(token, productID, shopID);
        return resp;
    }

     @GetMapping("/reportToAdmin")
    public ResponseEntity<Response> reportToAdmin(@RequestHeader("Authorization") String token,@RequestParam String message) {
       
        String decodedString = "";
    try {
        decodedString = URLDecoder.decode(message, StandardCharsets.UTF_8.toString());
          
    } catch (Exception e) {
        // Handle exception (e.g., UnsupportedEncodingException, which should not happen for UTF-8)
        e.printStackTrace();
    }
        ResponseEntity<Response> resp = _userService.reportToAdmin(token,decodedString);
        return resp;
    }
}
