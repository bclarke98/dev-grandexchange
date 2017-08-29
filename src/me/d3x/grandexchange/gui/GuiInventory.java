package me.d3x.grandexchange.gui;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.d3x.grandexchange.ExchangeHandler;
import me.d3x.grandexchange.GrandExchange;
import me.d3x.grandexchange.command.PartialCommand;
import me.d3x.grandexchange.trade.Trade;
import me.d3x.grandexchange.trade.TradeManager;

public class GuiInventory{
    
    private GrandExchange ge;
    private Inventory mainInventory;
    public HashMap<Player, Inventory> sellInventories;
    public HashMap<Player, Inventory> buyInventories;
    public HashMap<Player, Inventory> quantityInventories;
    public HashMap<Player, Inventory> priceInventories;
    public HashMap<Player, Inventory> tradeInventories;
    public HashMap<Player, PartialCommand> commands;
    
    public GuiInventory(GrandExchange ge) {
        this.ge = ge;
        //setting initial capacities to 1 to reduce overhead... not sure if it'll affect much
        sellInventories = new HashMap<Player, Inventory>(1);
        buyInventories = new HashMap<Player, Inventory>(1);
        quantityInventories = new HashMap<Player, Inventory>(1);
        priceInventories = new HashMap<Player, Inventory>(1);
        tradeInventories = new HashMap<Player, Inventory>(1);
        commands = new HashMap<Player, PartialCommand>(1);
        createMainInventory();
    }
    
    public void createMainInventory() {
        mainInventory = ge.getServer().createInventory(null, 9, ExchangeHandler.getInstance().getChatHandler().shortPrefix() + "Options Menu");
        ItemStack help = new ItemStack(Material.CYAN_SHULKER_BOX, 1);
        ItemMeta helpMeta = help.getItemMeta();
        helpMeta.setDisplayName("Help");
        help.setItemMeta(helpMeta);
        mainInventory.setItem(0, help);
        
        ItemStack list = new ItemStack(Material.ORANGE_SHULKER_BOX, 1);
        ItemMeta listMeta = list.getItemMeta();
        listMeta.setDisplayName("List Trades");
        list.setItemMeta(listMeta);
        mainInventory.setItem(6, list);
        
        ItemStack buy = new ItemStack(Material.LIME_SHULKER_BOX, 1);
        ItemMeta buyMeta = buy.getItemMeta();
        buyMeta.setDisplayName("Buy");
        ArrayList<String> buyLore = new ArrayList<String>();
        buyLore.add("Disabled. Please use /ge buy instead.");
        buyMeta.setLore(buyLore);
        buy.setItemMeta(buyMeta);
        mainInventory.setItem(7, buy);
        
        ItemStack sell = new ItemStack(Material.RED_SHULKER_BOX, 1);
        ItemMeta sellMeta = sell.getItemMeta();
        sellMeta.setDisplayName("Sell");
        sell.setItemMeta(sellMeta);
        mainInventory.setItem(8, sell);
        //display sign for amount selection
    }
    
    public void handleMainInventory(InventoryClickEvent event, Player player, ItemStack clicked) {
        event.setCancelled(true);
        if(clicked.getType().equals(Material.CYAN_SHULKER_BOX)) {
            player.closeInventory();
            player.performCommand("ge help");
        }
        if(clicked.getType().equals(Material.LIME_SHULKER_BOX)) {
            player.closeInventory();
            commands.put(player, new PartialCommand(player, 0));
        }
        if(clicked.getType().equals(Material.ORANGE_SHULKER_BOX)) {
            player.closeInventory();
            player.performCommand("ge list");
        }
        if(clicked.getType().equals(Material.RED_SHULKER_BOX)) {
            player.closeInventory();
            Inventory playerInv = ge.getServer().createInventory(null, 36, ExchangeHandler.getInstance().getChatHandler().shortPrefix() + "Click Item To Sell");
            for(ItemStack i : player.getInventory()) {
                if(i != null) {
                    playerInv.addItem(i);
                }
            }
            sellInventories.put(player, playerInv);
            commands.put(player, new PartialCommand(player, 1));
            player.openInventory(playerInv);
        }
    }
    
    public void handleBuyInventory(InventoryClickEvent event, Player player, ItemStack clicked) {
        event.setCancelled(true);
        player.closeInventory();
        
    }
    
    public void handleSellInventory(InventoryClickEvent event, Player player, ItemStack clicked) {
        event.setCancelled(true);
        player.closeInventory();
        commands.get(player).setItem(clicked.getType().toString());
        quantityInventories.put(player, createQuantityInventory(player, clicked));
        player.openInventory(quantityInventories.get(player));
    }
    
    public void handleQuantityInventory(InventoryClickEvent event, Player player, ItemStack clicked) {
        event.setCancelled(true);
        if(clicked.getType().equals(Material.CYAN_SHULKER_BOX)) {
            commands.get(player).addQuantity(64);
            quantityInventories.get(player).addItem(new ItemStack(Material.getMaterial(commands.get(player).getItem()), 64));
        }
        if(clicked.getType().equals(Material.LIGHT_BLUE_SHULKER_BOX)) {
            commands.get(player).addQuantity(10);
            quantityInventories.get(player).addItem(new ItemStack(Material.getMaterial(commands.get(player).getItem()), 10));
        }
        if(clicked.getType().equals(Material.WHITE_SHULKER_BOX)) {
            commands.get(player).addQuantity(1);
            quantityInventories.get(player).addItem(new ItemStack(Material.getMaterial(commands.get(player).getItem()), 1));
        }
        if(clicked.getType().equals(Material.RED_SHULKER_BOX)) {
            commands.get(player).addQuantity(-64);
            removeFromInventory(player, quantityInventories.get(player), commands.get(player).getItem(), 64);
        }
        if(clicked.getType().equals(Material.MAGENTA_SHULKER_BOX)) {
            commands.get(player).addQuantity(-10);
            removeFromInventory(player, quantityInventories.get(player), commands.get(player).getItem(), 10);
        }
        if(clicked.getType().equals(Material.PINK_SHULKER_BOX)) {
            commands.get(player).addQuantity(-1);
            removeFromInventory(player, quantityInventories.get(player), commands.get(player).getItem(), 1);
        }
        if(clicked.getType().equals(Material.PAPER)) {
            player.closeInventory();
            priceInventories.put(player, createPriceInventory());
            player.openInventory(priceInventories.get(player));
        }
    }
    
    public void handlePriceInventory(InventoryClickEvent event, Player player, ItemStack clicked) {
        event.setCancelled(true);
        if(clicked.getType().equals(Material.CYAN_SHULKER_BOX)) {
            commands.get(player).addPrice(50);
        }
        if(clicked.getType().equals(Material.LIGHT_BLUE_SHULKER_BOX)) {
            commands.get(player).addPrice(20);
        }
        if(clicked.getType().equals(Material.WHITE_SHULKER_BOX)) {
            commands.get(player).addPrice(1);
        }
        if(clicked.getType().equals(Material.RED_SHULKER_BOX)) {
            commands.get(player).addPrice(-50);
        }
        if(clicked.getType().equals(Material.MAGENTA_SHULKER_BOX)) {
            commands.get(player).addPrice(-20);
        }
        if(clicked.getType().equals(Material.PINK_SHULKER_BOX)) {
            commands.get(player).addPrice(-1);
        }
        
        ItemStack q = priceInventories.get(player).getItem(0);
        ItemMeta qm = q.getItemMeta();
        qm.setDisplayName("Unit Price");
        ArrayList<String> unitLoreList = new ArrayList<String>(1);
        unitLoreList.add(commands.get(player).getPrice() + "gp");
        qm.setLore(unitLoreList);
        q.setItemMeta(qm);
        
        ItemStack t = priceInventories.get(player).getItem(1);
        ItemMeta tm = t.getItemMeta();
        tm.setDisplayName("Total Price");
        ArrayList<String> totalLoreList = new ArrayList<String>(1);
        totalLoreList.add((commands.get(player).getPrice() * commands.get(player).getQuantity()) + "gp");
        tm.setLore(totalLoreList);
        t.setItemMeta(tm);
        
        if(clicked.getType().equals(Material.PAPER)) {
            if(!commands.get(player).createTrade()) {
                ExchangeHandler.getInstance().getChatHandler().sendErrorMessage(player, "Couldn't create trade offer!");
            }
            player.closeInventory();
        }
    }
    
    public void openTradesInventory(Player player) {
        player.closeInventory();
        tradeInventories.put(player, createListInventory(player));
        player.openInventory(tradeInventories.get(player));
    }
    
    public void handleTradesInventory(InventoryClickEvent event, Player player, ItemStack clicked) {
        event.setCancelled(true);
        int index = event.getSlot();
        ArrayList<Trade> trades = TradeManager.getInstance().getPlayerTradeMap().get(player.getUniqueId().toString());
        if(trades != null && index < trades.size()) {
            trades.get(index).cancel();
            Trade t = trades.remove(index);
            TradeManager.getInstance().removeFromTradeMap(t);
            player.closeInventory();
        }
    }
    
    private Inventory createQuantityInventory(Player player, ItemStack clicked){
        Inventory qInv = ge.getServer().createInventory(null, 36, ExchangeHandler.getInstance().getChatHandler().shortPrefix() + "Select Quantity To Sell");
        
        ItemStack q = new ItemStack(clicked);
        qInv.setItem(0, q);
        commands.get(player).setQuantity(q.getAmount());
        
        ItemStack addStack = new ItemStack(Material.CYAN_SHULKER_BOX);
        ItemMeta addMeta = addStack.getItemMeta();
        addMeta.setDisplayName("+64");
        addStack.setItemMeta(addMeta);
        
        ItemStack tenStack = new ItemStack(Material.LIGHT_BLUE_SHULKER_BOX);
        ItemMeta tenMeta = tenStack.getItemMeta();
        tenMeta.setDisplayName("+10");
        tenStack.setItemMeta(tenMeta);
        
        ItemStack oneStack = new ItemStack(Material.WHITE_SHULKER_BOX);
        ItemMeta oneMeta = oneStack.getItemMeta();
        oneMeta.setDisplayName("+1");
        oneStack.setItemMeta(oneMeta);
        
        ItemStack removeStack = new ItemStack(Material.RED_SHULKER_BOX);
        ItemMeta removeMeta = removeStack.getItemMeta();
        removeMeta.setDisplayName("-64");
        removeStack.setItemMeta(removeMeta);
        
        ItemStack rTenStack = new ItemStack(Material.MAGENTA_SHULKER_BOX);
        ItemMeta rTenMeta = rTenStack.getItemMeta();
        rTenMeta.setDisplayName("-10");
        rTenStack.setItemMeta(rTenMeta);
        
        ItemStack rOneStack = new ItemStack(Material.PINK_SHULKER_BOX);
        ItemMeta rOneMeta = rOneStack.getItemMeta();
        rOneMeta.setDisplayName("-1");
        rOneStack.setItemMeta(rOneMeta);
        
        ItemStack confirm = new ItemStack(Material.PAPER);
        ItemMeta cMeta = confirm.getItemMeta();
        cMeta.setDisplayName("Confirm");
        confirm.setItemMeta(cMeta);
        
        qInv.setItem(8, confirm);
        qInv.setItem(24, oneStack);
        qInv.setItem(25, tenStack);
        qInv.setItem(26, addStack);
        qInv.setItem(33, rOneStack);
        qInv.setItem(34, rTenStack);
        qInv.setItem(35, removeStack);
        return qInv;
    }
    
    private Inventory createPriceInventory(){
        Inventory qInv = ge.getServer().createInventory(null, 9, ExchangeHandler.getInstance().getChatHandler().shortPrefix() + "Select Unit Price");
        
        ItemStack q = new ItemStack(Material.GOLD_INGOT);
        ItemMeta qm = q.getItemMeta();
        qm.setDisplayName("Unit Price");
        ArrayList<String> unitLoreList = new ArrayList<String>(1);
        unitLoreList.add("0gp");
        qm.setLore(unitLoreList);
        q.setItemMeta(qm);
        qInv.setItem(0, q);
        
        ItemStack t = new ItemStack(Material.DIAMOND);
        ItemMeta tm = t.getItemMeta();
        tm.setDisplayName("Total Price");
        ArrayList<String> totalLoreList = new ArrayList<String>(1);
        totalLoreList.add("0gp");
        tm.setLore(totalLoreList);
        t.setItemMeta(tm);
        qInv.setItem(1, t);
        
        ItemStack addStack = new ItemStack(Material.CYAN_SHULKER_BOX);
        ItemMeta addMeta = addStack.getItemMeta();
        addMeta.setDisplayName("+50gp");
        addStack.setItemMeta(addMeta);
        
        ItemStack tenStack = new ItemStack(Material.LIGHT_BLUE_SHULKER_BOX);
        ItemMeta tenMeta = tenStack.getItemMeta();
        tenMeta.setDisplayName("+20gp");
        tenStack.setItemMeta(tenMeta);
        
        ItemStack oneStack = new ItemStack(Material.WHITE_SHULKER_BOX);
        ItemMeta oneMeta = oneStack.getItemMeta();
        oneMeta.setDisplayName("+1gp");
        oneStack.setItemMeta(oneMeta);
        
        ItemStack removeStack = new ItemStack(Material.RED_SHULKER_BOX);
        ItemMeta removeMeta = removeStack.getItemMeta();
        removeMeta.setDisplayName("-50gp");
        removeStack.setItemMeta(removeMeta);
        
        ItemStack rTenStack = new ItemStack(Material.MAGENTA_SHULKER_BOX);
        ItemMeta rTenMeta = rTenStack.getItemMeta();
        rTenMeta.setDisplayName("-20gp");
        rTenStack.setItemMeta(rTenMeta);
        
        ItemStack rOneStack = new ItemStack(Material.PINK_SHULKER_BOX);
        ItemMeta rOneMeta = rOneStack.getItemMeta();
        rOneMeta.setDisplayName("-1gp");
        rOneStack.setItemMeta(rOneMeta);
        
        ItemStack confirm = new ItemStack(Material.PAPER);
        ItemMeta cMeta = confirm.getItemMeta();
        cMeta.setDisplayName("Confirm");
        confirm.setItemMeta(cMeta);
        
        qInv.setItem(8, confirm);
        qInv.setItem(2, oneStack);
        qInv.setItem(3, tenStack);
        qInv.setItem(4, addStack);
        qInv.setItem(5, rOneStack);
        qInv.setItem(6, rTenStack);
        qInv.setItem(7, removeStack);
        return qInv;
    }
    
    private Inventory createListInventory(Player player) {
        ArrayList<Trade> tradeList = TradeManager.getInstance().getPlayerTradeMap().get(player.getUniqueId().toString());
        int amt = ((tradeList.size() - 1 / 9) + 1) * 9;
        Inventory tInv = ge.getServer().createInventory(null, amt, ExchangeHandler.getInstance().getChatHandler().shortPrefix() + "Trade Menu");
        if(tradeList != null) {
            for(int i = 0; i < tradeList.size(); i++) {
                Trade t = tradeList.get(i);
                ItemStack item = new ItemStack(Material.getMaterial(t.getItemName()));
                ItemMeta meta = item.getItemMeta();
                ArrayList<String> loreList = new ArrayList<String>();
                loreList.add(t.getType() == 0 ? "\2479\247l[ BUY ]" : "\2479\247l[ SELL ]");
                loreList.add("\2477Price: " + t.getPrice() + "gp");
                loreList.add("\2477Quantity: " + t.getQuantity());
                loreList.add("\2474Click to cancel trade.");
                meta.setLore(loreList);
                item.setItemMeta(meta);
                tInv.setItem(i, item);
            }
        }
        return tInv;
    }
    
    public void removeFromInventory(Player player, Inventory inventory, String material, int itemsLeftToRemove) {
        for(int i = inventory.getStorageContents().length - 1; i >= 0; i--){
            ItemStack stack = inventory.getStorageContents()[i];
            if(stack != null && stack.getType().equals(Material.getMaterial(material.toUpperCase()))){
                itemsLeftToRemove -= stack.getAmount();
                if(itemsLeftToRemove < 0){
                    stack.setAmount(-itemsLeftToRemove);
                    player.updateInventory();
                    break;
                }else{
                    stack.setAmount(0);
                    player.updateInventory();
                }
            }
        }
    }
    
    public Inventory getMainMenuInventory() {
        return this.mainInventory;
    }
    
    

}
