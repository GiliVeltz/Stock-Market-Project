package Domain.Alerts;

public class IntegrityRuleBreakAlert implements Alert{

    private String fromUser;
    private String targetUser;
    private String message;

    public IntegrityRuleBreakAlert(String fromUser,String message){
        this.fromUser = fromUser;
        this.targetUser = "u1_Admin";   
        this.message = message;
        setMessage();
     
    }
    @Override
    public String getMessage() {
        return this.message;
    }
    @Override
    public void setMessage() {
        this.message = "New Notification: " + targetUser + " user: " + fromUser + "report on breaking integrity rule " +"\n" + this.message;
        
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
