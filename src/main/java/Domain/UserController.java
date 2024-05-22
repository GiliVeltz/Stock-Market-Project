package Domain;

import java.util.ArrayList;
import java.util.List;

public class UserController {
    private List<User> registeredUsers;
    private List<String> guestIds;
    private PasswordEncoderUtil passwordEncoder;

    public UserController() {
        this.registeredUsers = new ArrayList<>();
        this.passwordEncoder = new PasswordEncoderUtil();
    }

    public boolean isUserNameExists(String username) {
        // Check if the username already exists
        for (User user : this.registeredUsers) {
            if (user.getUserName().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public User getUserByUsername(String username) {
        for (User user : this.registeredUsers) {
            if (user.getUserName().equals(username)) {
                return user;
            }
        }
        return null;
    }

    public boolean AreCredentialsCorrect(String username, String password) {
        User user =  getUserByUsername(username);
        if (user != null){
            return this.passwordEncoder.matches(password, user.getEncodedPassword());
        } 
        return false;
    }

    public void register(String userName, String password, String email) throws Exception {
        String encodedPass = this.passwordEncoder.encodePassword(password);
        if (!isUserNameExists(userName)) {
            this.registeredUsers.add(new User(userName, encodedPass, email));
        } else {
            throw new Exception("Username already exists.");
        }
    }

    // function that check if a given user is an admin
    public boolean isAdmin(String userName) throws Exception {
        User user = getUserByUsername(userName);
        if (user != null) {
            return user.isAdmin();
        } else
            throw new Exception("User not found.");
    }

    private boolean isGuestExists(String id) {
        return guestIds.contains(id);
    }
    

    public void addNewGuest(String id){
        if (isGuestExists(id)) {
            throw new IllegalArgumentException("Guest with ID " + id + " already exists.");
        }
        guestIds.add(id);
    }

    public void removeGuest(String id){
        if (!isGuestExists(id)) {
            throw new IllegalArgumentException("Guest with ID " + id + " does not exist.");
        }
        guestIds.remove(String.valueOf(id));
    }
}
