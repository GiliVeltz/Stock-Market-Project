package Domain;
public class User extends Guest {
    private String encoded_password;
    private String username;

    public User(String username, String encoded_password) {
        this.username = username;
        this.encoded_password = encoded_password;
    }

    public isCurrUser(String username, String encoded_password) {
        if(this.username == username & this.encoded_password == encoded_password){
            return true;
        }
        return false;
    }

    public getUserName(){
        return username;
    }

    public getPassword(){
        return username;
    }


}
