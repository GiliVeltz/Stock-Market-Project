package UI.Model;

import java.util.Date;

public class DiscountDto {
    private Date _expirationDate;

    public DiscountDto(Date expirationDate) {
        _expirationDate = expirationDate;
    }

    public Date getExpirationDate() {
        return _expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        _expirationDate = expirationDate;
    }
}
