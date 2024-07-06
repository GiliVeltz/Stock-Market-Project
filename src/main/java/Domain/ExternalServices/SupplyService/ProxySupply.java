package Domain.ExternalServices.SupplyService;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import Dtos.SupplyInfoDto;

import java.util.HashMap;
import java.util.Map;

public class ProxySupply {

    private String externalSystemUrl = "https://damp-lynna-wsep-1984852e.koyeb.app/";
    RestTemplate restTemplate;

    public ProxySupply() {
        restTemplate = new RestTemplate();
    }

    public boolean handshake() {
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

    public int supply(SupplyInfoDto supplyInfo) {
        // Form data
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        Map<String, String> formData = new HashMap<>();
        formData.put("action_type", "supply");
        formData.put("name", supplyInfo.getName());
        formData.put("address", supplyInfo.getAddress());
        formData.put("city", supplyInfo.getCity());
        formData.put("country", supplyInfo.getCountry());
        formData.put("zip", supplyInfo.getZip());

        StringBuilder formBody = new StringBuilder();
        formData.forEach((key, value) -> formBody.append(key).append("=").append(value).append("&"));
        String requestBody = formBody.toString().substring(0, formBody.length() - 1);

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(externalSystemUrl, request, String.class);

            // Check the response and parse it to integer
            if (response.getStatusCode() == HttpStatus.OK) {
                return Integer.parseInt(response.getBody());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1; // Return -1 if supply failed
    }

    public int cancel_supply(int transaction_id) {
        // Form data
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String formData = "action_type=cancel_supply&transaction_id=" + transaction_id;

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
