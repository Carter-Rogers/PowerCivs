package com.powercivs.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;

import net.md_5.bungee.api.ChatColor;

public class Civilization extends Entity{
	
	private static final long serialVersionUID = 1L;

	protected String leader;
	
	protected ArrayList<String> citizens = new ArrayList<String>();
	
	protected HashMap<String, Boolean> invites = new HashMap<String, Boolean>();
	
	public Civilization(String name) {
		super(name);
	}
	
	public void setLeader(String leader) {
		this.leader = leader;
		
		if(citizens.contains(leader)) {
			return;
		}else {
			citizens.add(leader);
			Bukkit.getLogger().info("Added To Citizen Roster");
		}
	}

	@Override
	public void update() {
		
	}
	
	public String getLeader() {
		return leader;
	}
	
	public boolean invitePlayer(String uuid) {
		if(invites.containsKey(uuid)) {
			return false;
		}else {
			invites.put(uuid, false);
			Bukkit.getPlayer(UUID.fromString(uuid)).sendMessage(ChatColor.YELLOW + "You've Been Invited To Join " + ChatColor.GREEN +  getName() + ChatColor.YELLOW + "! Accept or Decline With /civ <accept/deny> " + getName());
			return true;
		}
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
			Bukkit.getPlayer(UUID.fromString(uuid)).sendMessage(ChatColor.GREEN + "Succesfully Joined " + getName() + "!");
			
			invites.remove(uuid);
		}else {
			invites.remove(uuid);
		}
		
	}
	
	
	

}