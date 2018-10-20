package powercivs.nations.legislation;

import java.io.Serializable;

public class Policy implements Serializable{


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	public static boolean enacted = false;
	
	protected String policyName;
	public double value;
	protected PolicyApplies applies;
	
	public Policy(String name, double value2, PolicyApplies applies) {
		this.policyName = name;
		if(value2 > 1)
			this.value = value2 /= 100;
		else
			this.value = value2;
		this.applies = applies;
	}
	
	public PolicyApplies appliesTo() {
		return applies;
	}

	public String getPolicyName() {
		return policyName;
	}
	
	public double getValue() {
		return value;
	}
	
	public static enum PolicyApplies implements Serializable {
		
		CITIZENS("citizens");
		
		String applies;
		
		PolicyApplies(String applies) {
			this.applies = applies;
		}
		
		public String getApplies() {
			return applies;
		}
	}
	
	
}