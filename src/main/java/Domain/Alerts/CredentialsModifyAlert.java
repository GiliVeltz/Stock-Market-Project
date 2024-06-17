package Domain.Alerts;

import java.util.List;

public class CredentialsModifyAlert implements Alert {  
    private String message;
    private String fromUser;
    private String targetUser;
    private int shopId;
    private List<String> permissionsList;

    // constructor
    public CredentialsModifyAlert(String fromUser,String targetUser, List<String> permissionsList, int shopId) {
        this.fromUser = fromUser;
        this.targetUser = targetUser;
        this.permissionsList = permissionsList;
        this.shopId = shopId;
        setMessage();
    }

    public String getFromUser() {
        return fromUser;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage() {
        this.message = "hello: " + targetUser + "\nUser " + fromUser + " modified yours credentials from the shop: " + shopId +" The new credentials are: " + permissionsList;
    }

    public boolean isEmpty() {
        return message.isEmpty();
    }

    public int getShopId() {
        return shopId;
    }

    public String getTargetUser() {
        return targetUser;
    }
}
