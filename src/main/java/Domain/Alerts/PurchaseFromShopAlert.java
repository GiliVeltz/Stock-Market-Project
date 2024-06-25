package Domain.Alerts;

import java.util.List;

public class PurchaseFromShopAlert implements Alert {
        private String fromUser;
    private String targetUser;
    private List<Integer> productIdList;
    private int shopId;
    private String message;

    public PurchaseFromShopAlert(String owner, String username,List<Integer> productIdList,int shopId){
        this.fromUser = username;
        this.targetUser = owner;
        this.productIdList = productIdList;
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
        this.message = "New Notification: " + targetUser + "\nUser : " + this.fromUser + " has purchased the following products from shop: " + this.shopId + " with the following product ids: " + this.productIdList;
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
        return this.shopId;
    }
    @Override
    public String getTargetUser() {
       return this.targetUser;
    }
    
}
