package powercivs.nations;

import java.io.Serializable;
import java.util.Random;

public class LandClaim implements Serializable{

	private static final long serialVersionUID = 1L;
	protected final String chX, chY;
	protected final Nation owner;
	
	protected int claimAmount = 0;
	
	public LandClaim(String chX, String chY, Nation owner) {
		this.chX = chX;
		this.chY = chY;
		this.owner = owner;
		
		Random r = new Random();
		
		claimAmount = r.nextInt(250);
		
		owner.bank.addCapital(-claimAmount);
		
		ClaimManager.addClaim(this);
	}
	
	public String getOwner() {
		return owner.getNationName();
	}
	
	public String XandY() {
		return chX + ", "+chY;
	}
	
}