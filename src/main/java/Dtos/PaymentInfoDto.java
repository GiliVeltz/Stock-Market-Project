package Dtos;

public class PaymentInfoDto {
    private String currency;
    private String cardNumber;
    private String month;
    private String year;
    private String holder;
    private String ccv;
    private String id;

    public PaymentInfoDto() {
    }

    public PaymentInfoDto(String currency, String cardNumber, String month, String year, String holder, String ccv, String id) {
        this.currency = currency;
        this.cardNumber = cardNumber;
        this.month = month;
        this.year = year;
        this.holder = holder;
        this.ccv = ccv;
        this.id = id;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setCardNumber(String card_number) {
        this.cardNumber = card_number;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setHolder(String holder) {
        this.holder = holder;
    }

    public void setCcv(String ccv) {
        this.ccv = ccv;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCurrency() {
        return currency;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getMonth() {
        return month;
    }

    public String getYear() {
        return year;
    }

    public String getHolder() {
        return holder;
    }

    public String getCcv() {
        return ccv;
    }

    public String getId() {
        return id;
    }
}
