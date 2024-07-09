package UI.Model;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BasketDto {
    int _shopID;
    List<Integer> _productIDs;
    double _totalPrice;

    public BasketDto() {
        _shopID = -1;
        _productIDs = null;
        _totalPrice = -1;
    }

    public BasketDto(int shopID, List<Integer> productIDs, double totalPrice) {
        _shopID = shopID;
        _productIDs = productIDs;
        _totalPrice = totalPrice;
    }

    public int getShopID() {
        return _shopID;
    }

    public void setShopID(int shopID) {
        _shopID = shopID;
    }

    public List<Integer> getProductIDs() {
        return _productIDs;
    }

    public void setProductIDs(List<Integer> productIDs) {
        _productIDs = productIDs;
    }

    public double getTotalPrice() {
        return _totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        _totalPrice = totalPrice;
    }

    public Map<Integer, Long> getProductIDsCount() {
        return getProductIDs().stream()
                .collect(Collectors.groupingBy(productId -> productId, Collectors.counting()));
    }
}
