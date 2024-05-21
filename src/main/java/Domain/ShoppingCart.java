package Domain;

import java.util.List;

import Domain.ExternalServices.PaymentService.PaymentMethod;
import Domain.ExternalServices.SupplyService.SupplyMethod;

public class ShoppingCart {
    private List<ShoppingBasket> _shoppingBaskets;

    public ShoppingCart(List<ShoppingBasket> shoppingBaskets) {
        _shoppingBaskets = shoppingBaskets;
    }

    public String purchesCart(User buyer, PaymentMethod paymentMethod, SupplyMethod shippingMethod, List<Integer> busketsToBuy) {
            
        return "";
    }

    public String toString() {
        StringBuilder output = new StringBuilder();
        for (ShoppingBasket shoppingBasket : _shoppingBaskets) {
            output.append(shoppingBasket.toString()).append("\n");
        }
        return output.toString(); // Convert StringBuilder to String
    }
}
