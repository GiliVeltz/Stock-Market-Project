package Domain.Entities.Alerts;

public class GeneralAlert implements Alert{

    private String fromUser;
    private String targetUser;
    private String message;

    public GeneralAlert(String fromUser, String targetUser,String message){
        this.fromUser = fromUser;
        this.targetUser = targetUser;   
        this.message = message;
        setMessage();
     
    }
    @Override
    public String getMessage() {
        return this.message;
    }
    @Override
    public void setMessage() {
        this.message = "New Notification: " + targetUser + "\n" + this.message;
        
    }
    public void setMessage(String message){
        this.message = message;
    }
    
    @Override
    public String getFromUser() {
        return this.fromUser;
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
