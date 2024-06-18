package Domain.Alerts;


public class AppointedOwnerAlert implements Alert {
    private String ManagerName;
    private String userName;
    private int shopId;
    private String message;

    public AppointedOwnerAlert(String appointingManager, String targetUsername, int shopId) {
        this.ManagerName = appointingManager;
        this.userName = targetUsername;
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
        this.message = "Hello : " + userName + "\nUser: " + this.ManagerName + " has appointed you as an owner in shop "
                + this.shopId;
    }

    @Override
    public String getFromUser() {
        return this.ManagerName;
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
        return this.userName;
    }

}
