package by.astakhau.autotransportcompany;

public class Order {
    private int id;
    private int customerId;
    private int productId;
    private String country;

    public Order(String country, int productId, int customerId, int id) {
        this.country = country;
        this.productId = productId;
        this.customerId = customerId;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
