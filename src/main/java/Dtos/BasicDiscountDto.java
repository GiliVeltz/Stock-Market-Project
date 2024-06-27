package Dtos;

import java.util.Date;

import enums.Category;

public class BasicDiscountDto {
    public int productId;
    public boolean isPrecentage;
    public double discountAmount;
    public Date expirationDate;
    public Category category;

    public BasicDiscountDto(int productId, boolean isPrecentage, double discountAmount, Date expirationDate, Category category) {
        this.productId = productId;
        this.isPrecentage = isPrecentage;
        this.discountAmount = discountAmount;
        this.expirationDate = expirationDate;
        this.category = category;
    }
}
