package me.d3x.grandexchange.command.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import me.d3x.grandexchange.command.BaseCommand;

public class CommandSell extends BaseCommand{

	public CommandSell(String name) {
		super(name);
	}

	@Override
	public void onPlayerCommand(CommandSender sender, String[] args) {
		if(args.length == 3) {
			Player player = (Player) sender;
			PlayerInventory inventory = player.getInventory();
			Inventory sellInventory = Bukkit.createInventory(null, 0, "Grand Exchange - Sell");
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
			System.out.println(itemClicked.getType().name());
		}
	}

	@Override
	public String getParams() {
		return "[quantity] [price]";
	}
	

}
