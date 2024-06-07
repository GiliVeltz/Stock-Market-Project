package Domain.Alerts;

import java.util.List;

public class CredentialRemovedAlert implements Alert  {

    private String message;
    private String fromUser;
    private List <String> permissionsList;

    //constructor
    public CredentialRemovedAlert(String fromUser, List<String> permissionsList) {
        this.fromUser = fromUser;
        this.permissionsList = permissionsList;
        setMessage();
    }

   
    public String getFromUser() {
        return fromUser;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage() {
        this.message = "User " + fromUser + " remove you the next credentials: " + permissionsList;
    }

    public boolean isEmpty() {
        return message.isEmpty();
    }
    
}
