package Domain.Alerts;

public class ReOpenShopAlert implements Alert {

    private String openManager;
    private String targetUser;
    private int shopId;
    private String message;

    public ReOpenShopAlert(String openManager, String targetUsername, int shopId) {
        this.openManager = openManager;
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
        this.message = "New Notification: " + targetUser + "\nUser: " + this.openManager + " has reopened the shop with id: "
                + this.shopId;
    }

    @Override
    public String getFromUser() {
        return this.openManager;
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
