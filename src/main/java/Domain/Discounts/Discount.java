package Domain.Discounts;

import java.util.List;
import java.util.Map;

import Domain.Product;
import Domain.ShoppingBasket;
import Domain.Rules.Rule;

public interface Discount {
    void applyDiscount(ShoppingBasket basket);
}
