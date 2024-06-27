package Domain.Discounts;

import java.util.Date;
import Domain.ShoppingBasket;
import Domain.Rules.Rule;
import Dtos.BasicDiscountDto;
import Exceptions.DiscountExpiredException;
import Exceptions.StockMarketException;
import Domain.Product;

public abstract class Discount {
    protected Rule<ShoppingBasket> _rule;
    protected Rule<Product> _specialRule;
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

    // A special predicate to handle the shop and category discounts
    public boolean specialPredicate(Product prodcut){
        if(_specialRule != null){
            return _specialRule.predicate(prodcut);
        }
        return false;
    }

    public abstract int getParticipatingProduct();
    
    public abstract BasicDiscountDto getDto();

    protected abstract void applyDiscountLogic(ShoppingBasket basket) throws StockMarketException;
}
