package powercivs.nations;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import powercivs.PowerCivs;
import powercivs.nations.legislation.Policy;
import powercivs.nations.legislation.Policy.PolicyApplies;

public class CitizenManager {
	
	public static ArrayList<Citizen> citizens = new ArrayList<Citizen>();
	
	public static void initCitizens() {
		File directory = new File(PowerCivs.path + "/Players");

		File[] list = directory.listFiles();

		for (File f : list) {
			if (f.isFile()) {
				try {
					FileInputStream fis = new FileInputStream(f);
					ObjectInputStream ois = new ObjectInputStream(fis);

					String name = f.getName();
					
					name = name.substring(0, name.indexOf("."));
					
					Citizen cit = (Citizen)ois.readObject();
					
					Citizen c = new Citizen(name);
					c.money = cit.money;
					
					addCitizen(c);
					
					ois.close();
					fis.close();
				} catch (Exception e) {
					return;
				}
			}
		}
	}
	
	static boolean found = false;
	
	public static void addCitizen(Citizen citizen) {
		if(!citizens.contains(citizen))
				citizens.add(citizen);			
	}
	
	public static boolean exists(Citizen citizen) {
		if(citizens.contains(citizen))
			return true;
		else
			return false;
	}
	
	public static Citizen getCitizen(String name) {
		for(Citizen cit : citizens) {
			if(cit.displayName.equals(name)) {
				return cit;
			}else {
				continue;
			}
		}
		return null;
	}

	public static void saveCitizen() {
		try {			
			for(Citizen cit : citizens) {
				
				
				FileOutputStream ft = new FileOutputStream(PowerCivs.path + "/Players/" + cit.displayName + ".dat");
				ObjectOutputStream os = new ObjectOutputStream(ft);
				os.writeObject(cit);
				ft.flush();
				ft.close();
			}
		}catch(Exception e) {return;}
	}
	
	public static void takeTax() {
		try {
			for(Citizen c : citizens) {				
				Nation n = NationManager.getNationByCit(c.displayName);
				
				for(Policy p : n.policies) {
					if(p.appliesTo().equals(PolicyApplies.CITIZENS)) {
						int bal = Integer.valueOf(c.money);
						
						double take = bal * p.value;
						
						n.addCapital(take);
						c.addMoney((int)-take);
					}
				}
				
			}
		}catch(Exception e) {
			return;
		}
	}
	
}