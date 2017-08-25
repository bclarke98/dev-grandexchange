package me.d3x.grandexchange.engine;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import me.d3x.grandexchange.GrandExchange;

public class TradeManager {
	
	private static volatile TradeManager instance;
	
	public static TradeManager getInstance(){
		TradeManager result = instance;
		if(result == null) {
			synchronized(TradeManager.class) {
				result = instance;
				if(result == null) {
					result = instance = new TradeManager();
				}
			}
		}
		return result;
	}
    	
	private HashMap<String, ArrayList<ArrayList<Trade>>> tradeMap;
    private GrandExchange ge;
	
	/**
	 *   trades.dat format:
	 *   
	 *   ;[itemid]
	 *   >[uid]|[price]|[quantity]|[buy/sell]
	 *   >[uid]|[price]|[quantity]|[buy/sell]
	 *   \n
	 *   ;[itemid]
	 *   >[uid]|[price]|[quantity]|[buy/sell]
	 *   >[uid]|[price]|[quantity]|[buy/sell]
	 *   >[uid]|[price]|[quantity]|[buy/sell]
	 *   \n
	 */
	
	public void loadTradeMapFromFile(GrandExchange ge) {
        this.ge = ge;
        try {
			tradeMap = new HashMap<String, ArrayList<ArrayList<Trade>>>();
			File tradeData = new File(ge.getDataFolder() + "/trade/trades.dat");
			Scanner reader = new Scanner(tradeData); //TODO replace with BufferedReader if load time sucks
			String currentItem = "";
			int totalTrades = 0;
			while(reader.hasNextLine()) {
				String curline = reader.nextLine();
				if(curline.startsWith(";")) {
					currentItem = curline.substring(1);
					ArrayList<ArrayList<Trade>> arr = new ArrayList<ArrayList<Trade>>(); //TODO make this suck less
					arr.add(new ArrayList<Trade>());
					arr.add(new ArrayList<Trade>());
					tradeMap.put(currentItem, arr);
				}else if(curline.startsWith(">")) {
					String[] as = curline.substring(1).split("|");
					tradeMap.get(currentItem).get(Integer.parseInt(as[3])).add(new Trade(as[0], Integer.parseInt(as[1]), Integer.parseInt(as[2]), Integer.parseInt(as[3])));
					totalTrades++;
				}
			}
			reader.close();
			GrandExchange.print("Total trades loaded: " + totalTrades);
			//System.out.println(tradeMap.toString());
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void registerTrade(String itemname, String uid, int quantity, int price, int type) {
        Trade newTrade = new Trade(uid, quantity, price, type);
        ArrayList<Trade> potentialTrades = tradeMap.get(itemname).get((type + 1) % 2);
        if(potentialTrades.size() > 0 && potentialTrades.get(0).canCompleteTrade(newTrade)){
            GrandExchange.print("Found potential trade...");
            //handle processing trade
        }else{
		    tradeMap.get(itemname).get(type).add(newTrade);
		    GrandExchange.print("Added trade " + (type == 0 ? "[BUY]" : "[SELL]") + " [" + itemname + "]: U[" + uid + "] Q[" + quantity + "] P[" + price + "]");
            //TODO sort
        }
		
	}

    public void saveTradeMapToFile(GrandExchange ge){
        
    }
	
	public GrandExchange getGrandExchange() {
		return ge;
	}
}
