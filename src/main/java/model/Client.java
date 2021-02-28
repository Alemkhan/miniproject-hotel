package model;

public class Client {

    private int clientID;
    private String clientName;
    private String password;

    public Client(int clientID, String clientName, String password) {
        this.clientID = clientID;
        this.clientName = clientName;
        this.password = password;
    }

    public Client() {

    }

    public int getClientID() {
        return clientID;
    }

    public void setClientID(int clientID) {
        this.clientID = clientID;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Client{" +
                "clientID=" + clientID +
                ", clientName='" + clientName + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
