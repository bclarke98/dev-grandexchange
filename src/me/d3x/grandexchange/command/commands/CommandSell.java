package me.d3x.grandexchange.command.commands;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.d3x.grandexchange.command.BaseCommand;
import me.d3x.grandexchange.engine.TradeManager;

public class CommandSell extends BaseCommand{
	
	public CommandSell(String name) {
		super(name);
	}
	

	@Override
	public void onPlayerCommand(CommandSender sender, String[] args) {
        //player.performCommand('/ge sell [itemname] [quantity] [price]')
		if(args.length == 4) {
			Player player = (Player) sender;
            //validate that inventory contains item/quantity
			if(player.getInventory().contains(Material.getMaterial(args[1].toUpperCase()), Integer.parseInt(args[3]))) {
	            TradeManager.getInstance().registerTrade(args[1].toUpperCase(), player.getUniqueId().toString(), Integer.parseInt(args[2]), Integer.parseInt(args[3]), 1);
			}
		}else {
			paramError(sender);
		}
	}

	@Override
	public String getParams() {
		return "[itemname] [quantity] [price]";
	}
	

}
