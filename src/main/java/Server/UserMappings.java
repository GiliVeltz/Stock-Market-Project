package Server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ServiceLayer.*;

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
            @RequestParam String email) {
        // example request:
        // http://localhost:8080/api/user/register?username=test&password=test&email=test
        Response resp = _userService.register(username, password, email);
        return resp;
    }

    @GetMapping("/login")
    public Response login(
            @RequestParam String username,
            @RequestParam String password) {
        // example request:
        // http://localhost:8080/api/user/login?username=test&password=test
        Response resp = _userService.logIn(username, password);
        return resp;
    }

    @GetMapping("/logout")
    public Response logout(@RequestParam String token) {
        // example request:
        // http://localhost:8080/api/user/logout?token=user_token_here
        Response resp = _userService.logOut(token);
        return resp;
    }
}
