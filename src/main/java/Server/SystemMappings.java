package Server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ServiceLayer.*;

@RestController
@RequestMapping(path = "/api/system")
public class SystemMappings {
    private final SystemService _systemService;

    @Autowired
    public SystemMappings(SystemService systemService) {
        this._systemService = systemService;
    }

    @GetMapping("/openSystem")
    public Response openSystem(@RequestHeader(value = "Authorization", required = true) String token) {
        Response response = _systemService.openSystem(token);
        return response;
    }

    @GetMapping("/enterSystem")
    public Response enterSystem() {
        Response resp = _systemService.requestToEnterSystem();
        return resp;
    }

    @GetMapping("/leaveSystem")
    public Response leaveSystem(@RequestHeader(value = "Authorization", required = true) String token) {
        Response resp = _systemService.leaveSystem(token);
        return resp;
    }

}
