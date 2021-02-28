package model;

public class ExtraService {

    private int serviceID;
    private String serviceName;
    private int price;

    public ExtraService(int serviceID, String serviceName, int price) {
        this.serviceID = serviceID;
        this.serviceName = serviceName;
        this.price = price;
    }

    public int getServiceID() {
        return serviceID;
    }

    public void setServiceID(int serviceID) {
        this.serviceID = serviceID;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return serviceName + " costs " + price;
    }
}
