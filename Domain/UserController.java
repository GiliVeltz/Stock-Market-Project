package Domain;
import java.util.ArrayList;
import java.util.List;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

public class UserController {
    private List<User> users;
    private List<Guest> guests;
    private PasswordEncoderUtil passwordEncoder;

    public UserController() {
        this.users = new ArrayList<>();
        this.guests = new ArrayList<>();
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

    public boolean register(String user_name, String password){
        String encodedPass = this.passwordEncoder.encodePassword(password);
        if(!isUserNameExists(user_name)){
            this.users.add(new User(user_name, encodedPass));
            return true;
        }
        return false;
    }

    public boolean logIn(String user_name, String password){
        User user = getUserByUsername(user_name);
        if (user != null && isCredentialsCorrect(user, password)) {
            user.logIn();
            return true;
        }
        return false;
    }

    public boolean logOut(String user_name){
        User user = getUserByUsername(user_name);
        if (user != null) {
            user.logOut();
            return true;
        }
        return false;
    }
}
