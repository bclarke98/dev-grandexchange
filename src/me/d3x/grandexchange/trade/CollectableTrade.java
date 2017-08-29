package me.d3x.grandexchange.trade;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.d3x.grandexchange.ExchangeHandler;

public class CollectableTrade {
    
    private String uuid;
    private String itemName;
    private ItemStack[] itemPayout;
    private int itemCount;
    private double moneyPayout;
    private boolean cancelled;
    
    public CollectableTrade(String uuid, String itemName, ItemStack[] itemPayout, double moneyPayout, boolean canceled) {
        this.uuid = uuid;
        this.itemName = itemName;
        this.itemPayout = itemPayout;
        this.itemCount = 0;
        if(itemPayout != null) {
            for(ItemStack i : this.itemPayout) {
                itemCount += i.getAmount();
            }
        }
        this.moneyPayout = moneyPayout;
        ExchangeHandler.getInstance().getChatHandler().sendChatMessage(getPlayer(), "One of your trades has been " + (cancelled ? "cancelled." : "completed."));
        ExchangeHandler.getInstance().getChatHandler().sendChatMessage(getPlayer(), "Use \"/ge collect\" to collect.");
    }
    
    public CollectableTrade(String uuid, String itemName, ItemStack[] itemPayout, double moneyPayout) {
        this.uuid = uuid;
        this.itemName = itemName;
        this.itemPayout = itemPayout;
        this.itemCount = 0;
        if(itemPayout != null) {
            for(ItemStack i : this.itemPayout) {
                itemCount += i.getAmount();
            }
        }
        this.moneyPayout = moneyPayout;
        this.cancelled = false;
        ExchangeHandler.getInstance().getChatHandler().sendChatMessage(getPlayer(), "One of your trades has been completed.");
        ExchangeHandler.getInstance().getChatHandler().sendChatMessage(getPlayer(), "Use \"/ge collect\" to collect.");
    }
    
    public void collect() {
        if(this.itemPayout != null) {
            int total = 0;
            for(ItemStack i : this.itemPayout) {
                total += i.getAmount();
                getPlayer().getInventory().addItem(i);
            }
            ExchangeHandler.getInstance().getChatHandler().sendChatMessage(getPlayer(), this.cancelled ? ("You have been refunded " + total + " " + itemName) : ("You have purchased " + total + " " + itemName));
            if(this.moneyPayout >= 1) {
                ExchangeHandler.getInstance().getEco().addToBalance(getPlayer(), (int) moneyPayout);
                ExchangeHandler.getInstance().getChatHandler().sendChatMessage(getPlayer(), "You have been refunded " + moneyPayout + "gp");
            }
        }else {
            ExchangeHandler.getInstance().getEco().addToBalance(getPlayer(), (int) moneyPayout);
            ExchangeHandler.getInstance().getChatHandler().sendChatMessage(getPlayer(), this.cancelled ? ("You have been refunded" + moneyPayout + "gp") : ("You have been paid " + moneyPayout + "gp for selling " + itemName));
        }
    }
    
    public String getPrinted() {
        return uuid + "/" + itemName + "/" + itemCount + "/" + moneyPayout;
    }

    public Player getPlayer() {
        return TradeManager.getInstance().getGrandExchange().getServer().getPlayer(UUID.fromString(this.uuid));
    }

    public String getItemName() {
        return itemName;
    }

    public ItemStack[] getItemPayout() {
        return itemPayout;
    }

    public double getMoneyPayout() {
        return moneyPayout;
    }
    
}
