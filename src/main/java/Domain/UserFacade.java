package Domain;

import java.util.List;

public class UserFacade {
    private List<User> _registeredUsers;
    private List<String> _guestIds;
    private PasswordEncoderUtil _passwordEncoder;

    public UserFacade(List<User> registeredUsers, List<String> guestIds, PasswordEncoderUtil passwordEncoder) {
        _registeredUsers = registeredUsers;
        _guestIds = guestIds;
        _passwordEncoder = passwordEncoder;
    }

    public boolean isUserNameExists(String username) {
        // Check if the username already exists
        for (User user : this._registeredUsers) {
            if (user.getUserName().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public User getUserByUsername(String username) {
        for (User user : this._registeredUsers) {
            if (user.getUserName().equals(username)) {
                return user;
            }
        }
        return null;
    }

    public boolean AreCredentialsCorrect(String username, String password) {
        User user =  getUserByUsername(username);
        if (user != null){
            return this._passwordEncoder.matches(password, user.getEncodedPassword());
        } 
        return false;
    }

    // this function is used to register a new user to the system.
    public void register(String userName, String password, String email) throws Exception {
        String encodedPass = this._passwordEncoder.encodePassword(password);
        if (!isUserNameExists(userName)) {
            this._registeredUsers.add(new User(userName, encodedPass, email));
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
        return _guestIds.contains(id);
    }
    

    public void addNewGuest(String id){
        if (isGuestExists(id)) {
            throw new IllegalArgumentException("Guest with ID " + id + " already exists.");
        }
        _guestIds.add(id);
    }

    public void removeGuest(String id){
        if (!isGuestExists(id)) {
            throw new IllegalArgumentException("Guest with ID " + id + " does not exist.");
        }
        _guestIds.remove(String.valueOf(id));
    }

    public List<User> get_registeredUsers() {
        return _registeredUsers;
    }

    // function to return the purchase history for the user
    public List<Order> getPurchaseHistory(String username) {
        User user = getUserByUsername(username);
        if (user != null) {
            return user.getPurchaseHistory();
        }
        return null;
    }


}
