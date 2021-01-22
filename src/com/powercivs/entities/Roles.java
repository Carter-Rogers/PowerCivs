package com.powercivs.entities;

public enum Roles {
	
	VP("Vice President"), SOS("Secretary of State"), SOD("Secretary of Defense"), SOT("Secretary of the Treasury");

	protected String role;
	
	Roles(String role) {
		this.role = role;
	}
	
	public String getRole() {
		return role;
	}
	
}
