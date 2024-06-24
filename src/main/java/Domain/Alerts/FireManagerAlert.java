package Domain.Alerts;

public class FireManagerAlert implements Alert {

    private String ManagerName;
    private String userName;
    private int shopId;
    private String message;

    public FireManagerAlert(String manager, String username,int shopId){
        this.ManagerName = manager;
        this.userName = username;
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
        this.message = "New Notification: " + userName + "\nUser: " + this.ManagerName + " has fired you as a manager in shop " + this.shopId;
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
