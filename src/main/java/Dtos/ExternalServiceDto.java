package Dtos;

public class ExternalServiceDto {
    private String serviceName;
    private String informationPersonName;
    private String informationPersonPhone;
    
    // Constructor
    public ExternalServiceDto(String serviceName, String informationPersonName, String informationPersonPhone) {
        this.serviceName = serviceName;
        this.informationPersonName = informationPersonName;
        this.informationPersonPhone = informationPersonPhone;
    }
    
    // Getters and Setters
    public String getServiceName() {
        return serviceName;
    }
    
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
    
    public String getInformationPersonName() {
        return informationPersonName;
    }
    
    public void setInformationPersonName(String informationPersonName) {
        this.informationPersonName = informationPersonName;
    }
    
    public String getInformationPersonPhone() {
        return informationPersonPhone;
    }
    
    public void setInformationPersonPhone(String informationPersonPhone) {
        this.informationPersonPhone = informationPersonPhone;
    }
}
