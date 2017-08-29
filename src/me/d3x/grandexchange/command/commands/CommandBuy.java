package me.d3x.grandexchange.command.commands;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.kirik.zen.economy.NotEnoughMoneyException;

import me.d3x.grandexchange.ExchangeHandler;
import me.d3x.grandexchange.command.BaseCommand;
import me.d3x.grandexchange.trade.TradeManager;

public class CommandBuy extends BaseCommand{

    public CommandBuy(String name) {
        super(name);
    }


    @Override
    public void onPlayerCommand(CommandSender sender, String[] args) {
        //player.performCommand('/ge buy [itemname] [quantity] [price]')
        if(args.length == 4) {
            Player player = (Player) sender;
            try {
                ExchangeHandler.getInstance().getEco().subtractFromBalance(player, (int) (Integer.parseInt(args[2]) * Double.parseDouble(args[3])));
                if(Material.getMaterial(args[1].toUpperCase()) != null) {
                    TradeManager.getInstance().registerTrade(args[1].toUpperCase(), player.getUniqueId().toString(), Integer.parseInt(args[2]), Double.parseDouble(args[3]), 0);
                }else {
                    sendErrorTo(player, "Item not found with name: " + args[1].toUpperCase());
                }
            }catch(NotEnoughMoneyException ex) {
                sendErrorTo(player, "You don't have enough money to create this trade offer.");
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
