package powercivs.nations;

import java.io.Serializable;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class Citizen implements Serializable{

	private static final long serialVersionUID = 1L;
	protected String displayName;
	protected String lastNation = "";
	public String money = "1000";
	
	public ArrayList<String> requestingNations;
	
	protected boolean jailed = false, warrant = false;
	
	public Citizen(String displayName) {
		this.displayName = displayName;
		this.money = "1000";
		this.requestingNations = new ArrayList<String>();
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public int getMoney() {
		return Integer.parseInt(money);
	}
	
	@SuppressWarnings("deprecation")
	public void addRequest(String request) {
		requestingNations.add(request);
		Bukkit.getPlayer(displayName).sendMessage(ChatColor.BLUE + request + " Has Asked You To Join Their Nation! Accept with /accept <nation name>!");
	}
	
	public void addMoney(int amount) {
		int mon = getMoney() + amount;
		money = String.valueOf(mon);
	}
		
}