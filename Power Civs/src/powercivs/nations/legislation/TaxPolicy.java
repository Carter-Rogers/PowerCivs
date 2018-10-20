package powercivs.nations.legislation;

import java.io.Serializable;

public class TaxPolicy extends Policy implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	protected PolicyApplies applies;
	
	public TaxPolicy(String policyName, double value, PolicyApplies applies) {
		super(policyName, value, applies);
	}
	
	public void setValue(double value) {
		if(value > 1)
			this.value = value /= 100;
		else
			this.value = value;
	}
	
	public double getRate() {
		return value;
	}
	
	
}