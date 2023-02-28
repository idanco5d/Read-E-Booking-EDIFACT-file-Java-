package bkauto1;
import java.util.ArrayList;

public class Booking {
    private String otherReference;
    private ArrayList<Customer> customers;
    private ArrayList<BookedItem> bookedItems;
    private String pol;
    private String pod;
    
    public Booking() {
        customers = new ArrayList<>();
        bookedItems = new ArrayList<>();
    }

    public Booking(String otherReference, ArrayList<Customer> customers, ArrayList<BookedItem> bookedItems, String pol, String pod) {
        this.otherReference = otherReference;
        this.customers = customers;
        this.bookedItems = bookedItems;
        this.pol = pol;
        this.pod = pod;
    }
    
    public String getOtherReference() {
        return otherReference;
    }
    
    public ArrayList<BookedItem> getBookedItems() {
        return bookedItems;
    }

    public ArrayList<Customer> getCustomers() {
        return customers;
    }

    public String getPol() {
        return pol;
    }

    public String getPod() {
        return pod;
    }
    
    public void setOtherReference(String otherReference) {
        this.otherReference = otherReference;
    }
    
    public void setBookedItems(ArrayList<BookedItem>  bookedItems) {
        this.bookedItems = bookedItems;
    }

    public void setCustomers (ArrayList<Customer> customers) {
        this.customers = customers;
    }

    public void setPol (String pol) {
        this.pol = pol;
    }

    public void setPod (String pod) {
        this.pod = pod;
    }

    public void addCustomer (Customer customer) {
        customers.add(customer);
    }

    public void addBookedItem (BookedItem bookedItem) {
        ArrayList<BookedItem> bookedItems = getBookedItems();
        for (BookedItem item : bookedItems) {
            if (item.getCargoType().equals(bookedItem.getCargoType())) {
                item.setAmount(item.getAmount()+1);
                item.setWeight(item.getWeight() + bookedItem.getWeight());
                return;
            }
        }
        bookedItems.add(bookedItem);
    }

    public void printBookingDetails() {
        System.out.println("Booking details:");
        System.out.println("Other reference: " + otherReference);
        System.out.println("Customers:");
        for (Customer customer : customers) {
            customer.printDetails();
        }
        System.out.println("Booked items:");
        for (BookedItem item : bookedItems) {
            item.printDetails();
        }
        System.out.println("POL: " + pol);
        System.out.println("POD: " + pod);
    }
    
}