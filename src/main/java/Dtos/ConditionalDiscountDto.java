package Dtos;

import java.util.Date;
import java.util.List;

import enums.Category;

public class ConditionalDiscountDto extends BasicDiscountDto {
    public List<Integer> mustHaveProducts;

    public ConditionalDiscountDto(int productId, boolean isPrecentage, double discountAmount, Date expirationDate, Category category,
            List<Integer> mustHaveProducts) {
        super(productId, isPrecentage, discountAmount, expirationDate, category);
        this.mustHaveProducts = mustHaveProducts;
    }
}
