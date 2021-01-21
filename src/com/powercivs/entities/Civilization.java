package com.powercivs.entities;

import java.util.ArrayList;

import org.bukkit.Bukkit;

public class Civilization extends Entity{
	
	private static final long serialVersionUID = 1L;

	protected String leader;
	
	protected ArrayList<String> citizens = new ArrayList<String>();
	
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
	
	
	

}