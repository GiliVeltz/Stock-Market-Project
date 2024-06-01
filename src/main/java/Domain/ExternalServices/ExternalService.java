
package Domain.ExternalServices;

// An interface for all external services
public interface ExternalService {

    public boolean ConnectToService();
    
    public String getServiceName();

    public void setServiceName(String serviceName);

    public String getInformationPersonName();

    public void setInformationPersonName(String informationPersonName);

    public String getInformationPersonPhone();

    public void setInformationPersonPhone(String informationPersonPhone);
}