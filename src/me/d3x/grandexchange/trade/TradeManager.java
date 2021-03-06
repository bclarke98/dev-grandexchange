package me.d3x.grandexchange.trade;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.d3x.grandexchange.ExchangeHandler;
import me.d3x.grandexchange.GrandExchange;

public class TradeManager {

    private static volatile TradeManager instance;

    public static TradeManager getInstance() {
        TradeManager result = instance;
        if (result == null) {
            synchronized (TradeManager.class) {
                result = instance;
                if (result == null) {
                    result = instance = new TradeManager();
                }
            }
        }
        return result;
    }

    private HashMap<String, ArrayList<ArrayList<Trade>>> tradeMap;
    private HashMap<String, ArrayList<Trade>> playerTradeMap;
    private HashMap<String, ArrayList<CollectableTrade>> collectableTradeMap;
    private GrandExchange ge;

    public void loadTradeMapFromFile(GrandExchange ge) {
        this.ge = ge;
        try {
            tradeMap = new HashMap<String, ArrayList<ArrayList<Trade>>>();
            playerTradeMap = new HashMap<String, ArrayList<Trade>>();
            
            File tradeData = new File(ge.getDataFolder() + "/trade/trades.dat");
            Scanner reader = new Scanner(tradeData); // TODO replace with BufferedReader if load time sucks
            String currentItem = "";
            int totalTrades = 0;
            while (reader.hasNextLine()) {
                String curline = reader.nextLine();
                if (curline.startsWith(";")) {
                    currentItem = curline.substring(1);
                    ArrayList<ArrayList<Trade>> arr = new ArrayList<ArrayList<Trade>>();
                    arr.add(new ArrayList<Trade>());
                    arr.add(new ArrayList<Trade>());
                    tradeMap.put(currentItem, arr);
                } else if (curline.startsWith(">")) {
                    String[] as = curline.substring(1).split("/");
                    Trade newTrade = new Trade(as[0], currentItem, Integer.parseInt(as[1]),
                            Double.parseDouble(as[2]) / Integer.parseInt(as[1]), Integer.parseInt(as[3]));
                    addPlayerTrade(as[0], newTrade);
                    tradeMap.get(currentItem).get(Integer.parseInt(as[3])).add(newTrade);
                    totalTrades++;
                }
            }
            reader.close();
            GrandExchange.print("Total trades loaded: " + totalTrades);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        try {
            collectableTradeMap = new HashMap<String, ArrayList<CollectableTrade>>();
            File collectionsData = new File(ge.getDataFolder() + "/trade/collections.dat");
            int totalCollections = 0;
            Scanner reader = new Scanner(collectionsData);
            while(reader.hasNextLine()) {
                String curline = reader.nextLine();
                String[] as = curline.split("/");
                if(as.length > 3) {
                    totalCollections++;
                    String uuid = as[0];
                    String itemName = as[1];
                    int itemCount = Integer.parseInt(as[2]);
                    ItemStack [] itemPayout = null;
                    if(itemCount > 0) {
                        itemPayout = new ItemStack[itemCount / 64 + 1];
                        int itemsLeft = itemCount;
                        for(int i = 0; i < itemPayout.length; i++) {
                            itemPayout[i] = itemsLeft > 64 ? new ItemStack(Material.getMaterial(itemName), 64) : new ItemStack(Material.getMaterial(itemName), itemsLeft);
                            itemsLeft -= 64;
                        }
                    }
                    double moneyPayout = Double.parseDouble(as[3]);
                    addNewCollectableTrade(uuid, itemName, itemPayout, moneyPayout);
                }
            }
            GrandExchange.print("Total collectable trades loaded: " + totalCollections);
            reader.close();
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void registerTrade(String itemName, String uid, int quantity, double price, int type) {
        Trade newTrade = new Trade(uid, itemName, quantity, price, type);
        if(tradeMap.get(itemName) == null) {
            ArrayList<ArrayList<Trade>> arr = new ArrayList<ArrayList<Trade>>();
            arr.add(new ArrayList<Trade>());
            arr.add(new ArrayList<Trade>());
            tradeMap.put(itemName, arr);
        }
        ArrayList<Trade> potentialTrades = tradeMap.get(itemName).get((type + 1) % 2);
        if (potentialTrades.size() > 0 && potentialTrades.get(0).canCompleteTrade(newTrade)) {
            newTrade.reduceQuantity(potentialTrades.get(0), itemName);
            if (newTrade.getQuantity() > 0) {
                tradeMap.get(itemName).get(type).add(newTrade);
                addPlayerTrade(uid, newTrade, true);
                GrandExchange.print("Added trade " + (type == 0 ? "[BUY]" : "[SELL]") + " [" + itemName + "]: U[" + uid + "] Q[" + newTrade.getQuantity() + "] P[" + price + "]");
            }
            potentialTrades.get(0).reduceQuantity(newTrade, itemName);
            if (potentialTrades.get(0).getQuantity() <= 0) {
                potentialTrades.remove(0);
            }
            for(int i = 0; i < 2; i++) {
                tradeMap.get(itemName).get(i).sort((Trade t1, Trade t2)->t1.compareTo(t2));
            }
        } else {
            tradeMap.get(itemName).get(type).add(newTrade);
            addPlayerTrade(uid, newTrade, true);
            GrandExchange.print("Added trade " + (type == 0 ? "[BUY]" : "[SELL]") + " [" + itemName + "]: U[" + uid + "] Q[" + quantity + "] P[" + price + "]");
            
            tradeMap.get(itemName).get(type).sort((Trade t1, Trade t2)->t1.compareTo(t2));
        }

    }

    public void saveTradeMapToFile(GrandExchange ge) {
        try {
            File tradeData = new File(ge.getDataFolder() + "/trade/trades.dat");
            PrintWriter writer = new PrintWriter(tradeData, "UTF-8");
            int totalTrades = 0;
            for (String s : tradeMap.keySet()) {
                writer.write(";" + s + "\n");
                for (int i = 0; i < 2; i++) {
                    for (Trade t : tradeMap.get(s).get(i)) {
                        totalTrades++;
                        writer.write(">" + t.getSellerUID() + "/" + t.getQuantity() + "/" + t.getPrice() + "/" + i + "\n");
                    }
                }
                writer.write("\n");
            }
            writer.flush();
            writer.close();
            GrandExchange.print("Total trades saved: " + totalTrades);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            File collectionsData = new File(ge.getDataFolder() + "/trade/collections.dat");
            PrintWriter writer = new PrintWriter(collectionsData, "UTF-8");
            int totalCollections = 0;
            for(ArrayList<CollectableTrade> arr : collectableTradeMap.values()) {
                for(CollectableTrade ct : arr) {
                    writer.write(ct.getPrinted() + "\n");
                    totalCollections++;
                }
            }
            writer.flush();
            writer.close();
            GrandExchange.print("Total collectable trades saved: " + totalCollections);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void resetTrades() {
        for (String s : tradeMap.keySet()) {
            for (int i = 0; i < 2; i++) {
                tradeMap.get(s).get(i).clear();
            }
        }
        GrandExchange.print("Cleared Trades.");
    }
    
    public void printTrades() {
        for (String s : tradeMap.keySet()) {
            for (int i = 0; i < 2; i++) {
                for (Trade t : tradeMap.get(s).get(i)) {
                    GrandExchange.print(">" + t.getSellerUID() + "/" + t.getPrice() + "/" + t.getQuantity() + "/" + i);
                }
            }
        }
    }
    
    public void addPlayerTrade(String uuid, Trade trade, boolean alert) {
        Player player = ge.getServer().getPlayer(UUID.fromString(uuid));
        if(playerTradeMap.get(player.getUniqueId().toString()) == null) {
            playerTradeMap.put(player.getUniqueId().toString(), new ArrayList<Trade>());
        }
        playerTradeMap.get(player.getUniqueId().toString()).add(trade);
        if(alert) {
            ExchangeHandler.getInstance().getChatHandler().sendChatMessage(player, "Added trade " + (trade.getType()== 0 ? "[\2472BUY\247a]" : "[SELL]") + 
                    " [\2472" + trade.getQuantity() + "\247a] [\2472" + trade.getItemName() + "\247a] for [\2472" + trade.getPrice() + "\247a] gp");
        }
    }
    
    public void addPlayerTrade(String uuid, Trade trade) {
        addPlayerTrade(uuid, trade, false);
    }

    public GrandExchange getGrandExchange() {
        return ge;
    }
    
    public HashMap<String, ArrayList<CollectableTrade>> getCollectableTrades() {
        return this.collectableTradeMap;
    }
    
    public HashMap<String, ArrayList<Trade>> getPlayerTradeMap() {
        return this.playerTradeMap;
    }
    
    public HashMap<String, ArrayList<ArrayList<Trade>>> getTradeMap(){
        return this.tradeMap;
    }
    
    public void removeFromTradeMap(Trade t) {
        if(tradeMap.get(t.getItemName()) != null) {
            ArrayList<Trade> list = tradeMap.get(t.getItemName()).get(t.getType());
            if(list.remove(t)) {
                System.out.println("Successful removal");
            }
        }
    }
    
    public void addNewCollectableTrade(String uuid, String itemName, ItemStack[] itemPayout, double moneyPayout) {
        addNewCollectableTrade(uuid, itemName, itemPayout, moneyPayout, false);
    }
    
    public void addNewCollectableTrade(String uuid, String itemName, ItemStack[] itemPayout, double moneyPayout, boolean cancelled) {
        Player player = ge.getServer().getPlayer(UUID.fromString(uuid));
        if(this.collectableTradeMap.get(player.getUniqueId().toString()) == null) {
            this.collectableTradeMap.put(player.getUniqueId().toString(), new ArrayList<CollectableTrade>());
        }
        this.collectableTradeMap.get(player.getUniqueId().toString()).add(new CollectableTrade(uuid, itemName, itemPayout, moneyPayout, cancelled));
    }
}
