package com.powercivs.entities;

import java.io.Serializable;

public abstract class Entity implements Serializable{
	
	private static final long serialVersionUID = 1L;
	protected String name;
	
	public Entity(String name) {
		this.name = name;
	}
	
	public abstract void update();

	public String getName() {
		return name;
	}
	
}