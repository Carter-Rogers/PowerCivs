package powercivs.corporations;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import powercivs.PowerCivs;
import powercivs.nations.Citizen;
import powercivs.nations.CitizenManager;
import powercivs.nations.Nation;
import powercivs.nations.NationManager;

public class Corporation implements Serializable {

	private static final long serialVersionUID = 1L;

	protected Nation homeNation;

	protected double capital;
	protected double charityTax = 2; // default is 2$ out of 100<2%ofcapitalgiventostate>

	public double employeePercentage = 2;

	public UUID CEO;
	protected String ceo;
	protected ArrayList<String> employees = new ArrayList<String>();

	protected String corporationName;

	public Corporation(String corporationName, UUID CEO) {
		this.corporationName = corporationName;
		this.CEO = CEO;
		this.ceo = CEO.toString();
		Nation n = NationManager.getNationByCit(Bukkit.getPlayer(CEO).getDisplayName());
		this.homeNation = n;
		addEmployee(Bukkit.getPlayer(CEO).getDisplayName());

		this.capital = 100.0;// default starting capital;
	}

	public void addEmployee(String player) {
		if (!employees.contains(player)) {
			employees.add(player);
			Bukkit.broadcastMessage(ChatColor.GOLD + corporationName + " just hired a new employee!");
		} else
			return;
	}

	public void payEmployees() {
		for (String s : employees) {
			Citizen c = CitizenManager.getCitizen(s);
			
			if(c != null) {
				c.addMoney((int)(capital * (employeePercentage / 100)));
				capital -= ((int)capital * (employeePercentage / 100));
			}
			
			CitizenManager.saveCitizen();
			
			
		}
	}

	public void setCEO(Player sender, Player newCEO) {
		if (sender.getUniqueId() == CEO)
			this.CEO = newCEO.getUniqueId();
		if (!employees.contains(sender.getDisplayName())) {
			employees.add(sender.getDisplayName());
		} else
			return;

	}

	public double getCapital() {
		return capital;
	}

	public String getName() {
		return corporationName;
	}

	public String getNation() {
		return homeNation.getNationName();
	}

	public void takeTax() {
		try {
			double val = homeNation.getCorporateRate();
			val = val /= 100;
			double takeAmount = capital * val;
			capital = capital - takeAmount;
			NationManager.getNation(homeNation.getNationName()).addCapital(takeAmount);
			PowerCivs.saveNations();
			CitizenManager.saveCitizen();
		} catch (Exception e) {
			return;
		}
	}

}