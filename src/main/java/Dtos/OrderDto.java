package Dtos;

import java.util.HashMap;
import java.util.Map;

import Domain.Entities.Order;
import Domain.Entities.ShoppingBasket;
import Exceptions.StockMarketException;

public class OrderDto {
    private Integer _orderId;
    private Map<Integer, ShoppingBasketDto> _shoppingBasketMap; // <ShopId, ShoppingBasketPerShop>
    private double _totalOrderAmount;

    public OrderDto() {
    }

    public OrderDto(Order order) throws StockMarketException {
        _orderId = order.getOrderId();
        _shoppingBasketMap = new HashMap<>();
        for (Map.Entry<Integer, ShoppingBasket> entry : order.getShoppingBasketMap().entrySet()) {
            _shoppingBasketMap.put(entry.getKey(), new ShoppingBasketDto(entry.getValue()));
        }
        _totalOrderAmount = order.getOrderTotalAmount();
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
