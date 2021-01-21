package com.powercivs;

import static com.powercivs.entities.EntityManager.*;

import java.io.File;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.powercivs.entities.Civilization;

public class PowerCivs extends JavaPlugin implements Listener{

	public static String path;
	
	private void folderSetup() {
		if(getDataFolder().exists() != true) {			
			getDataFolder().mkdir();
		}
		
		new File(getDataFolder().getAbsolutePath() + "/Entities").mkdir();
		
		
		path = getDataFolder().getPath();		
	}
	
	@Override
	public void onEnable() {
		folderSetup();
		
		initEntityManager();
		
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player player = (Player) sender;
		
		if(cmd.getName().equalsIgnoreCase("about")) {	
			Player p = (Player) sender;
			p.sendMessage(path);
			return true;
		}
		
		if(cmd.getName().equalsIgnoreCase("civ") && args[0].equalsIgnoreCase("new")) {
			if(args.length <= 1) {
				player.sendMessage(ChatColor.BLUE + "EX: /civ new <name>");
				return true;
			}else {
				
				@SuppressWarnings("unused")
				String type = args[0]; //default is civ for now. 
				String name = args[1];
				
				Civilization civ = new Civilization(name);
				civ.setLeader(player.getUniqueId().toString());
				
				if(getCivByUUID(civ.getLeader()) == null) {
					if(registerCivilization(civ)) {
						saveCivs();
						return true;
					}else {
						player.sendMessage(ChatColor.RED + "That Civilization Already Exists!");
					}
					
					return true;
				}else {
					player.sendMessage(ChatColor.RED + " You Are Either The Owner or Belong To Another Civ!");
				}
				
				
				
				return true;
			}
		}
		

		if(cmd.getName().equalsIgnoreCase("civ") && args[0].equalsIgnoreCase("info")) {
			if(args.length <= 1) { //Check your own civilization stats
				Civilization civ = getCivByUUID(player.getUniqueId().toString());
				
				if(civ == null)
					player.sendMessage(ChatColor.RED + "You Aren't In A Civilization!");
				else {
					player.sendMessage("");
					player.sendMessage(ChatColor.GREEN + "" + civ.getName() + "'s Information:");
					player.sendMessage(ChatColor.GREEN + "Leader: " + civ.getLeader());
					player.sendMessage("");
					
					return true;
				}
				
				return true;
			}else {
				String name = args[1];
				
				Civilization civ = getCivByName(name);
				

				if(civ == null)
					player.sendMessage(ChatColor.RED + "That Civilization Doesn't Exist!");
				else {
					player.sendMessage("");
					player.sendMessage(ChatColor.GREEN + "" + civ.getName() + "'s Information:");
					player.sendMessage(ChatColor.GREEN + "Leader: " + Bukkit.getPlayer(UUID.fromString(civ.getLeader())).getName());
					player.sendMessage("");
					
					return true;
				}
				
				return true;
			}
		}
		
		
		return true;
	}
	
	
	@Override
	public void onDisable() {
		
	}
	
}
