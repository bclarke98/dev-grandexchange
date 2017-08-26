package me.d3x.grandexchange.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import me.d3x.grandexchange.ExchangeHandler;
import me.d3x.grandexchange.ExchangeHandler.ChatHandler;

public abstract class BaseCommand implements Listener{

	private String name;
	private ChatHandler chatHandler;
	
	public BaseCommand(String name) {
		this.name = name;
		this.chatHandler = ExchangeHandler.getInstance().getChatHandler();
	}
	
	/** 
	 * 
	 * @param sender - sender of message
	 * @param args - message args (args[0] is always name of command)
	 * 
	 */
	public boolean processCommand(CommandSender sender, String[] args) {
		if(sender instanceof Player) {
			onPlayerCommand(sender, args);
		}
		return true;
	}
	
	public void sendMessageTo(CommandSender target, String message) {
		target.sendMessage(this.chatHandler.chatPrefix() + message);
	}
	
	public void sendErrorTo(CommandSender target, String message) {
		sendMessageTo(target,this.chatHandler.errorPrefix() + message);
	}
	
	public void paramError(CommandSender target) {
		sendErrorTo(target, "Invalid amount of parameters.");
		sendMessageTo(target, this.chatHandler.usagePrefix() + getHelp());
	}
	
	public abstract void onPlayerCommand(CommandSender sender, String[] args);
	public abstract String getParams();
	
	public String getName() {
		return this.name;
	}
	
	public String getHelp() {
		return "\2477/ge \2479" + this.name + " \2473" + this.getParams();
	}
}
