package application;

import java.util.ArrayList;
import java.util.List;

public class Sport {
    private int sportID;
    private String name;
    private List<Plan> plans=new ArrayList<Plan>();

    // Constructors
    public Sport() {
    }

    public Sport(String name) {
        this.name = name;
    }

    public List<Plan> getPlans() {
		return plans;
	}

	public void setPlans(List<Plan> plans) {
		this.plans = plans;
	}

	// Getters and Setters
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

    // toString method for easy printing
    @Override
    public String toString() {
        return name;
    }
}

