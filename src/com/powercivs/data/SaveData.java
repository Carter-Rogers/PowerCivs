package com.powercivs.data;

import static com.powercivs.entities.EntityManager.*;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import static com.powercivs.claims.ClaimManager.*;

public class SaveData extends BukkitRunnable{
	
	@Override
	public void run() {
		saveCivs();
		saveClaims();
		Bukkit.getLogger().info("PowerCivs Data Saved Successfully!");
	}
	
}