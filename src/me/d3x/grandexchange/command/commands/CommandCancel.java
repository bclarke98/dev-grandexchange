package me.d3x.grandexchange.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.d3x.grandexchange.command.BaseCommand;
import me.d3x.grandexchange.trade.Trade;
import me.d3x.grandexchange.trade.TradeManager;

public class CommandCancel extends BaseCommand{

    public CommandCancel(String name) {
        super(name);
    }

    @Override
    public void onPlayerCommand(CommandSender sender, String[] args) {
        if(TradeManager.getInstance().getPlayerTradeMap().get(((Player)(sender)).getUniqueId().toString()) != null
                && TradeManager.getInstance().getPlayerTradeMap().get(((Player)(sender)).getUniqueId().toString()).size() > 0) {
            sendMessageTo(sender, "Removing active trades.");
            for(int i = TradeManager.getInstance().getPlayerTradeMap().get(((Player)(sender)).getUniqueId().toString()).size() - 1; i >= 0; i--) {
                Trade t = TradeManager.getInstance().getPlayerTradeMap().get(((Player)(sender)).getUniqueId().toString()).get(i);
                t.cancel();
                TradeManager.getInstance().removeFromTradeMap(t);
                
            }
            TradeManager.getInstance().getPlayerTradeMap().get(((Player)(sender)).getUniqueId().toString()).clear();
        }else {
            sendMessageTo(sender, "No active trades.");
        }
    }

    @Override
    public String getParams() {
        return "";
    }

}
