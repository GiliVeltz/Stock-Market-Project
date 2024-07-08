package UI.Model;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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


    // Extracts the value of a field from a shop string
    // shopString contains shopID, name and Rating for response
    // for example : " */Id/* 1 */Name/* shop1 */Rating/* 4.5" .
    public static String extractValue(String shopString, String field) {
        String regex = " \\*/" + field + "/\\* ([^\\s]+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(shopString);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return null; // not found
    }
}

