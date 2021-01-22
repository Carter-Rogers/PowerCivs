package com.powercivs.claims;

import static com.powercivs.entities.EntityManager.*;

public class LandClaim {
	
	private String chX, chY, owner, plot_owner = null;
	
	public boolean setClaimOwner(String chX, String chY, String owner) {
		boolean claimed = false;
		
		for(LandClaim lc : claims) {
			if(lc.chX.equalsIgnoreCase(chX) && lc.chY.equalsIgnoreCase(chY) && !owner.equalsIgnoreCase(owner)) {
				return false;
			}else {
				claimed = true;
			}
		}
		
		if(!claimed) {
			this.chX = chX;
			this.chY = chY;
			this.owner = owner;
			return true;
		}else {
			return false;
		}
		
	}

	public String getOnwer() {
		return owner;
	}
	
	public String getPlotOwner() {
		return plot_owner;
	}
	
}