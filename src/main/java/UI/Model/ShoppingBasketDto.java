package UI.Model;

import java.util.List;

public class ShoppingBasketDto {
    private ShopDto _shop;
    private List<Integer> _productIdList;
    private double _basketTotalAmount;

    public ShoppingBasketDto(ShopDto shop, List<Integer> productIdList, double basketTotalAmount) {
        this._shop = shop;
        this._productIdList = productIdList;
        this._basketTotalAmount = basketTotalAmount;
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
