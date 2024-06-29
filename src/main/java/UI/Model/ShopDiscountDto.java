package UI.Model;
import java.util.Date;

public class ShopDiscountDto {
    public int productId;
    public boolean isPrecentage;
    public double discountAmount;
    public Date expirationDate;
    public String category;

    public ShopDiscountDto(){
        this.productId = -1;
        this.isPrecentage = false;
        this.discountAmount = 0;
        this.expirationDate = null;
        this.category = null;
    }
    public ShopDiscountDto(int productId, boolean isPrecentage, double discountAmount, Date expirationDate, String category) {
        this.productId = productId;
        this.isPrecentage = isPrecentage;
        this.discountAmount = discountAmount;
        this.expirationDate = expirationDate;
        this.category = category;
    }
}