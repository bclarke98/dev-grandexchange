package me.d3x.grandexchange;

import java.io.File;
import java.io.PrintWriter;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_12_R1.command.ColouredConsoleSender;
import org.bukkit.plugin.java.JavaPlugin;

import me.d3x.grandexchange.command.BaseCommand;
import me.d3x.grandexchange.engine.TradeManager;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.Material;

public class GrandExchange extends JavaPlugin{
	
	
	@Override
	public void onEnable() {
		loadDataFromFiles();
		ExchangeHandler.getInstance().loadCommands(this);
		ExchangeHandler.getInstance().alphabetizedCommands();
		getServer().getPluginManager().registerEvents(ExchangeHandler.getInstance(), this);
		for(BaseCommand c : ExchangeHandler.getInstance().getCommands().values()) {
			getServer().getPluginManager().registerEvents(c, this);
		}
	}
	
	@Override
	public void onDisable() {
		saveDataToFiles();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		return ExchangeHandler.getInstance().handleCommand(sender, command, label, args);
	}
	
	public static void print(Object... args) {
		String s = ChatColor.DARK_GREEN + "[" + ChatColor.GOLD + "GrandExchange" + ChatColor.DARK_GREEN + "] " + ChatColor.GREEN;
		for(Object o : args) { s += o; }
		ColouredConsoleSender.getInstance().sendMessage(s);
	}
	
	private void loadDataFromFiles() {
		print("Loading trade data...");
		if(!getDataFolder().exists()) {
			getDataFolder().mkdir();
			print("Created data folder...");
		}
		File tradeFolder = new File(getDataFolder() + "/trade");
		if (!tradeFolder.exists()) {
			tradeFolder.mkdir();
			print("Created trade folder");
		}
		try {
			File tradeData = new File(getDataFolder() + "/trade/trades.dat");
			if(!tradeData.exists()) {
				tradeData.createNewFile();
				PrintWriter writer = new PrintWriter(tradeData, "UTF-8");
				for(Material mat : Material.values()) {
					writer.write(";" + mat + "\n\n");
				}
				writer.flush();
				writer.close();
				print("Created trades.dat");
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		TradeManager.getInstance().loadTradeMapFromFile(this);
	}
    
    private void saveDataToFiles(){
        TradeManager.getInstance().saveTradeMapToFile(this);
    }

}
