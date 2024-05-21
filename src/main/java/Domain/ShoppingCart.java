package Domain;

import java.util.List;

public class ShoppingCart {
    private List<ShoppingBasket> _shoppingBaskets;

    public ShoppingCart(List<ShoppingBasket> shoppingBaskets) {
        _shoppingBaskets = shoppingBaskets;
    }

    public String toString() {
        StringBuilder output = new StringBuilder();
        for (ShoppingBasket shoppingBasket : _shoppingBaskets) {
            output.append(shoppingBasket.toString()).append("\n");
        }
        return output.toString(); // Convert StringBuilder to String
    }
}
