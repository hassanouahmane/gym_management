package application;

public class Coach {

    private Integer coachID; 
    private Integer sportID; 
    private String fname;
    private String lname;
    private String phone;
    private String identity;
	private Integer sportIdField;
    private String sportName;


    public Integer getCoachID() {
        return coachID;
    }

    public void setCoachID(Integer coachID) {
        this.coachID = coachID;
    }

    public Integer getsportIdField() {
        return sportIdField;
    }

    public void setsportIdField(Integer sportIdField) {
        this.sportIdField = sportIdField;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }
    public String getSportName() {
        return sportName;
    }

    public void setSportName(String sportName) {
        this.sportName = sportName;
    }
}