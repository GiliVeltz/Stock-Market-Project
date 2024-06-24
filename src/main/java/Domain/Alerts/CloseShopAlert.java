package Domain.Alerts;

public class CloseShopAlert implements Alert{

    private String removingManager;
    private String targetUser;
    private int shopId;
    private String message;

    public CloseShopAlert(String removingManager, String targetUsername, int shopId) {
        this.removingManager = removingManager;
        this.targetUser = targetUsername;
        this.shopId = shopId;
        this.message = "";
        setMessage();
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public void setMessage() {
        this.message = "New Notification: " + targetUser + "\nUser: " + this.removingManager + " has closed ths shop with id: " + this.shopId;
    }
    

    @Override
    public String getFromUser() {
        return this.removingManager;
    }

    @Override
    public boolean isEmpty() {
        return this.message.isEmpty();
    }

    @Override
    public int getShopId() {
        return this.shopId;
    }

    @Override
    public String getTargetUser() {
        return this.targetUser;
    }
    
}
