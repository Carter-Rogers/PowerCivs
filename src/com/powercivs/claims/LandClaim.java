package com.powercivs.claims;

import java.io.Serializable;

public class LandClaim implements Serializable{
	
	private static final long serialVersionUID = 1L;
	public String chX, chY, owner, plot_owner = null;
	
	public void setClaimOwner(String chX, String chY, String owner) {
		this.chX = chX;
		this.chY = chY;
		this.owner = owner;
	}

	public String getOwner() {
		return owner;
	}
	
	public String getPlotOwner() {
		return plot_owner;
	}
	
}