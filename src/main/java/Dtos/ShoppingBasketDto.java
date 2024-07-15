package Dtos;

import java.util.List;

import Domain.Entities.ShoppingBasket;
import Exceptions.StockMarketException;

public class ShoppingBasketDto {
    private ShopDto _shop;
    private List<Integer> _productIdList;
    private double _basketTotalAmount;

    public ShoppingBasketDto(ShopDto shop, List<Integer> productIdList, double basketTotalAmount) {
        this._shop = shop;
        this._productIdList = productIdList;
        this._basketTotalAmount = basketTotalAmount;
    }

    public ShoppingBasketDto(ShoppingBasket shoppingBasket) throws StockMarketException {
        this._shop = new ShopDto(shoppingBasket.getShop());
        this._productIdList = shoppingBasket.getProductIdsList();
        this._basketTotalAmount = shoppingBasket.getShoppingBasketPrice();
    }

    public ShopDto getShop() {
        return _shop;
    }

    public List<Integer> getProductIdList() {
        return _productIdList;
    }

    public double getBasketTotalAmount() {
        return _basketTotalAmount;
    }
}
