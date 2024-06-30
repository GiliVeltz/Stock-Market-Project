package UI.Model;

import java.util.List;

public class ShoppingBasketDto {
    private ShopDto _shop;
    private List<Integer> _productIdList;
    private double _basketTotalAmount;

    public ShoppingBasketDto() {
    }

    public ShoppingBasketDto(ShopDto shop, List<Integer> productIdList, double basketTotalAmount) {
        this._shop = shop;
        this._productIdList = productIdList;
        this._basketTotalAmount = basketTotalAmount;
    }

    public ShopDto getShop() {
        return _shop;
    }

    public void setShop(ShopDto shop) {
        this._shop = shop;
    }

    public List<Integer> getProductIdList() {
        return _productIdList;
    }

    public void setProductIdList(List<Integer> productIdList) {
        this._productIdList = productIdList;
    }   

    public double getBasketTotalAmount() {
        return _basketTotalAmount;
    }

    public void setBasketTotalAmount(double basketTotalAmount) {
        this._basketTotalAmount = basketTotalAmount;
    }
}
