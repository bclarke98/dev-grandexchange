package me.d3x.grandexchange.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.d3x.grandexchange.command.BaseCommand;
import me.d3x.grandexchange.trade.CollectableTrade;
import me.d3x.grandexchange.trade.TradeManager;

public class CommandCollect extends BaseCommand{

    public CommandCollect(String name) {
        super(name);
    }

    @Override
    public void onPlayerCommand(CommandSender sender, String[] args) {
        if(TradeManager.getInstance().getCollectableTrades().get(((Player)(sender)).getUniqueId().toString()) != null) {
            for(CollectableTrade ct : TradeManager.getInstance().getCollectableTrades().get(((Player)(sender)).getUniqueId().toString())) {
                ct.collect();
            }
            TradeManager.getInstance().getCollectableTrades().get(((Player)(sender)).getUniqueId().toString()).clear();
        }
    }

    @Override
    public String getParams() {
        return "";
    }

}
