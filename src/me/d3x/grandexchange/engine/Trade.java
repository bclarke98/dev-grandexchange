package me.d3x.grandexchange.engine;

import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import me.d3x.grandexchange.ExchangeHandler;

public class Trade {
	
	private String sellerUID;
	private int quantity;
	private int originalQuantity;
	private double price;
    private double unitPrice;
	private int type;
	private UUID uuid;
    
	public Trade(String sellerUID, int quantity, double unitPrice, int type) {
		this.sellerUID = sellerUID;
		this.quantity = quantity;
		this.originalQuantity = quantity;
		this.price = unitPrice * quantity;
		this.unitPrice = unitPrice;
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
	
	public int getOriginalQuantity() {
	    return originalQuantity;
	}

    public void reduceQuantity(Trade other, String itemName){
        this.quantity -= (other.getQuantity() <= 0) ? other.getOriginalQuantity() : other.getQuantity();
        this.quantity = Math.max(0, this.quantity);
        int delta = this.originalQuantity - (this.quantity > 0 ? this.quantity : 0);
        this.price = this.quantity * this.getUnitPrice();
        if(this.getType() == 0) {
            ItemStack[] items = new ItemStack[delta / 64 + 1];
            int itemsLeft = delta;
            for(int i = 0; i < items.length; i++) {
                items[i] = itemsLeft > 64 ? new ItemStack(Material.getMaterial(itemName), 64) : new ItemStack(Material.getMaterial(itemName), itemsLeft);
                itemsLeft -= 64;
            }
            ExchangeHandler.getInstance().getPlayerByUUID(getUUID()).getInventory().addItem(items);
            double offset = this.getUnitPrice() - other.getUnitPrice();
            double excess = offset * delta;
            double buyPrice = (this.getUnitPrice() * delta) - ((excess > 0) ? excess : 0);
            ExchangeHandler.getInstance().getChatHandler().sendChatMessage(getUUID(), "You have bought " + delta + " " + itemName  + " for " + buyPrice);
            //player.addMoney(excess);
        }else {
            //pay the man
            ExchangeHandler.getInstance().getChatHandler().sendChatMessage(getUUID(), "You have sold " + delta + " " + itemName + " for " + (this.getUnitPrice() * delta));
        }
        if(this.quantity > 0) {
            this.originalQuantity = this.quantity;
        }
    }

	public double getPrice() {
		return price;
	}
	
	/**
	 * @return 0 if buying, 1 if selling
	 */
	public int getType() {
		return this.type;
	}
	
	public double getUnitPrice() {
		return this.unitPrice;
	}
    
    public int compareTo(Trade other){
        return (getType() == 0) ? -(int)(this.getUnitPrice() - other.getUnitPrice()) : (int)(this.getUnitPrice() - other.getUnitPrice());
    }
    
	/**
	 * @param other - trade object that isn't on the marketplace yet
	 */
    public boolean canCompleteTrade(Trade other){
        return this.getType() != other.getType() && (this.getType() == 0 ? this.compareTo(other) >= 0 : this.compareTo(other) <= 0);
    }

    
}
