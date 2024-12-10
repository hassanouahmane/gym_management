package application;

import java.util.Objects;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class User {
    private int userID;
    private String lName;
    private String fName;
    private String username;
    private String password;
    private char authority;

    // Constructors, getters, and setters
    public User() {
    	
    }

    public User(int userID, String lName, String fName, String username, String password, char authority) {
        this.userID = userID;
        this.lName = lName; 
        this.fName = fName;
        this.username = username;
        this.password = password;
        this.authority = authority;
    }

    // Getters and setters

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getLName() {
        return lName;
    }

    public void setLName(String lName) {
        this.lName = lName;
    }

    public String getFName() {
        return fName;
    }

    public void setFName(String fName) {
        this.fName = fName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public char getAuthority() {
        return authority;
    }

    public void setAuthority(char authority) {
        this.authority = authority;
    }

    // equals, hashCode, and toString methods

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return userID == user.userID &&
                authority == user.authority &&
                Objects.equals(lName, user.lName) &&
                Objects.equals(fName, user.fName) &&
                Objects.equals(username, user.username) &&
                Objects.equals(password, user.password);
    }
    
    public StringProperty fullNameProperty() {
        return new SimpleStringProperty(this.fName + " " + this.lName);
    }

    public StringProperty usernameProperty() {
        return new SimpleStringProperty(this.username);
    }

    public StringProperty passwordProperty() {
        return new SimpleStringProperty(this.password);
    }

    public ObjectProperty<Character> authorityProperty() {
        return new SimpleObjectProperty<>(this.authority);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userID, lName, fName, username, password, authority);
    }

    @Override
    public String toString() {
        return "User{" +
                "userID=" + userID +
                ", lName='" + lName + '\'' +
                ", fName='" + fName + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", authority=" + authority +
                '}';
    }
}
