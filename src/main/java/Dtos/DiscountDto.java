package Dtos;

import java.util.Date;

import Domain.Discounts.Discount;

public class DiscountDto {
    private Date _expirationDate;

    public DiscountDto(Date expirationDate) {
        _expirationDate = expirationDate;
    }

    public DiscountDto(Discount discount) {
        _expirationDate = discount.getExpirationDate();
    }

    public Date getExpirationDate() {
        return _expirationDate;
    }
}
