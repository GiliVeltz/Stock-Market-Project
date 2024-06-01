
package Domain.ExternalServices;

import java.util.logging.Logger;

public class ExternalService {

    // private fields
    private int _id;
    private String _serviceName;
    private String _informationPersonName;
    private String _informationPersonPhone;
    private static final Logger logger = Logger.getLogger(ExternalService.class.getName());

    // constructor
    public ExternalService(int id, String newSerivceName, String informationPersonName, String informationPersonPhone) {
        _id = id;
        _serviceName = newSerivceName;
        _informationPersonName = informationPersonName;
        _informationPersonPhone = informationPersonPhone;
    }

    public boolean ConnectToService(){
        throw new UnsupportedOperationException("ConnectToService for service " + _serviceName + " not supported yet.");
    }

    // getters and seeters

    public String getServiceName() {
        return _serviceName;
    }

    public void setServiceName(String serviceName) {
        this._serviceName = serviceName;
    }

    public String getInformationPersonName() {
        return _informationPersonName;
    }

    public void setInformationPersonName(String informationPersonName) {
        this._informationPersonName = informationPersonName;
    }

    public String getInformationPersonPhone() {
        return _informationPersonPhone;
    }

    public void setInformationPersonPhone(String informationPersonPhone) {
        this._informationPersonPhone = informationPersonPhone;
    }
}