package Dtos;

import java.util.List;

public class BasketDto {
    int _shopID;
    List<Integer> _productIDs;
    double _totalPrice;

    public BasketDto(int shopID, List<Integer> productIDs, double totalPrice) {
        _shopID = shopID;
        _productIDs = productIDs;
        _totalPrice = totalPrice;
    }
}
