package Dtos;

import java.util.Date;

public class BasicDiscountDto {
    public int productId;
    public boolean isPrecentage;
    public double discountAmount;
    public Date expirationDate;

    public BasicDiscountDto(int productId, boolean isPrecentage, double discountAmount, Date expirationDate) {
        this.productId = productId;
        this.isPrecentage = isPrecentage;
        this.discountAmount = discountAmount;
        this.expirationDate = expirationDate;
    }
}
