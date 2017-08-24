package me.d3x.grandexchange;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import me.d3x.grandexchange.command.BaseCommand;

public class GrandExchange extends JavaPlugin{
	
	public static void print(Object... args) {
		String s = "[GrandExchange] ";
		for(Object o : args) { s += o; }
		System.out.println(s);
	}
	
	@Override
	public void onEnable() {
		ExchangeHandler.getInstance().loadCommands();
		ExchangeHandler.getInstance().alphabetizedCommands();
		getServer().getPluginManager().registerEvents(ExchangeHandler.getInstance(), this);
		for(BaseCommand c : ExchangeHandler.getInstance().getCommands().values()) {
			getServer().getPluginManager().registerEvents(c, this);
		}
	}
	
	@Override
	public void onDisable() {
		
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		return ExchangeHandler.getInstance().handleCommand(sender, command, label, args);
	}

}
