package powercivs.nations;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import powercivs.PowerCivs;

public class NationManager implements Serializable {

	private static final long serialVersionUID = 1L;

	public static ArrayList<Nation> nations = new ArrayList<Nation>();

	public static String[] paths = new String[50];

	public static void initNations() {
		File directory = new File(PowerCivs.path + "/Nations");

		File[] list = directory.listFiles();

		for (File f : list) {
			if (f.isFile()) {
				try {
					FileInputStream fis = new FileInputStream(f);
					ObjectInputStream ois = new ObjectInputStream(fis);

					Nation n = (Nation) ois.readObject();
					registerNation(n);

					ois.close();
					fis.close();
				} catch (Exception e) {
					return;
				}
			}
		}
	}

	public static void loadCits() {
		
	}
	
	public static void setHome(String name, String chX, String chY, String chZ) {
		for(Nation n : nations) {
			if(n.getNationName().equals(name)) {
				n.setHome(chX, chY, chZ);
			}else {
				continue;
			}
		}
	}
	
	public static boolean registerNation(Nation n) {
		if(nations.size() <= 0) {
			nations.add(n);
			Bukkit.broadcastMessage(ChatColor.BLUE + n.getNationName() + " has been founded! Lookout world!");
		}else {
			for(Nation nation : nations) {
				if(nation.getNationName().equals(n.getNationName())) {
					return false;
				}else {
					nations.add(n);
					Bukkit.broadcastMessage(ChatColor.BLUE + n.getNationName() + " has been founded! Lookout world!");
					return true;
				}
			}	
		}
		return false;
	}

	public static Nation getNation(String name) {
		for (Nation n : nations) {
			if (n.nationName.equals(name)) {
				return n;
			} else {
				continue;
			}
		}
		return null;
	}
	
	public static Nation getNationByCit(String player) {
		for(Nation nation : nations) {
			for(Citizen c : nation.citizens) {
				if(c.getDisplayName().equals(player)) {
					return nation;
				}
			}
		}
		return null;
	}
	
	public static boolean isWarring(Nation n, Nation n1) {
		if(n.isEnemy(n1.getNationName()))
			return true;
		else
			return false;
	}

	public static Nation playerBelongs(Player player) {
		for (Nation n : nations) {
			for(Citizen c : n.citizens) {
				if(c.getDisplayName().equals(player.getDisplayName())) {
					return n;
				}
			}
		}
		return null;
	}
	
	@SuppressWarnings("deprecation")
	public static void setWar(Nation setting, Nation enemy) {
		for(Citizen c : enemy.citizens) {
			Bukkit.getPlayer(c.displayName).sendMessage(ChatColor.DARK_AQUA + setting.nationName + " Has Just Declared War With Your Nation!");
		}
		
		for(Citizen cc : setting.citizens) {
			Bukkit.getPlayer(cc.displayName).sendMessage(ChatColor.DARK_AQUA + "Your Nation Just Declared War With " + enemy.nationName + "!");
		}
		
		setting.enemies.add(enemy.getNationName());
		enemy.enemies.add(setting.getNationName());
	}
	
	@SuppressWarnings("deprecation")
	public static void endWar(Nation setting, Nation enemy) {
		for(Citizen c : enemy.citizens) {
			Bukkit.getPlayer(c.displayName).sendMessage(ChatColor.DARK_AQUA + setting.nationName + " Has Just Ended A War With Your Nation!");
		}
		
		for(Citizen cc : setting.citizens) {
			Bukkit.getPlayer(cc.displayName).sendMessage(ChatColor.DARK_AQUA + "Your Nation Just Ended A War With " + enemy.nationName + "!");
		}
		
		setting.enemies.remove(enemy.getNationName());
		enemy.enemies.remove(setting.getNationName());

	}
	
	public static Nation playerBelongs(Citizen cit) {
		for (Nation n : nations) {
			if (n.citizens.contains(cit))
				return n;
			else
				continue;
		}
		return null;
	}
	
	public static void updateNations() {
		for(Nation n : nations) {
			if(n.getTreasury() < 0.0)
				n.indexDebt += 1;
				if(n.indexDebt >= 10) {					
					n.broadcastToCitizens(ChatColor.RED + "Your Nation Has Gone Bankrupt, It Will Now Be Disbanded!");
					removeNation(n.getNationName());
				}
		}
	}

	public static void saveNations() {
		try {
			for (Nation nation : nations) {
				FileOutputStream fout = new FileOutputStream(PowerCivs.path + "/Nations/" + nation.getNationName() + ".dat");
				ObjectOutputStream oos = new ObjectOutputStream(fout);
				oos.writeObject(nation);
				oos.close();
				fout.flush();
				fout.close();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void removeName(String nation, String name) {
		for (Nation n : nations) {
			if(nation.equals(n.getNationName())) {
				for(Citizen c : n.citizens) {
					if(c.displayName.equals(name)) {
						int i = n.citizens.indexOf(c);
						
						n.citizens.get(i).displayName = "dadfased";
					}
				}
			}
		}
	}
	
	
	public static void removeNation(String name) {
		try {
			for (Nation n : nations) {
				if (n.nationName.equals(name)) {
					nations.remove(n);
					ClaimManager.removeClaim(n.getNationName());
					File f = new File(PowerCivs.path + "/Nations/" + name + ".dat");
					f.delete();
				}
			}
		} catch (Exception e) {
			return;
		}
	}
	

}