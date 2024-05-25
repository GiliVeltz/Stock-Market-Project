package Domain.Discounts;

import java.util.Date;
import java.util.List;
import java.util.Map;

import Domain.Product;
import Domain.ShoppingBasket;
import Domain.Rules.Rule;
import Exceptions.DiscountExpiredException;

public abstract class Discount {
    protected Rule<ShoppingBasket> _rule;
    private Date _expirationDate;

    public Discount(Date expirationDate) {
        _expirationDate = expirationDate;
    }

    public void applyDiscount(ShoppingBasket basket) throws DiscountExpiredException {
        Date currentTime = new Date();
        if (currentTime.before(_expirationDate) && _rule.predicate(basket))
            applyDiscountLogic(basket);
        else if (currentTime.after(_expirationDate))
            throw new DiscountExpiredException("Discount has expired");
    }

    public Date getExpirationDate() {
        return _expirationDate;
    }

    protected abstract void applyDiscountLogic(ShoppingBasket basket) throws DiscountExpiredException;
}
