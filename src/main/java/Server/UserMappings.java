package Server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/register")
    public Response register(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String email,
            @RequestParam String birthYear,
            @RequestParam String birthMonth,
            @RequestParam String birthDay,
            @RequestHeader(value = "Authorization", required = false) String token) {
        // example request:
        // http://localhost:8080/api/user/register?username=test&password=test&email=test
        Response resp = _userService.register(token, username, password, email, birthYear, birthMonth, birthDay);
        return resp;
    }

    @GetMapping("/login")
    public Response login(
            @RequestParam String username,
            @RequestParam String password, 
            @RequestHeader(value = "Authorization", required = false) String token) {
        // example request:
        // http://localhost:8080/api/user/login?username=test&password=test
        Response resp = _userService.logIn(token, username, password);
        return resp;
    }

    @GetMapping("/logout")
    public Response logout(@RequestHeader(value = "Authorization", required = false) String token) {
        // example request:
        // "http://localhost:8080/api/user/logout" -H "Authorization: Bearer user_token_here"
        Response resp = _userService.logOut(token);
        return resp;
    }
}
