package ServiceLayer;

import Domain.UserController;

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
            response.setErrorMessage("LogIn failed: " + e.getMessage());
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
            response.setErrorMessage("LogOut failed: " + e.getMessage());
        }
        return response;
    }

    public Response register(String user_name, String password, String email){
        Response response = new Response();
        try {
            userController.register(user_name, password, email);
            response.setReturnValue("Registeration Succeed");
        } catch (Exception e) {
            response.setErrorMessage("Registeration failed: " + e.getMessage());
        }
        return response;
    }
}
