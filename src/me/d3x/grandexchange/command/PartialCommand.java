package me.d3x.grandexchange.command;

import org.bukkit.entity.Player;

public class PartialCommand {
    
    private Player sender;
    private String itemName;
    private int quantity;
    private int price;
    private int type;
    
    public PartialCommand(Player sender, int type) {
        this.sender = sender;
        this.type = type;
        this.quantity = this.price = 0;
    }
    
    public boolean createTrade() {
        if(this.itemName != null && quantity > 0 && price > 0 && type != -1) {
            sender.performCommand("ge " + ((type == 0) ?  "buy " : "sell ") + itemName.toUpperCase() + " " + quantity + " " + price);
            return true;
        }
        return false;
    }
    
    public void setItem(String itemName) {
        this.itemName = itemName;
    }
    
    public String getItem() {
        return this.itemName;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public void addQuantity(int n) {
        this.quantity += n;
        this.quantity = Math.max(0, this.quantity);
    }
    
    public int getQuantity() {
        return this.quantity;
    }

    public void setPrice(int price) {
        this.price = price;
    }
    
    public void addPrice(int n) {
        this.price += n;
        this.price = Math.max(0, this.price);
    }
    
    public int getPrice() {
        return this.price;
    }

}
