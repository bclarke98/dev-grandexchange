package me.d3x.grandexchange.engine;


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

}
