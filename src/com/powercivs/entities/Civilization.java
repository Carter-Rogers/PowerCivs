package com.powercivs.entities;

import java.util.ArrayList;
<<<<<<< HEAD

import org.bukkit.Bukkit;

public class Civilization extends Entity{
	
	private static final long serialVersionUID = 1L;

	protected String leader;
	
	protected ArrayList<String> citizens = new ArrayList<String>();
=======
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;

import net.md_5.bungee.api.ChatColor;

public class Civilization extends Entity{
	
	private static final long serialVersionUID = 1L;
	protected String leader;
	protected int money = 150; //money is managed in civilizations by resources. i.e: gold, iron, diamonds, etc.
	protected ArrayList<String> citizens = new ArrayList<String>();
	protected HashMap<String, Boolean> invites = new HashMap<String, Boolean>();
	public HashMap<String, String> cabinet_roles = new HashMap<String, String>();
>>>>>>> development
	
	public Civilization(String name) {
		super(name);
	}
	
	public void setLeader(String leader) {
		this.leader = leader;
		
		if(citizens.contains(leader)) {
			return;
		}else {
			citizens.add(leader);
<<<<<<< HEAD
			Bukkit.getLogger().info("Added To Citizen Roster");
=======
>>>>>>> development
		}
	}

	@Override
	public void update() {
		
	}
	
	public String getLeader() {
		return leader;
	}
	
<<<<<<< HEAD
	
	

=======
	public boolean invitePlayer(String uuid) {
		if(invites.containsKey(uuid)) {
			return false;
		}else {
			invites.put(uuid, false);
			Bukkit.getPlayer(uuid).sendMessage(ChatColor.YELLOW + "You've Been Invited To Join " + ChatColor.GREEN +  getName() + ChatColor.YELLOW + "! Accept or Decline With /civ <accept/deny> " + getName());
			return true;
		}
	}
	
	public void removeCitizen(String uuid) {
		citizens.remove(uuid);
	}
	
	public boolean isInvited(String uuid) {
		if(invites.containsKey(uuid)) {
			return true;
		}
		
		return false;
	}
	
	public void AcceptOrDeny(String uuid, boolean response) {
		if(response) {
			citizens.add(uuid);
			Bukkit.getPlayer(uuid).sendMessage(ChatColor.GREEN + "Succesfully Joined " + getName() + "!");
			
			invites.remove(uuid);
		}else {
			invites.remove(uuid);
		}
		
	}
	
	public void appointCitizen(String uuid, Roles role) {
		if(cabinet_roles.containsValue(role.role)) {
			for(Entry<String, String> entry : cabinet_roles.entrySet()) {
				if(entry.getValue() == role.role) {
					cabinet_roles.remove(entry.getKey(), role.role);
					Bukkit.getPlayer(leader).sendMessage("fixed");
				}
			}
			
			Bukkit.getPlayer(uuid).sendMessage(ChatColor.YELLOW + "You've Been Appointed As " + ChatColor.GREEN + role.role + ChatColor.YELLOW + "!");
			
			for(String s : citizens) {
				if(Bukkit.getPlayer(s) == null) {
					continue;
				}else {
					if(s.equalsIgnoreCase(uuid)) {
						continue;
					}else {
						Bukkit.getPlayer(s).sendMessage(ChatColor.GREEN + uuid + ChatColor.YELLOW + " Has Been Appointed " + ChatColor.GREEN + role.role + ChatColor.YELLOW + "!");						
					}
				}
			}
			
			cabinet_roles.put(uuid, role.role);
		}else {
			for(String s : citizens) {
				if(Bukkit.getPlayer(s) == null) {
					continue;
				}else {
					if(s.equalsIgnoreCase(uuid)) {
						continue;
					}else {
						Bukkit.getPlayer(s).sendMessage(ChatColor.GREEN + uuid + ChatColor.YELLOW + " Has Been Appointed " + ChatColor.GREEN + role.role + ChatColor.YELLOW + "!");						
					}
				}
			}
			
			Bukkit.getPlayer(uuid).sendMessage(ChatColor.YELLOW + "You've Been Appointed As " + ChatColor.GREEN + role.role + ChatColor.YELLOW + "!");
			cabinet_roles.put(uuid, role.role);
		}		
	}
	
>>>>>>> development
}