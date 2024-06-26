package UI.Model;

import java.util.Map;

public class OrderDto {
    private Integer _orderId;
    private Map<Integer ,ShoppingBasketDto> _shoppingBasketMap; // <ShopId, ShoppingBasketPerShop> 
    private double _totalOrderAmount;

    public OrderDto() {
    }

    public OrderDto(int orderId, Map<Integer, ShoppingBasketDto> shoppingBasketMap, double totalOrderAmount) {
        _orderId = orderId;
        _shoppingBasketMap = shoppingBasketMap;
        _totalOrderAmount = totalOrderAmount;
    }

    public Integer getOrderId() {
        return _orderId;
    }

    public void setOrderId(Integer orderId) {
        _orderId = orderId;
    }

    public Map<Integer, ShoppingBasketDto> getShoppingBasketMap() {
        return _shoppingBasketMap;
    }

    public void setShoppingBasketMap(Map<Integer, ShoppingBasketDto> shoppingBasketMap) {
        _shoppingBasketMap = shoppingBasketMap;
    }

    public double getTotalOrderAmount() {
        return _totalOrderAmount;
    }

    public void setTotalOrderAmount(double totalOrderAmount) {
        _totalOrderAmount = totalOrderAmount;
    }
}
