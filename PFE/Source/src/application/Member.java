package application;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.Period;
import java.util.Date;

public class Member {
    private int memberID; // Primary Key
    private int age;
    private String lname;
    private String fname;
    private String phone;
    private String address;
    private String parent_fullname;
    private String parent_phone;
    private String gender;
    private LocalDate birthday;
    private String identity;
    private Timestamp subscription_date;
    private String note;
    private String email;
    
    //************
    
    private String fullname;
    private Date birth;
    private Date subscription;
    private String sport;
    private String stat;
    private int sale;
    
    public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public Date getBirth() {
		return birth;
	}

	public void setBirth(Date birth) {
		this.birth = birth;
	}

	public Date getSubscription() {
		return subscription;
	}

	public void setSubscription(Date subscription) {
		this.subscription = subscription;
	}

	public String getSport() {
		return sport;
	}

	public void setSport(String sport) {
		this.sport = sport;
	}

	public String getStat() {
		return stat;
	}

	public void setStat(String stat) {
		this.stat = stat;
	}

	public int getSale() {
		return sale;
	}

	public void setSale(int sale) {
		this.sale = sale;
	}

	//**********

    // Constructors

    public Member() {
        // Default constructor
    }

    public Member(String lname, String fname, String phone, String address, String parent_fullname, String parent_phone,
            String gender, LocalDate birthday, String identity, Timestamp subscription_date, String note, String email) {
        this.lname = lname;
        this.fname = fname;
        this.phone = phone;
        this.address = address;
        this.parent_fullname = parent_fullname;
        this.parent_phone = parent_phone;
        this.gender = gender;
        this.birthday = birthday;
        this.identity = identity;
        this.subscription_date = subscription_date;
        this.note = note;
        this.email = email;
        this.setAge();
    }

    // Getters and Setters

    public int getMemberID() {
        return memberID;
    }

    public void setMemberID(int memberID) {
        this.memberID = memberID;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getParent_fullname() {
        return parent_fullname;
    }

    public void setParent_fullname(String parent_fullname) {
        this.parent_fullname = parent_fullname;
    }

    public String getParent_phone() {
        return parent_phone;
    }

    public void setParent_phone(String parent_phone) {
        this.parent_phone = parent_phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate localDate) {
        this.birthday = localDate;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public Timestamp getSubscription_date() {
        return subscription_date;
    }

    public void setSubscription_date(Timestamp timestamp) {
        this.subscription_date = timestamp;
    }

    public String getDescription() {
        return note;
    }

    public void setDescription(String note) {
        this.note = note;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAge() {
        return age;
    }

    public void setAge() {
        if (birthday != null) {
            LocalDate currentDate = LocalDate.now();
            Period period = Period.between(birthday, currentDate);
            this.age = period.getYears();
        } else {
            this.age = 0;
        }
    }

    @Override
    public String toString() {
        return "Member [memberID=" + memberID + ", lname=" + lname + ", fname=" + fname + ", phone=" + phone
                + ", address=" + address + ", parent_fullname=" + parent_fullname + ", parent_phone=" + parent_phone
                + ", gender=" + gender + ", birthday=" + birthday + ", identity=" + identity + ", subscription_date="
                + subscription_date + ", description=" + note + ", email=" + email + "]";
    }
}
