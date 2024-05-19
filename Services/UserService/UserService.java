public class UserService {
    private UserController userController;
    private TokenService tokenService;

    public UserService(){
        userController = new UserController();
        tokenService = new TokenService();
    }

    public String logIn(String user_name, String password){
        if (userController.logIn(user_name, password)){
            return tokenService.generateToken(user_name);
        }
    }

    public boolean logOut(String token, String user_name, String password){
        if (tokenService.validateToken(token)){
            if(userController.logOut(user_name)){
                return true;
            }
        }
        return false;
    }

    public boolean register(String user_name, String password){
        if (userController.register(user_name, password)){
            return true;
        }
        return false;
    }

}
