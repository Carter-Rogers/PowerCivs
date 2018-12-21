package powercivs.nations;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import powercivs.GovernmentAsset;
import powercivs.GovernmentElection;
import powercivs.GovernmentElection.ElectionCandidate;
import powercivs.GovernmentElection.GovernmentOfficial;
import powercivs.GovernmentEntity;
import powercivs.nations.economy.Bank;
import powercivs.nations.laws.Law;
import powercivs.nations.legislation.Policy;
import powercivs.nations.legislation.Policy.PolicyApplies;
import powercivs.nations.legislation.TaxPolicy;

public class Nation extends GovernmentEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	public NationType type;

	protected String nationName;
	protected String mayor;
	protected double treasury;
	protected UUID mayorUUID;

	protected ArrayList<Citizen> citizens = new ArrayList<Citizen>();
	public ArrayList<String> enemies = new ArrayList<String>();
	protected ArrayList<Law> laws = new ArrayList<Law>();
	public ArrayList<Policy> policies = new ArrayList<Policy>();

	public double corporate;
	public Bank bank;

	public final TaxPolicy flat_tax = new TaxPolicy("Flat Tax", .03, PolicyApplies.CITIZENS);

	public String homeX, homeY, homeZ;

	public Nation(NationType type, String nationName, Player player) {
		this.type = type;
		this.nationName = nationName;
		this.corporate = 0;
		this.bank = new Bank(50.0d);

		if (player != null) {
			this.mayor = player.getDisplayName();
			this.mayorUUID = player.getUniqueId();
			addCitizen(new Citizen(player.getDisplayName()));
		} else {
			this.mayor = "NO_MAYOR";
		}
	}
	
	public ElectionCandidate getCandidate(String name) {
		for(ElectionCandidate c : candidates) {
			if(c.getPlayer().equals(name)) {
				return c;
			}
		}
		return null;
	}

	public void newElection(GovernmentOfficial official) {
		presidential = new GovernmentElection(official, null);
		presidential.done = true;
		
		if (presidential.done == true) {
			GovernmentElection ge = new GovernmentElection(official, NationManager.getNation(nationName));
			presidential = ge;
			presidential.population = citizens.size();
			this.broadcastToCitizens(ChatColor.GOLD + "The " + ChatColor.BLUE + presidential.getPosition() + ChatColor.GOLD + " election is now open! You can register with /e register !");
		} else {
			presidential.population = citizens.size();
			Bukkit.getPlayer(mayorUUID).sendMessage("ALREADY GOING ON!" + presidential.population);
		}

	}

	public void generateAssetValue() {
		for (GovernmentAsset asset : assets) {
			if (asset.counted != true) {
				if (asset instanceof GovernmentAsset.ItemAsset) {
					int quantity = ((GovernmentAsset.ItemAsset) asset).quantity;
					switch (((GovernmentAsset.ItemAsset) asset).itemID) {
					case "DIAMOND":
						for (int i = 0; i < quantity; i++) {
							bank.addCapital(GovernmentAsset.GEMS);
						}
						asset.counted = true;
						return;
					}
				}

			}
		}
	}

	public void addCitizen(Citizen citizen) {
		if (citizens.contains(citizen)) {
			return;
		} else {
			citizens.add(citizen);
		}
	}

	public void removeCitizen(Citizen citizen) {
		try {
			for (Citizen c : citizens) {
				if (c.displayName.equals(citizen.displayName)) {
					citizens.remove(c);
				} else {
					continue;
				}
			}
		} catch (Exception e) {
			return;
		}
	}

	public void addPolicy(String name, double value, PolicyApplies applies) {
		if (policies.contains(new Policy(name, value, applies)))
			return;
	}

	public void removePolicy(String name) {
		try {
			for (Policy p : policies) {
				if (p.getPolicyName().equals(name)) {
					policies.remove(p);
				} else {
					continue;
				}
			}
		} catch (Exception e) {
			return;
		}
	}

	public void broadcastToCitizens(String message) {
		for (Citizen c : citizens) {
			@SuppressWarnings("deprecation")
			Player p = Bukkit.getPlayer(c.getDisplayName());
			if (p == null)
				return;
			else
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
		if (enemies.contains(name))
			return true;
		else
			return false;
	}

	public void setHome(String chX, String chY, String chZ) {
		this.homeX = chX;
		this.homeZ = chZ;
		this.homeY = chY;
	}

	public void setMayor(Player p) {
		this.mayor = p.getDisplayName();
		this.mayorUUID = p.getUniqueId();
		NationManager.saveNations();
	}

	public static enum NationType {
		DEMOCRATIC, COMMUNISTIC, SOCIALISTIC
	}

}