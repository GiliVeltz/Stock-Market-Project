package Domain.Entities.Discounts;

import java.util.Date;

public abstract class BaseDiscount extends Discount {
    public BaseDiscount() {
    }
    public BaseDiscount(Date expirationDate) {
        super(expirationDate);
    }
}
