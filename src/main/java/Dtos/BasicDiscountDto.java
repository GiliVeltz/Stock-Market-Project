package Dtos;

import java.util.Date;

import Domain.Entities.enums.Category;

public class BasicDiscountDto {
    public int productId;
    public boolean isPrecentage;
    public double discountAmount;
    public Date expirationDate;
    public Category category;
    public int id;

    public BasicDiscountDto(int productId, boolean isPrecentage, double discountAmount, Date expirationDate, Category category, int id) {
        this.productId = productId;
        this.isPrecentage = isPrecentage;
        this.discountAmount = discountAmount;
        this.expirationDate = expirationDate;
        this.category = category;
        this.id = id;
    }
}
