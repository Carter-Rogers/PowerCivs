package powercivs.nations.economy;

public class Bank extends Economy{

	private static final long serialVersionUID = 1L;

	protected double capital;
	
	public Bank(double start) {
		this.capital = start;
	}
	
	public void addCapital(double amount) {
		this.capital += amount;
	}

	public double getCapital() {
		return capital;
	}
	
	public void setBank(double amount) {
		this.capital = amount;
	}
	
}