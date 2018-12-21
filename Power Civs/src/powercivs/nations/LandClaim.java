package powercivs.nations;

import java.io.Serializable;
import java.util.Random;

import org.bukkit.Bukkit;

public class LandClaim implements Serializable{

	private static final long serialVersionUID = 1L;
	protected final String chX, chY;
	protected final Nation owner;
	protected final String owner_private; // for private claims
	
	protected int claimAmount = 0;
	
	protected boolean capitalClaim = false;
	
	public LandClaim(String chX, String chY, Nation owner) {
		this.chX = chX;
		this.chY = chY;
		this.owner = owner;
		this.owner_private = "?";
		
		Random r = new Random();
		
		claimAmount = r.nextInt(250);
		
		owner.bank.addCapital(-claimAmount);
		
		ClaimManager.addClaim(this);
	}
	
	@SuppressWarnings("deprecation")
	public LandClaim(String chX, String chY, String owner_private) {
		this.chX = chX;
		this.chY = chY;
		if(NationManager.getNationByCit(Bukkit.getPlayer(owner_private).getUniqueId().toString()) != null) {
			this.owner = NationManager.getNationByCit(Bukkit.getPlayer(owner_private).getUniqueId().toString());
		}else {			
			this.owner = null;
		}
		this.owner_private = owner_private;
		
		Random r = new Random();
		
		claimAmount = r.nextInt(250);
		
		ClaimManager.addClaim(this);
	}
	
	public String getOwner() {
		if(owner != null)
			return owner.getNationName();
		else
			return "?";
	}
	
	public String XandY() {
		return chX + ", "+chY;
	}
	
	public String getPrivateOwner() {
		return owner_private;
	}
	
	public void setCapitalClaim(boolean tof) {
		this.capitalClaim = tof;
	}
	
	public boolean isCapitalClaim() {
		return this.capitalClaim;
	}
	
}