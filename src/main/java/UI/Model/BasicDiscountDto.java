package UI.Model;

import java.util.Date;

import enums.Category;

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

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public boolean isPrecentage() {
        return isPrecentage;
    }

    public void setPrecentage(boolean precentage) {
        isPrecentage = precentage;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
