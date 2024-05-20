package ServiceLayer;

import javax.xml.ws.Response;

public class UserService {
    private UserController userController;
    private TokenService tokenService;

    public UserService(){
        userController = new UserController();
        tokenService = new TokenService();
    }

    public Response logIn(String userName, String password) throws Exception {
        Response response = new Response();
        try {
            userController.logIn(userName, password);
            response.setReturnValue(tokenService.generateToken(userName));
        } catch (Exception e) {
            response.setErrorMessage("Login failed: " + e.getMessage());
        }
        return response;
    }

    public Response logOut(String token, String user_name, String password){
        Response response = new Response();
        try {
            if (tokenService.validateToken(token)){
                userController.logOut(user_name);
                response.setReturnValue("Login Succeed");
            } else {
                response.setErrorMessage("Token is incorrect");
            }
        } catch (Exception e) {
            response.setErrorMessage("Login failed: " + e.getMessage());
        }
        return response;
    }

    public Response register(String user_name, String password){
        Response response = new Response();
        try {
            userController.register(user_name, password);
            response.setReturnValue("Registeration Succeed");
        } catch (Exception e) {
            response.setErrorMessage("Registeration failed: " + e.getMessage());
        }
    }
}
