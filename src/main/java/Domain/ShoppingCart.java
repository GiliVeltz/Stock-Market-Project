package Domain;

import java.util.List;

public class ShoppingCart {
    private List<ShoppingBasket> _shoppingBaskets;

    public ShoppingCart(List<ShoppingBasket> shoppingBaskets) {
        _shoppingBaskets = shoppingBaskets;
    }
}
