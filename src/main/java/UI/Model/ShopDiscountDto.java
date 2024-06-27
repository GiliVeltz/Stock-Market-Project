package UI.Model;
import java.util.Date;

public class ShopDiscountDto {
    public int productId;
    public boolean isPrecentage;
    public double discountAmount;
    public Date expirationDate;
    public Category category;

    public ShopDiscountDto(){
        this.productId = -1;
        this.isPrecentage = false;
        this.discountAmount = 0;
        this.expirationDate = null;
        this.category = null;
    }
    public ShopDiscountDto(int productId, boolean isPrecentage, double discountAmount, Date expirationDate, Category category) {
        this.productId = productId;
        this.isPrecentage = isPrecentage;
        this.discountAmount = discountAmount;
        this.expirationDate = expirationDate;
        this.category = category;
    }

    public int getProductId() {
        return productId;
    }

    public boolean isPrecentage() {
        return isPrecentage;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public Category getCategory() {
        return category;
    }

    public String getType(){
        if(category!=null){
            return "Category";
        }
        if(productId == -1){
            return "Shop";
        }
        return "Product";
    }
    public String getDiscount(){
        if(isPrecentage){
            return discountAmount+"%";
        }
        return ""+discountAmount;
    }
    public String getParticipants(){
        String type = getType();
        if(type.equals("Category")){
            return category.toString();
        }
        if(type.equals("Shop")){
            return "All products";
        }
        return "Product ID: "+productId;
    }
}