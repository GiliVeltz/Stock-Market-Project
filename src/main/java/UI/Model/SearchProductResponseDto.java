package UI.Model;

import java.util.List;
import java.util.Map;

public class SearchProductResponseDto {
    private String errorMessage;
    private Map<ShopDto, List<ProductDto>> returnValue;

    public SearchProductResponseDto() {
    }

    public SearchProductResponseDto(String errorMessage, Map<ShopDto, List<ProductDto>> returnValue) {
        this.errorMessage = errorMessage;
        this.returnValue = returnValue;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Map<ShopDto, List<ProductDto>> getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(Map<ShopDto, List<ProductDto>> returnValue) {
        this.returnValue = returnValue;
    }
}

