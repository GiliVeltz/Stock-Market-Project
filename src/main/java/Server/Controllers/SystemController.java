package Server.Controllers;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ServiceLayer.*;

@RestController
@SuppressWarnings({"rawtypes"})
@RequestMapping(path = "/api/system")
public class SystemController {
    private final SystemService _systemService;

    @Autowired
    public SystemController(SystemService systemService) {
        this._systemService = systemService;
    }

    @GetMapping("/openSystem")
    public ResponseEntity<Response> openSystem(@RequestHeader(value = "Authorization", required = true) String token) {
        ResponseEntity<Response> response = _systemService.openSystem(token);
        return response;
    }

    @GetMapping("/enterSystem")
    public ResponseEntity<Response> enterSystem() {
        ResponseEntity<Response> resp = _systemService.requestToEnterSystem();
        return resp;
    }

    @PostMapping("/leaveSystem")
    public ResponseEntity<Response> leaveSystem(@RequestHeader(value = "Authorization", required = true) String token) {
        ResponseEntity<Response> resp = _systemService.leaveSystem(token);
        return resp;
    }

    @PostMapping("/leaveSystem")
    public ResponseEntity<Response> reportToAdmin(@RequestHeader("Authorization") String token,@RequestParam String message) {
       
        String decodedString = "";
    try {
        decodedString = URLDecoder.decode(message, StandardCharsets.UTF_8.toString());
          
    } catch (Exception e) {
        // Handle exception (e.g., UnsupportedEncodingException, which should not happen for UTF-8)
        e.printStackTrace();
    }
        ResponseEntity<Response> resp = _systemService.reportToAdmin(token,decodedString);
        return resp;
    }

     @GetMapping("/openComplaint")
    public ResponseEntity<Response> openComplaint(@RequestHeader("Authorization") String token, @RequestParam Integer shopId,@RequestParam String message) {
        String decodedString = "";
    try {
        decodedString = URLDecoder.decode(message, StandardCharsets.UTF_8.toString());
          
    } catch (Exception e) {
        // Handle exception (e.g., UnsupportedEncodingException, which should not happen for UTF-8)
        e.printStackTrace();
    }
    return _shopService.openComplaint(token,shopId,decodedString);
      
    }

}
