package com.powercivs.data;

import static com.powercivs.entities.EntityManager.*;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class SaveData extends BukkitRunnable{
	
	@Override
	public void run() {
		saveCivs();
		Bukkit.getLogger().info("PowerCivs Data Saved Successfully!");
	}
	
}