package Domain.Discounts;

import java.util.Date;
import Domain.ShoppingBasket;
import Domain.Rules.Rule;
import Exceptions.DiscountExpiredException;
import Exceptions.StockMarketException;

public abstract class Discount {
    protected Rule<ShoppingBasket> _rule;
    private Date _expirationDate;

    public Discount(Date expirationDate) {
        _expirationDate = expirationDate;
    }

    public void applyDiscount(ShoppingBasket basket) throws StockMarketException {
        Date currentTime = new Date();
        if (currentTime.before(_expirationDate) && _rule.predicate(basket))
            applyDiscountLogic(basket);
        else if (currentTime.after(_expirationDate))
            throw new DiscountExpiredException("Discount has expired");
    }

    public Date getExpirationDate() {
        return _expirationDate;
    }

    public abstract int getParticipatingProduct();

    protected abstract void applyDiscountLogic(ShoppingBasket basket) throws StockMarketException;
}
