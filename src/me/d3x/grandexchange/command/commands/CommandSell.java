package me.d3x.grandexchange.command.commands;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import me.d3x.grandexchange.command.BaseCommand;
import me.d3x.grandexchange.engine.TradeManager;

public class CommandSell extends BaseCommand{

	public HashMap<String, ArrayList<String>> tempQueries;
	
	public CommandSell(String name) {
		super(name);
		tempQueries = new HashMap<String, ArrayList<String>>();
	}
	

	@Override
	public void onPlayerCommand(CommandSender sender, String[] args) {
		if(args.length == 3) {
			Player player = (Player) sender;
			Inventory sellInventory = Bukkit.createInventory(null, 0, "Grand Exchange - Sell");
			ArrayList<String> existingQuery = tempQueries.get(player.getUniqueId().toString());
			if(existingQuery != null) {
				tempQueries.remove(player.getUniqueId().toString());
			}
			ArrayList<String> tempArgs = new ArrayList<String>();
			tempArgs.add(args[1]);
			tempArgs.add(args[2]);
			tempQueries.put(player.getUniqueId().toString(), tempArgs);
			player.openInventory(sellInventory);
		}else {
			paramError(sender);
		}
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Inventory inventory = event.getInventory();
		if(inventory.getName().equals("Grand Exchange - Sell")) {
			Player player = (Player)(event.getWhoClicked());
			ItemStack itemClicked = event.getCurrentItem();
			if(itemClicked != null && itemClicked.getType().getId() != 0) {
				ArrayList<String> tempArgs = tempQueries.get(player.getUniqueId().toString());
				if(tempArgs != null) {
					TradeManager.getInstance().registerTrade(itemClicked.getType().getId(), player.getUniqueId().toString(), Integer.parseInt(tempArgs.get(0)), Integer.parseInt(tempArgs.get(1)), 1);
					player.closeInventory();
					player.updateInventory();
					//player.getInventory().addItem(itemClicked);
				}
			}
		}
	}

	@Override
	public String getParams() {
		return "[quantity] [price]";
	}
	

}
