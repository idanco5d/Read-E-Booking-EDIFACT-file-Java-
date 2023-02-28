package bkauto1;
public class BookedItem {
    private String cargoType;
    private int amount;
    private double weight;
    
    public BookedItem() {

    }

    public BookedItem(String cargoType, int amount, double weight) {
        this.cargoType = cargoType;
        this.amount = amount;
        this.weight = weight;
    }
    
    public String getCargoType() {
        return cargoType;
    }
    
    public int getAmount() {
        return amount;
    }
    
    public double getWeight() {
        return weight;
    }
    
    public void setCargoType(String cargoType) {
        this.cargoType = cargoType;
    }
    
    public void setAmount(int amount) {
        this.amount = amount;
    }
    
    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void printDetails() {
        System.out.println("Cargo Type: " + cargoType);
        System.out.println("Amount: " + amount);
        System.out.println("Weight: " + weight);
    }
}
