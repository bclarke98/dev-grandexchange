package me.d3x.grandexchange.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import me.d3x.grandexchange.ExchangeHandler;
import me.d3x.grandexchange.GrandExchange;
import me.d3x.grandexchange.command.ConsoleCommand;
import me.d3x.grandexchange.command.BaseCommand;

public class CommandHelp extends BaseCommand{

	public CommandHelp(String name) {
		super(name);
	}
	
	@Override
	public boolean processCommand(CommandSender sender, String[] args) {
        if(sender instanceof Player) {
            onPlayerCommand(sender, args);
        }else if(sender instanceof ConsoleCommandSender) {
            for (BaseCommand c : ExchangeHandler.getInstance().getAlphabetizedCommands()) {
                if(c instanceof ConsoleCommand) {
                    GrandExchange.print(c.getHelp());
                }
            }
        }
        return true;
    }

	@Override
	public void onPlayerCommand(CommandSender sender, String[] args) {
		if(args.length > 1) {
			for(int i = 1; i < args.length; i++) {
				BaseCommand c = ExchangeHandler.getInstance().getCommands().get(args[i].toLowerCase());
				if(c != null) {
					sendMessageTo(sender, c.getHelp());
				}else {
					sendMessageTo(sender, "No command with name \"" + args[i].toLowerCase() + "\"");
				}
			}
		}else {
			for (BaseCommand c : ExchangeHandler.getInstance().getAlphabetizedCommands()) {
				if(!(c instanceof ConsoleCommand)) {
				    sendMessageTo(sender, c.getHelp());
				}
			}
		}
	}

	@Override
	public String getParams() {
		return "[command name]";
	}
	
	
}
