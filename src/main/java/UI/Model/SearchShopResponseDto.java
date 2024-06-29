package UI.Model;

import java.util.List;
import java.util.Map;

public class SearchShopResponseDto {
    private String errorMessage;
    private List<ShopDto> returnValue;

    public SearchShopResponseDto() {
    }

    public SearchShopResponseDto(String errorMessage, List<ShopDto> returnValue) {
        this.errorMessage = errorMessage;
        this.returnValue = returnValue;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public List<ShopDto> getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(List<ShopDto> returnValue) {
        this.returnValue = returnValue;
    }
}
