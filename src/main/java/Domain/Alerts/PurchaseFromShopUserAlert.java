package Domain.Alerts;


public class PurchaseFromShopUserAlert implements Alert {
    private String targetUser;
    private String message;

    public PurchaseFromShopUserAlert(String username) {
        this.targetUser = username;
       
        this.message = "";
        setMessage();
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public void setMessage() {
        this.message = "New Notification: " + targetUser + "\n" + "Your purchase were successfully made";
    }

    @Override
    public String getFromUser() {
        return "";
    }

    @Override
    public boolean isEmpty() {
        return this.message.isEmpty();
    }

    @Override
    public int getShopId() {
        return -1;
    }

    @Override
    public String getTargetUser() {
        return this.targetUser;
    }

}
