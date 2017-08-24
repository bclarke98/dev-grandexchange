package me.d3x.grandexchange.engine;

import java.util.UUID;

import me.d3x.grandexchange.ExchangeHandler;

public class Trade {
	
	private String sellerUID;
	private int quantity;
	private int price;
	private int type;
	private UUID uuid;
    
	public Trade(String sellerUID, int quantity, int price, int type) {
		this.sellerUID = sellerUID;
		this.quantity = quantity;
		this.price = price;
		this.type = type;
        this.uuid = UUID.fromString(sellerUID);
	}

	public String getSellerUID() {
		return sellerUID;
	}
    
    public UUID getUUID(){
        return uuid;
    }

	public int getQuantity() {
		return quantity;
	}

    public void reduceQuantity(Trade other){
        this.quantity -= other.getQuantity();
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
    
    public int compareTo(Trade other){
        return (int)(this.getUnitPrice() - other.getUnitPrice());
    }
    
	/**
	 * @param other - trade object that isn't on the marketplace yet
	 */
    public boolean canCompleteTrade(Trade other){
        return this.getType() != other.getType() && (this.getType() == 0 ? this.compareTo(other) <= 0 : this.compareTo(other) >= 0);
    }

    public void completeTrade(Trade other){
        this.reduceQuantity(other);
        other.reduceQuantity(this);
        //if getType() == 0 ...
    }
    
}
