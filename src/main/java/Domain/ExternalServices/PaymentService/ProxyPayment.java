package Domain.ExternalServices.PaymentService;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import Dtos.PaymentInfoDto;

import java.util.HashMap;
import java.util.Map;

public class ProxyPayment {
    private String externalSystemUrl = "https://damp-lynna-wsep-1984852e.koyeb.app/";
    RestTemplate restTemplate;

    public ProxyPayment() {
        restTemplate = new RestTemplate();
    }
    
    public boolean handshake(){
        // Form data
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> request = new HttpEntity<>("action_type=handshake", headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(externalSystemUrl, request, String.class);

            // Check if response is "OK"
            if (response.getStatusCode() == HttpStatus.OK && "OK".equals(response.getBody())) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public int payment(PaymentInfoDto paymentInfo, double totalPrice)  {
        // Form data
        Map<String, String> formData = new HashMap<>();
        formData.put("action_type", "pay");
        formData.put("amount", Double.toString(totalPrice));
        formData.put("currency", paymentInfo.getCurrency());
        formData.put("card_number", paymentInfo.getCardNumber());
        formData.put("month", paymentInfo.getMonth());
        formData.put("year", paymentInfo.getYear());
        formData.put("holder", paymentInfo.getHolder());
        formData.put("ccv", paymentInfo.getCcv());
        formData.put("id", paymentInfo.getId());

        // Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Request entity
        HttpEntity<Map<String, String>> request = new HttpEntity<>(formData, headers);

        try {
            // Send POST request
            ResponseEntity<String> response = restTemplate.postForEntity(externalSystemUrl, request, String.class);

            // Parse the response to an integer
            if (response.getStatusCode() == HttpStatus.OK) {
                return Integer.parseInt(response.getBody());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1; // Return -1 if payment failed
    }

    public int cancel_pay(int transaction_id)  {
        // Form data
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String formData = "action_type=cancel_pay&transaction_id=" + transaction_id;

        HttpEntity<String> request = new HttpEntity<>(formData, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(externalSystemUrl, request, String.class);

            // Check the response and parse it to integer
            if (response.getStatusCode() == HttpStatus.OK) {
                return Integer.parseInt(response.getBody());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1; // Return -1 if cancellation failed
    }
}
