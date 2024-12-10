package application;

public class Equipement {

    private int equipementID;
    private String name;
    private int quantity;
    private double price;
    private String description;

    // Constructor
    public Equipement(int equipementID, String name, int quantity, double price, String description) {
        this.equipementID = equipementID;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.description = description;
    }

    // Getters
    public int getEquipementID() {
        return equipementID;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }
}
