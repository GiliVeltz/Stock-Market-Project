package Domain;
public class User extends Guest {
    private String encoded_password;
    private String username;
    private boolean loggedIn;

    public User(String username, String encoded_password) {
        this.username = username;
        this.encoded_password = encoded_password;
        this.loggedIn = false;
    }

    public boolean isCurrUser(String username, String encoded_password) {
        if(this.username == username & this.encoded_password == encoded_password){
            return true;
        }
        return false;
    }

    public String getUserName(){
        return this.username;
    }

    public String getEncodedPassword(){
        return this.encoded_password;
    }

    public void logIn(){
        this.loggedIn = true;
    }

    public void logOut(){
        this.loggedIn = false;
    }

}
