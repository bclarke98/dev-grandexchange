package me.d3x.grandexchange.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;


import me.d3x.grandexchange.GrandExchange;
import me.d3x.grandexchange.ExchangeHandler;
import me.d3x.grandexchange.command.ConsoleCommand;
import me.d3x.grandexchange.command.BaseCommand;

public class CommandForce extends BaseCommand implements ConsoleCommand{

    public CommandForce(String name) {
        super(name);
    }

    @Override
    public boolean processCommand(CommandSender sender, String[] args) {
        if(sender instanceof Player) {
            onPlayerCommand(sender, args);
        }else if(sender instanceof ConsoleCommandSender && args.length > 2){
            Player player = ExchangeHandler.getInstance().getGrandExchange().getServer().getPlayer(args[1]);
            String s = "";
            for(int i = 2; i < args.length; i++){
                s += args[i] + " ";
            }
            if(player != null){
                GrandExchange.print(player.performCommand("" + s) ? "Successfully performed command." : "Failed to execute command");
            }else{
                GrandExchange.print("Couldn't find player.");
            }
        }
        return true;
    }    


    @Override
    public void onPlayerCommand(CommandSender sender, String[] args) {}
   
 
    @Override
    public String getParams() {
        return "[username] [command] [args]";
    }


}
