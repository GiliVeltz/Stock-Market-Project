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
        users = new ArrayList<>();
        guests = new ArrayList<>();
        passwordEncoder = new PasswordEncoderUtil();
    }

    public boolean isUserNameExists(String username) {
        // Check if the username already exists
        for (User user : users) {
            if (user.getUserName().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public boolean isCredentialCorrect(String username, String encoded_password) {
        // Check if the username already exists
        for (User user : users) {
            if(user.getUserName().equals(username)){
                if (user.getPassword().equals(encoded_password)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean register(String user_name, String password){
        String encodedPass = passwordEncoder.encodePassword(password);
        if(!isUserNameExists(user_name)){
            users.add(new User(user_name, encodedPass));
            return true;
        }
        return false;
    }

    public boolean logIn(String user_name, String password){
        String encodedPass = passwordEncoder.encodePassword(password);
        if(!isCredentialCorrect(user_name, encodedPass)){
            return false;
        } 
        return true;
    }

    public boolean logOut(String user_name){
        
    }

}
