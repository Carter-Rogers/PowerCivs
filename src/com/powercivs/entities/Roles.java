package com.powercivs.entities;

public enum Roles {
	
	SOS("Secretary of State"), SOD("Secretary of Defense"), SOT("Secretary of the Treasury");

	protected String role;
	
	Roles(String role) {
		this.role = role;
	}
	
	public String getRole() {
		return role;
	}
	
}
