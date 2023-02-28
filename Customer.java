package bkauto1;

public class Customer {
    private CustType.customerType type;
    private String name;

    public Customer(CustType.customerType type, String name) {
        this.type = type;
        this.name = name;
    }

    public CustType.customerType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(CustType.customerType type) {
        this.type = type;
    }

    public void printDetails() {
        System.out.println("Customer type: " + type);
        System.out.println("Name: " + name);
    }
}
