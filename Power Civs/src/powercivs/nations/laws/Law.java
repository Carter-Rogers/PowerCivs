package powercivs.nations.laws;

import java.io.Serializable;

public class Law implements Serializable{
	
	public static final long serialVersionUID = 1L;
	
	public static final Law test = new Law("TEST_LAW", 0);
	
	protected final String NAME;
	protected int fineAmmount = 0;
	protected boolean jailTime = false; //jail-time is default of 1 in-game day.
	
	public Law(String NAME, int fineAmount) {
		this.NAME = NAME;
		if(fineAmmount <= 0)
			jailTime = true;
		else
			this.fineAmmount = fineAmount;
	}
	
}