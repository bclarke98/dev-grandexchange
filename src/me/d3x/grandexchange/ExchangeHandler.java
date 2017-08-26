package me.d3x.grandexchange;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import me.d3x.grandexchange.command.BaseCommand;

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
			}else {
				return commands.get("help").processCommand(sender, args);
			}
		}
		return true;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		
	}
	
	@EventHandler 
	public void onPlayerMove(PlayerMoveEvent event){
		
	}
	
	@EventHandler 
	public void onPlayerClickInventory(InventoryClickEvent event){
		//if(event.getCurrentItem() != null && !event.getCurrentItem().getType().equals(Material.AIR)) {
		//	System.out.println(event.getCurrentItem().getType());
		//}
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
    	
    	public String errorPrefix() {
    		return "\\2474[\\247cError\\2474]:\\247c ";
    	}
    	
    	public String usagePrefix() {
    		return "\2472[\2476Grand Exchange\2472]:\247a ";
    	}
    	
    	public void sendChatMessage(Player player, String message) {
            player.sendMessage(chatPrefix() + message);
        }
    	
    	public void sendChatMessage(UUID uuid, String message) {
            getPlayerByUUID(uuid).sendMessage(chatPrefix() + message);
        }
    	
    }

}
