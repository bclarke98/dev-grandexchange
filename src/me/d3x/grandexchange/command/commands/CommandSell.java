package me.d3x.grandexchange.command.commands;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
			if(Material.getMaterial(args[1].toUpperCase()) != null && player.getInventory().contains(Material.getMaterial(args[1].toUpperCase()), Integer.parseInt(args[2]))) {
	            TradeManager.getInstance().registerTrade(args[1].toUpperCase(), player.getUniqueId().toString(), Integer.parseInt(args[2]), Double.parseDouble(args[3]), 1);
                int itemsLeftToRemove = Integer.parseInt(args[2]);
                for(int i = player.getInventory().getStorageContents().length - 1; i >= 0; i--){
                    ItemStack stack = player.getInventory().getStorageContents()[i];
                    if(stack != null && stack.getType().equals(Material.getMaterial(args[1].toUpperCase()))){
                        itemsLeftToRemove -= stack.getAmount();
                        if(itemsLeftToRemove < 0){
                            stack.setAmount(-itemsLeftToRemove);
                            player.updateInventory();
                            break;
                        }else{
                            stack.setAmount(0);
                            player.updateInventory();
                        }
                    }
                }
			}else{
                sendErrorTo(sender, "You don't have enough [" + args[1] + "] to sell!");
            } 
		}else {
			paramError(sender);
		}
	}

	@Override
	public String getParams() {
		return "[itemname] [quantity] [unit price]";
	}
	

}
