public class UserService {
    private UserController userController;
    private TokenService tokenService;

    public UserService(){
        userController = new UserController();
        tokenService = new TokenService();
    }

    public String logIn(String userName, String password) throws Exception {
        try {
            userController.logIn(userName, password);
            return tokenService.generateToken(userName);
        } catch (Exception e) {
            throw new Exception("Login failed: " + e.getMessage());
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
