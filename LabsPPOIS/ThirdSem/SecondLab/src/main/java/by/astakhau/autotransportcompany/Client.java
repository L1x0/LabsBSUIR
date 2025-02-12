package by.astakhau.autotransportcompany;

public class Client extends People {
    private Order order;

    public Client(String name, int age, Order order) {
        super(name, age);
        this.order = order;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

}
