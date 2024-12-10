package application;

public class Plan {
    private int planID;
    private int sportID;
    private String name;
    private double price;
    private int duration;

    // Constructors
    public Plan() {
    }

    public Plan(int planID, int sportID, String name, double price, int duration) {
        this.planID = planID;
        this.sportID = sportID;
        this.name = name;
        this.price = price;
        this.duration = duration;
    }

    // Getters and Setters
    public int getPlanID() {
        return planID;
    }

    public void setPlanID(int planID) {
        this.planID = planID;
    }

    public int getSportID() {
        return sportID;
    }

    public void setSportID(int sportID) {
        this.sportID = sportID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    // Override toString for easier debugging
    @Override
    public String toString() {
        return "Plan{" +
                "planID=" + planID +
                ", sportID=" + sportID +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", duration=" + duration +
                '}';
    }
}
