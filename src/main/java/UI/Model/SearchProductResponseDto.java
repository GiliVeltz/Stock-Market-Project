package UI.Model;

import java.util.List;
import java.util.Map;

public class SearchProductResponseDto {
    private String errorMessage;
    private Map<String, List<ProductDto>> returnValue;

    public SearchProductResponseDto() {
    }

    public SearchProductResponseDto(String errorMessage, Map<String, List<ProductDto>> returnValue) {
        this.errorMessage = errorMessage;
        this.returnValue = returnValue;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Map<String, List<ProductDto>> getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(Map<String, List<ProductDto>> returnValue) {
        this.returnValue = returnValue;
    }
}

