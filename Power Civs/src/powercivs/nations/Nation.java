package powercivs.nations;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import powercivs.nations.economy.Bank;
import powercivs.nations.laws.Law;
import powercivs.nations.legislation.Policy;
import powercivs.nations.legislation.Policy.PolicyApplies;
import powercivs.nations.legislation.TaxPolicy;

public class Nation implements Serializable {

	private static final long serialVersionUID = 1L;

	public NationType type;

	protected String nationName;
	protected String mayor;
	protected double treasury;
	private UUID mayorUUID;

	protected ArrayList<Citizen> citizens = new ArrayList<Citizen>();
	public ArrayList<String> enemies = new ArrayList<String>();
	protected ArrayList<Law> laws = new ArrayList<Law>();
	public ArrayList<Policy> policies = new ArrayList<Policy>();
	
	public double corporate;
	public Bank bank;

	public final TaxPolicy flat_tax = new TaxPolicy("Flat Tax", .03, PolicyApplies.CITIZENS);
	
	public Nation(NationType type, String nationName, Player player) {
		this.type = type;
		this.nationName = nationName;
		this.corporate = 2;
		this.bank = new Bank(50.0d);
		
		if (player != null) {
			this.mayor = player.getDisplayName();
			this.mayorUUID = player.getUniqueId();
			addCitizen(new Citizen(player.getDisplayName()));
		} else {
			this.mayor = "NO_MAYOR";
		}
	}
	
	public void addCitizen(Citizen citizen) {
		if(citizens.contains(citizen)) {
			return;
		}else {
			citizens.add(citizen);
		}
	}
	
	public void removeCitizen(Citizen citizen) {
		try {
			for(Citizen c : citizens) {
				if(c.displayName.equals(citizen.displayName)) {
					citizens.remove(c);
				}else {
					continue;
				}
			}
		}catch(Exception e) {
			return;
		}
	}

	public void addPolicy(String name, double value, PolicyApplies applies) {
		if(policies.contains(new Policy(name, value, applies)))
			return;
	}
	
	public void removePolicy(String name) {
		try {
			for(Policy p : policies) {
				if(p.getPolicyName().equals(name)) {
					policies.remove(p);
				}else {
					continue;
				}
			}	
		}catch(Exception e) {
			return;
		}
 	}
	
	public void broadcastToCitizens(String message) {
		for(Citizen c : citizens) {
			@SuppressWarnings("deprecation")
			Player p = Bukkit.getPlayer(c.getDisplayName());
			p.sendMessage(ChatColor.BLUE + message);
		}
	}
	
	public void addCapital(double amount) {
		this.bank.addCapital(amount);
	}

	public double getCorporateRate() {
		return corporate;
	}

	public String getNationName() {
		return nationName;
	}

	public String getMayor() {
		return mayor;
	}

	public double getTreasury() {
		return bank.getCapital();
	}

	public UUID getUUID() {
		return mayorUUID;
	}
	
	public NationType getType() {
		return type;
	}

	public void setCorporateRate(double rate) {
		this.corporate = rate;
	}
	
	public boolean isEnemy(String name) {
		if(enemies.contains(name)) 
			return true;
		else
			return false;
	}
	
	public static enum NationType {
		DEMOCRATIC, COMMUNISTIC, SOCIALISTIC
	}

}