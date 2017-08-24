package me.d3x.grandexchange.engine;

public class Trade {
	
	private String sellerUID;
	private int quantity;
	private int price;
	private int type;
	
	public Trade(String sellerUID, int quantity, int price, int type) {
		this.sellerUID = sellerUID;
		this.quantity = quantity;
		this.price = price;
		this.type = type;
	}

	public String getSellerUID() {
		return sellerUID;
	}

	public int getQuantity() {
		return quantity;
	}

	public int getPrice() {
		return price;
	}
	
	/**
	 * @return 0 if buying, 1 if selling
	 */
	public int getType() {
		return this.type;
	}
	
	public double getUnitPrice() {
		return (double)(price / quantity);
	}

}
