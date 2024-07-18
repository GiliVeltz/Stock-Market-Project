package UI.Presenter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class ErrorMessageGenerator {
    private final static String ERROR_MSG = "An unexpected error has occurred. Please try again later.";
    // Static method to generate a generic error message
    public static String generateGenericErrorMessage(String errorMessage) {
        try{
            String extractedMsg =  extractErrorMessage(errorMessage);
            return extractedMsg;
        }catch(Exception e){
            return ERROR_MSG;
        }
    }

    // Static method to generate a generic error message
    public static String generateGenericErrorMessage() {
        return ERROR_MSG;
    }

    private static String extractErrorMessage(String errorMessageString) throws Exception {
        // Find the first index of '{'
        int jsonStartIndex = errorMessageString.indexOf('{');
        if (jsonStartIndex == -1) {
            throw new IllegalArgumentException("Invalid error message string format.");
        }

        // Extract the JSON substring
        String jsonString = errorMessageString.substring(jsonStartIndex);

        // Create an ObjectMapper instance
        ObjectMapper objectMapper = new ObjectMapper();

        // Parse the JSON string to a JsonNode
        JsonNode jsonNode = objectMapper.readTree(jsonString);

        // Retrieve the error message
        return jsonNode.get("errorMessage").asText();
    }
}
// "500 : "{"errorMessage":"LogIn Failed: Username dasd does not exist.","returnValue":null}""