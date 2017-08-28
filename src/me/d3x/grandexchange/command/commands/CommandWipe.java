package me.d3x.grandexchange.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import me.d3x.grandexchange.command.AdminCommand;
import me.d3x.grandexchange.command.BaseCommand;
import me.d3x.grandexchange.trade.TradeManager;

public class CommandWipe extends BaseCommand implements AdminCommand{

    public CommandWipe(String name) {
        super(name);
    }

    @Override
    public boolean processCommand(CommandSender sender, String[] args) {
        if(sender instanceof Player) {
            onPlayerCommand(sender, args);
        }else if(sender instanceof ConsoleCommandSender){
           TradeManager.getInstance().resetTrades();
        }
        return true;
    }    


    @Override
    public void onPlayerCommand(CommandSender sender, String[] args) {}
   
 
    @Override
    public String getParams() {
        return "";
    }


}
