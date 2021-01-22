package com.powercivs.entities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import org.bukkit.Bukkit;

import com.powercivs.PowerCivs;
import com.powercivs.claims.LandClaim;

import net.md_5.bungee.api.ChatColor;

public class EntityManager implements Serializable{

	public static final long serialVersionUID = 1L;
	
	public static ArrayList<Civilization> civs = new ArrayList<Civilization>();
	public static ArrayList<LandClaim> claims = new ArrayList<LandClaim>();
	
	public static void initEntityManager() {
		File directory = new File(PowerCivs.path + "/Entities");
		
		File[] list = directory.listFiles();
		
		for(File file : list) {
			if(file.isFile()) {
				try {
					FileInputStream fis = new FileInputStream(file);
					ObjectInputStream ois = new ObjectInputStream(fis);
					
					Civilization c = (Civilization) ois.readObject();
					civs.add(c);
					
					
					ois.close();
					fis.close();
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	public static boolean registerCivilization(Civilization civ) {
		if(civs.size() <= 0) {
			civs.add(civ);
			Bukkit.broadcastMessage(ChatColor.RED + civ.getName() + " has been founded! Lookout world...");
			return true;
		}else {
			boolean match = false;
			
			for(Civilization c : civs) {
				if(c.getName().equalsIgnoreCase(civ.getName())) {
					match = true;
				}
			}
			
			if(!match) {		
				civs.add(civ);
				Bukkit.broadcastMessage(ChatColor.RED + civ.getName() + " has been founded! Lookout world...");
					
				return true;
			}else {
				return false;
			}
			
		}
		
		
	}
	
	public static Civilization getCivByName(String name) {
		for(Civilization civ : civs) {
			if(civ.getName().equals(name)) {
				return civ;
			}
		}
		return null;
	}

	public static Civilization getCivByUUID(String UUID) {
		for(Civilization civ : civs) {
			if(civ.citizens.contains(UUID)) {
				return civ;
			}
		}
		return null;
	}
	
	public static void saveCivs() {
		try {
			for(Civilization civ : civs) {
				
				
				
				FileOutputStream fout = new FileOutputStream(PowerCivs.path + "/Entities/" + civ.getName() + ".dat");
				ObjectOutputStream oos = new ObjectOutputStream(fout);
				
				oos.writeObject(civ);
				oos.close();
				fout.flush();
				fout.close();
				
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}