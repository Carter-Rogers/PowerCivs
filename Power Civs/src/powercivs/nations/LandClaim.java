package powercivs.nations;

import java.io.File;
import java.io.Serializable;
import java.util.Random;

import powercivs.PowerCivs;

public class LandClaim implements Serializable{

	private static final long serialVersionUID = 1L;
	private String chX;
	private String chY;
	protected Nation owner;
	protected String owner_private = null; // for private claims
	
	protected int claimAmount = 0;
	
	protected boolean capitalClaim = false;
	protected boolean forsale = false;
	
	public LandClaim(String chX, String chY, Nation owner) {
		this.setChX(chX);
		this.setChY(chY);
		this.owner = owner;
		this.owner_private = "?";
		
		Random r = new Random();
		
		claimAmount = r.nextInt(250);
		
		owner.bank.addCapital(-claimAmount);
		
		ClaimManager.addClaim(this);
	}
	
	public LandClaim(String chX, String chY, int val) {
		this.setChX(chX);
		this.setChY(chY);
		claimAmount = val;
	}
	
	public LandClaim(String chX, String chY, String owner_private) {
		this.setChX(chX);
		this.setChY(chY);
		this.owner = null;
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
		return getChX() + ","+getChY();
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
	
	public void setForsale(boolean tof) {
		this.forsale = tof;
	}
	
	public boolean isSelling() {
		return forsale;
	}
	
	public void removeLand() {
		File f = new File(PowerCivs.path + "/Claims/" + this.getOwner() + "," + this.getChX() + ","
				+ this.getChY() + ".dat");
		this.owner = null;
		this.owner_private = null;
		f.delete();
	}
	
	public void setPrivate(String owner) {
		File f = new File(PowerCivs.path + "/Claims/" + this.getOwner() + "," + this.getChX() + ","
				+ this.getChY() + ".dat");
		this.owner = null;
		f.delete();
		int price = this.claimAmount;
		String chX = this.chX;
		String chY = this.chY;
		
		boolean remove = true;
		if(remove) {
			ClaimManager.removeClaim(chX, chY);
			remove = false;
		}
		
		LandClaim lc = new LandClaim(chX, chY, price);
		ClaimManager.addClaim(lc);
		ClaimManager.getClaim(chX, chY).owner_private = owner;
		ClaimManager.saveClaims();
	}
	
	public int getClaimAmount() {
		return claimAmount;
	}

	public String getChX() {
		return chX;
	}

	public void setChX(String chX) {
		this.chX = chX;
	}

	public String getChY() {
		return chY;
	}

	public void setOwner(Nation owner) {
		this.owner = owner;
	}
	
	public void setChY(String chY) {
		this.chY = chY;
	}
	
}