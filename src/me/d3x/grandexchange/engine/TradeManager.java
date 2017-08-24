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
	
	
	//private HashMap<Integer, ArrayList<Trade>> tradeMap;
	private HashMap<Integer, ArrayList<ArrayList<Trade>>> tradeMap;
	
//	public void loadTradeMapFromFile(GrandExchange ge) {
//		try {
//			tradeMap = new HashMap<Integer, List<ArrayList<Trade>>();
//			File tradeData = new File(ge.getDataFolder() + "\\trade\\trades.dat");
//			Scanner reader = new Scanner(tradeData); //TODO replace with BufferedReader if load time sucks
//			int currentNum = 0;
//			int totalTrades = 0;
//			while(reader.hasNextLine()) {
//				String curline = reader.nextLine();
//				if(curline.startsWith(";")) {
//					currentNum = Integer.parseInt(curline.substring(1));
//					tradeMap.put(new Integer(currentNum), new ArrayList<Trade>());
//				}else if(curline.startsWith(">")) {
//					String[] as = curline.substring(1).split("|");
//					tradeMap.get(currentNum).add(new Trade(as[0], Integer.parseInt(as[1]), Integer.parseInt(as[2]), Integer.parseInt(as[3])));
//					totalTrades++;
//				}
//			}
//			reader.close();
//			GrandExchange.print("Total trades loaded: " + totalTrades);
//		}catch(Exception e) {
//			e.printStackTrace();
//		}
//	}
	
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
		try {
			tradeMap = new HashMap<Integer, ArrayList<ArrayList<Trade>>>();
			File tradeData = new File(ge.getDataFolder() + "\\trade\\trades.dat");
			Scanner reader = new Scanner(tradeData); //TODO replace with BufferedReader if load time sucks
			int currentNum = 0;
			int totalTrades = 0;
			while(reader.hasNextLine()) {
				String curline = reader.nextLine();
				if(curline.startsWith(";")) {
					currentNum = Integer.parseInt(curline.substring(1));
					ArrayList<ArrayList<Trade>> arr = new ArrayList<ArrayList<Trade>>(); //TODO make this suck less
					arr.add(new ArrayList<Trade>());
					arr.add(new ArrayList<Trade>());
					tradeMap.put(new Integer(currentNum), arr);
				}else if(curline.startsWith(">")) {
					String[] as = curline.substring(1).split("|");
					tradeMap.get(currentNum).get(Integer.parseInt(as[3])).add(new Trade(as[0], Integer.parseInt(as[1]), Integer.parseInt(as[2]), Integer.parseInt(as[3])));
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
	
	public void registerTrade(int itemID, String uid, int quantity, int price, int type) {
		tradeMap.get(itemID).get(type).add(new Trade(uid, quantity, price, type));
		//TODO check to see if an offer exists to satisfy the trade, if not, sort
		GrandExchange.print("Added trade " + (type == 0 ? "[BUY]" : "[SELL]") + " [" + itemID + "]: U[" + uid + "] Q[" + quantity + "] P[" + price + "]");
		
	}
}
