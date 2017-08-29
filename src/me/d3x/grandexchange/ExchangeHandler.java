package me.d3x.grandexchange;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import me.d3x.grandexchange.command.BaseCommand;
import me.d3x.grandexchange.gui.GuiInventory;
import me.d3x.grandexchange.trade.TradeManager;

public class ExchangeHandler implements Listener{
	
	private static volatile ExchangeHandler instance;
	private ChatHandler chatHandler;
	
	public static ExchangeHandler getInstance(){
		ExchangeHandler result = instance;
		if(result == null) {
			synchronized(ExchangeHandler.class) {
				result = instance;
				if(result == null) {
					result = instance = new ExchangeHandler();
				}
			}
		}
		return result;
	}
	
    private GrandExchange ge;
    private GuiInventory gui;
	private HashMap<String, BaseCommand> commands;
	private ArrayList<BaseCommand> alphabetizedCommands = null;
	
	public ExchangeHandler() {
		this.chatHandler = new ChatHandler();
	}
	
	public HashMap<String, BaseCommand> initializeFromJar(String jarPath, String cmdDir){
		HashMap<String, BaseCommand> objects = new HashMap<String, BaseCommand>();
		ZipInputStream zip;
		try {
			zip = new ZipInputStream(new FileInputStream(jarPath));
			for(ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
				if(!entry.isDirectory() && entry.getName().endsWith(".class")) {
					if(entry.getName().startsWith(cmdDir + "/")) {
						String s = entry.getName().substring(0, entry.getName().length() - 6).replace('/', '.');
						String k = s.split("Command")[1].toLowerCase();
						objects.put(k, (BaseCommand)(Class.forName(s).getConstructor(String.class).newInstance(k)));
					}
				}
			}
			zip.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return objects;
	}
	
	
	public void loadCommands(GrandExchange ge) {
        this.ge = ge;
        this.gui = new GuiInventory(ge);
		this.commands = initializeFromJar("plugins/GrandExchange.jar", "me/d3x/grandexchange/command/commands");
		GrandExchange.print("Loaded commands...");
	}
	
	
	public void alphabetizedCommands(){
		alphabetizedCommands = new ArrayList<BaseCommand>(this.commands.values());
		Collections.sort(alphabetizedCommands, new Comparator<BaseCommand>() {
			@Override
			public int compare(BaseCommand c1, BaseCommand c2) {
				return c1.getName().compareTo(c2.getName());
			}
		});
	}
	
	public ArrayList<BaseCommand> getAlphabetizedCommands(){
		return this.alphabetizedCommands;
	}
	
	/**
	 * 
	 * @param sender - represents whatever is sending the command (Player/Console/CommandBlock)
	 * @param command - command being called
	 * @param label - first word of command (/[label] [args...])
	 * @param args - any words other than the label in the command
	 * 
	 */
	public boolean handleCommand(CommandSender sender, Command command, String label, String[] args) {
		if(command.getName().toLowerCase().equals("ge")) {
			if(args.length > 0 && commands.get(args[0].toLowerCase()) != null) {
				return commands.get(args[0].toLowerCase()).processCommand(sender, args);
			}else if(args.length == 0 && sender instanceof Player){
			    Player player = (Player)(sender);
                player.openInventory(gui.getMainMenuInventory());
				//return commands.get("help").processCommand(sender, args);
			}
		}
		return true;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		if(TradeManager.getInstance().getCollectableTrades().get(event.getPlayer().getUniqueId().toString()) != null) {
            chatHandler.sendChatMessage(event.getPlayer(), "One of your trades has been completed.");
            chatHandler.sendChatMessage(event.getPlayer(), "Use \"/ge collect\" to collect.");
		}
	}
	
	@EventHandler 
	public void onPlayerMove(PlayerMoveEvent event){
		
	}
	
	@EventHandler 
	public void onPlayerClickInventory(InventoryClickEvent event){
		if(event.getInventory().equals(gui.getMainMenuInventory()) && event.getCurrentItem() != null && !event.getCurrentItem().getType().equals(Material.AIR)) {
		    gui.handleMainInventory(event, (Player) event.getWhoClicked(), event.getCurrentItem());
		}
		if(event.getInventory().equals(gui.buyInventories.get((Player)(event.getWhoClicked()))) && event.getCurrentItem() != null && !event.getCurrentItem().getType().equals(Material.AIR)){
            gui.handleBuyInventory(event, (Player) event.getWhoClicked(), event.getCurrentItem());
        }
		if(event.getInventory().equals(gui.sellInventories.get((Player)(event.getWhoClicked()))) && event.getCurrentItem() != null && !event.getCurrentItem().getType().equals(Material.AIR)){
		    gui.handleSellInventory(event, (Player) event.getWhoClicked(), event.getCurrentItem());
		}
		if(event.getInventory().equals(gui.quantityInventories.get((Player)(event.getWhoClicked()))) && event.getCurrentItem() != null && !event.getCurrentItem().getType().equals(Material.AIR)){
            gui.handleQuantityInventory(event, (Player) event.getWhoClicked(), event.getCurrentItem());
        }
		if(event.getInventory().equals(gui.priceInventories.get((Player)(event.getWhoClicked()))) && event.getCurrentItem() != null && !event.getCurrentItem().getType().equals(Material.AIR)){
            gui.handlePriceInventory(event, (Player) event.getWhoClicked(), event.getCurrentItem());
        }
	}
	
	public HashMap<String, BaseCommand> getCommands(){
		return this.commands;
	}
    
    public GrandExchange getGrandExchange(){
        return this.ge;
    }
    
    public Player getPlayerByUUID(UUID uuid) {
    	return this.getGrandExchange().getServer().getPlayer(uuid);
    }
    
    public ChatHandler getChatHandler() {
    	return this.chatHandler;
    }
    
    
    public class ChatHandler{
    	
    	public String chatPrefix() {
    		return "\2472[\2476Grand Exchange\2472]:\247a ";
    	}
    	
    	public String shortPrefix() {
    	    return "\2479[GE]\247r ";
    	}
    	
    	public String errorPrefix() {
    		return "\2474[\247cError\2474]:\247c ";
    	}
    	
    	public String usagePrefix() {
    		return "\2479[\2473Usage\2479]:\247r ";
    	}
    	
    	public void sendChatMessage(Player player, String message) {
    	    if(player.isOnline()) {
                player.sendMessage(chatPrefix() + message);
    	    }
        }
    	
    	public void sendErrorMessage(Player player, String message) {
    	    if(player.isOnline()) {
    	        player.sendMessage(errorPrefix() + message);
    	    }
        }
    	
    	public void sendChatMessage(UUID uuid, String message) {
    	    if(getPlayerByUUID(uuid).isOnline()) {
                getPlayerByUUID(uuid).sendMessage(chatPrefix() + message);
    	    }
        }
    	
    }

}
