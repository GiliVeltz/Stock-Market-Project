package Domain.Entities.Discounts;

import java.util.Date;

public abstract class BaseDiscount extends Discount {
    public BaseDiscount(Date expirationDate, int id) {
        super(expirationDate, id);
    }
}
