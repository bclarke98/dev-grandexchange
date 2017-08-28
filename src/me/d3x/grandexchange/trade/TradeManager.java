package me.d3x.grandexchange.trade;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.d3x.grandexchange.GrandExchange;

public class TradeManager {
    
    //TODO create CommandCancel and CommandCollect
    //TODO check collectionsMap on player login
    //TODO save/load collectionsMap

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
            //handle collections
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
                addPlayerTrade(uid, newTrade);
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
            addPlayerTrade(uid, newTrade);
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
    
    public void addPlayerTrade(String uuid, Trade trade) {
        Player player = ge.getServer().getPlayer(UUID.fromString(uuid));
        if(playerTradeMap.get(player.getUniqueId().toString()) == null) {
            playerTradeMap.put(player.getUniqueId().toString(), new ArrayList<Trade>());
        }
        playerTradeMap.get(player.getUniqueId().toString()).add(trade);
    }

    public GrandExchange getGrandExchange() {
        return ge;
    }
    
    public HashMap<String, ArrayList<CollectableTrade>> getCollectableTrades() {
        return this.collectableTradeMap;
    }
    
    public void addNewCollectableTrade(String uuid, String itemName, ItemStack[] itemPayout, double moneyPayout) {
        Player player = ge.getServer().getPlayer(UUID.fromString(uuid));
        if(this.collectableTradeMap.get(player.getUniqueId().toString()) == null) {
            this.collectableTradeMap.put(player.getUniqueId().toString(), new ArrayList<CollectableTrade>());
        }
        this.collectableTradeMap.get(player.getUniqueId().toString()).add(new CollectableTrade(uuid, itemName, itemPayout, moneyPayout));
    }
}
