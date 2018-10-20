package powercivs.corporations;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import powercivs.PowerCivs;

public class CorporationManager implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static ArrayList<Corporation> corporations = new ArrayList<Corporation>();

	public static void initCorporations() {		
		File directory = new File(PowerCivs.path + "/Corporations");

		File[] list = directory.listFiles();

		for (File f : list) {
			if (f.isFile()) {
				if (f.getPath().contains("_corp")) {
					try {
						FileInputStream fis = new FileInputStream(f);
						ObjectInputStream ois = new ObjectInputStream(fis);

						Corporation c = (Corporation) ois.readObject();
						registerCorporation(c);

						ois.close();
						fis.close();
					} catch (Exception e) {
						return;
					}
				}
			}
		}
	}

	public static void registerCorporation(Corporation corporation) {
		if (corporations.contains(corporation))
			return;
		else {
			corporations.add(corporation);
			Bukkit.broadcastMessage(ChatColor.RED + "A New Corporation Named " + corporation.corporationName
					+ " Has Just Been Registered!");
		}
	}

	public static Corporation getInfo(Player player) {
		for (Corporation corp : corporations) {
			Bukkit.getLogger().info(corp.CEO + ", " + player.getName());
			if (corp.CEO.equals(player.getUniqueId()))
				return corp;
			else
				continue;
		}
		return null;
	}
	
	public static Corporation getCorporation(Player player) {
		for(Corporation corp : corporations) {
			if(corp.CEO.equals(player.getUniqueId()))
				return corp;
		}
		return null;
	}

	public static Corporation getInfo(String name) {
		for (Corporation corp : corporations) {
			if (corp.getName().equalsIgnoreCase(name))
				return corp;
			else
				continue;
		}
		return null;
	}
	
	public static void removeCorporation(UUID UUIDS, String name) {
		for(Corporation c : corporations) {
			if(c.CEO.equals(UUIDS)) {
				File f = new File(PowerCivs.path + "/Corporations" + "/" + c.corporationName + "_corp.dat");
				File f1 = new File(PowerCivs.path + "/Corporations" + "/" + c.corporationName + "_corp_bank.dat");
				f.delete();
				f1.delete();
				corporations.remove(c);
				Bukkit.broadcastMessage(ChatColor.RED + Bukkit.getPlayer(UUIDS).getDisplayName() + " Has Ended The Corporation " + c.corporationName + "!");
			
				return;
			}
		}
	}
	
	public static void payEmployees() {
		for(Corporation c : corporations) {
			c.payEmployees();
		}
	}

	public static void saveCorporations() {
		try {
			for (Corporation corp : corporations) {
				FileOutputStream fout = new FileOutputStream(PowerCivs.path + "/Corporations" + "/" + corp.getName() + "_corp.dat");
				ObjectOutputStream oos = new ObjectOutputStream(fout);
				oos.writeObject(corp);
				fout.flush();
				fout.close();

				File f = new File(PowerCivs.path + "/Corporations" + "/" + corp.getName() + "_corp_bank.dat");
				if (f.exists())
					f.delete();

				FileOutputStream fos = new FileOutputStream(PowerCivs.path + "/Corporations" + "/" + corp.getName() + "_corp_bank.dat");
				ObjectOutputStream os = new ObjectOutputStream(fos);
				os.close();
				fos.flush();
				fos.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}