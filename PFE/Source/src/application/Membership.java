package application;

import java.sql.Date;
import java.time.LocalDate;

public class Membership {

	private int contractID;
	private int memberID; 
	private int userID;
	private int sportID;
	private double price;
	private double payed;
	private Date createdAt;
	private LocalDate fromDate = LocalDate.now();
	private Date toDate;
	private String note;
	private String member;
	private String sport;
	private String duration;
	private String finishedAt;
	private Date fromDate1;

	public Membership(int contractID, int memberID, int userID, int sportID, double price, double payed, Date createdAt,
			LocalDate fromDate, Date toDate, String note) {
		this.contractID = contractID;
		this.memberID = memberID;
		this.userID = userID;
		this.price = price;
		this.payed = payed;
		this.createdAt = createdAt;
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.note = note;
	}

	public Membership() {

	}

	public int getContractID() {
		return contractID;
	}

	public void setContractID(int contractID) {
		this.contractID = contractID;
	}

	public int getMemberID() {
		return memberID;
	}

	public void setMemberID(int memberID) {
		this.memberID = memberID;
	}

	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	public int getSportID() {
		return sportID;
	}

	public void setSportID(int sportID) {
		this.sportID = sportID;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public double getPayed() {
		return payed;
	}

	public void setPayed(double payed) {
		this.payed = payed;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDate getFromDate() {
		return fromDate;
	}

	public void setFromDate(LocalDate fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getMember() {
		return member;
	}

	public void setMember(String member) {
		this.member = member;
	}

	public String getSport() {
		return sport;
	}

	public void setSport(String sport) {
		this.sport = sport;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public Date getFromDate1() {
		return fromDate1;
	}

	public void setFromDate1(Date fromDate1) {
		this.fromDate1 = fromDate1;
	}

	public String getFinishedAt() {
		return finishedAt;
	}

	public void setFinishedAt(String finishedAt) {
		this.finishedAt = finishedAt;
	}

	@Override
	public String toString() {
		return "Membership{" + "contractID=" + contractID + ", memberID=" + memberID + ", sportID=" + sportID
				+ ", userID=" + userID + ", price=" + price + ", payed=" + payed + ", createdAt=" + createdAt
				+ ", fromDate=" + fromDate + ", toDate=" + toDate + ", note='" + note + '\'' + '}';
	}
	
	
}
