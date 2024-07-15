package Dtos;

import Domain.Entities.ShopOrder;
import Exceptions.StockMarketException;

public class ShopOrderDto {
    private Integer _orderId;
    private ShoppingBasketDto _shoppingBasketDto;
    private double _totalOrderAmount;

    public ShopOrderDto() {
    }

    public ShopOrderDto(int orderId, ShoppingBasketDto shoppingBasketDto, double totalOrderAmount) {
        _orderId = orderId;
        _shoppingBasketDto = shoppingBasketDto;
        _totalOrderAmount = totalOrderAmount;
    }

    public ShopOrderDto(ShopOrder shopOrder) throws StockMarketException {
        _orderId = shopOrder.getShopOrderId();
        _shoppingBasketDto = new ShoppingBasketDto(shopOrder.getShoppingBasket());
        _totalOrderAmount = shopOrder.getOrderTotalAmount();
    }

    public Integer getOrderId() {
        return _orderId;
    }

    public void setOrderId(Integer orderId) {
        _orderId = orderId;
    }

    public ShoppingBasketDto getShoppingBasketDto() {
        return _shoppingBasketDto;
    }

    public void setShoppingBasketDto(ShoppingBasketDto shoppingBasketDto) {
        _shoppingBasketDto = shoppingBasketDto;
    }

    public double getTotalOrderAmount() {
        return _totalOrderAmount;
    }

    public void setTotalOrderAmount(double totalOrderAmount) {
        _totalOrderAmount = totalOrderAmount;
    }
}
