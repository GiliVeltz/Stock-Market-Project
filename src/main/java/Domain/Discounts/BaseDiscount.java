package Domain.Discounts;

import java.util.Date;

public abstract class BaseDiscount extends Discount {
    public BaseDiscount(Date expirationDate) {
        super(expirationDate);
    }
}
