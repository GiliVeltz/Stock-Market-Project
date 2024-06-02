package Dtos;

import java.util.Date;
import java.util.List;

public class ConditionalDiscountDto extends BasicDiscountDto {
    public List<Integer> mustHaveProducts;

    public ConditionalDiscountDto(int productId, boolean isPrecentage, double discountAmount, Date expirationDate,
            List<Integer> mustHaveProducts) {
        super(productId, isPrecentage, discountAmount, expirationDate);
        this.mustHaveProducts = mustHaveProducts;
    }
}
