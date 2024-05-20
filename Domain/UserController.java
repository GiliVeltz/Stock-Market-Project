package Domain;
import java.util.ArrayList;
import java.util.List;

public class UserController {
    private List<User> users;
    private PasswordEncoderUtil passwordEncoder;

    public UserController() {
        this.users = new ArrayList<>();
        this.passwordEncoder = new PasswordEncoderUtil();
    }

    public boolean isUserNameExists(String username) {
        // Check if the username already exists
        for (User user : this.users) {
            if (user.getUserName().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public User getUserByUsername(String username) {
        for (User user : this.users) {
            if (user.getUserName().equals(username)) {
                return user;
            }
        }
        return null;
    }

    public boolean isCredentialsCorrect(User user, String password) {
        return this.passwordEncoder.matches(password, user.getEncodedPassword());
    }

    public void register(String user_name, String password, String email) throws Exception{
        String encodedPass = this.passwordEncoder.encodePassword(password);
        if (!isUserNameExists(user_name)) {
            this.users.add(new User(user_name, encodedPass, email));
        } else {
            throw new Exception("Username already exists.");
        }
    }

    public void logIn(String user_name, String password){
        User user = getUserByUsername(user_name);
        if (user != null && isCredentialsCorrect(user, password)) {
            user.logIn();
        }
        throw new Exception("Invalid credentials or registration required.");
    }

    public void logOut(String user_name){
        User user = getUserByUsername(user_name);
        if (user != null) {
            user.logOut();
        }
        throw new Exception("User not found or already logged out.");
    }
}
