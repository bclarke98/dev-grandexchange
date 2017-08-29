package me.d3x.grandexchange.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import me.d3x.grandexchange.ExchangeHandler;
import me.d3x.grandexchange.command.BaseCommand;
import me.d3x.grandexchange.command.ConsoleCommand;
import me.d3x.grandexchange.trade.TradeManager;

public class CommandList extends BaseCommand implements ConsoleCommand{

    public CommandList(String name) {
        super(name);
    }

    @Override
    public boolean processCommand(CommandSender sender, String[] args) {
        if(sender instanceof Player) {
            onPlayerCommand(sender, args);
        }else if(sender instanceof ConsoleCommandSender){
           TradeManager.getInstance().printTrades();
        }
        return true;
    }    


    @Override
    public void onPlayerCommand(CommandSender sender, String[] args) {
        if(TradeManager.getInstance().getPlayerTradeMap().get(((Player)(sender)).getUniqueId().toString()) != null
                && TradeManager.getInstance().getPlayerTradeMap().get(((Player)(sender)).getUniqueId().toString()).size() > 0) {
            //sendMessageTo(sender, "Listing active trades:");
            //for(Trade t : TradeManager.getInstance().getPlayerTradeMap().get(((Player)(sender)).getUniqueId().toString())) {
            //    sendMessageTo(sender, t.getText());
            //}
            ExchangeHandler.getInstance().getGui().openTradesInventory((Player)(sender));
        }else {
            sendMessageTo(sender, "No active trades.");
        }
    }
   
 
    @Override
    public String getParams() {
        return "";
    }


}
