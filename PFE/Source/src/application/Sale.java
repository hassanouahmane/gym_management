package application;

import java.time.LocalDate;

public class Sale {

	private int saleID;
	private int equipementID;
	private String equipement;
	private int memberID;
	private String member;
	private int quantity;
	private double price;
	private double payed;
	private LocalDate createdAt;

	public Sale(String equipement, String member, int quantity, double price, double payed) {
		this.equipement = equipement;
		this.member = member;
		this.quantity = quantity;
		this.price = price;
		this.payed = payed;
	}

	public Sale() {
		
	}

	public int getSaleID() {
		return saleID;
	}

	public void setSaleID(int saleID) {
		this.saleID = saleID;
	}

	public int getEquipementID() {
		return equipementID;
	}

	public void setEquipementID(int equipementID) {
		this.equipementID = equipementID;
	}

	public int getMemberID() {
		return memberID;
	}

	public void setMemberID(int memberID) {
		this.memberID = memberID;
	}

	public LocalDate getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDate createdAt) {
		this.createdAt = createdAt;
	}

	public String getEquipement() {
		return equipement;
	}

	public void setEquipement(String equipement) {
		this.equipement = equipement;
	}

	public String getMember() {
		return member;
	}

	public void setMember(String member) {
		this.member = member;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
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

	@Override
	public String toString() {
		return "Sale [equipement=" + equipement + ", member=" + member + ", quantity=" + quantity + ", price=" + price
				+ ", payed=" + payed + "]";
	}

}
