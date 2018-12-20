package powercivs;

import java.io.Serializable;

import powercivs.nations.LandClaim;

public class GovernmentAsset implements Serializable{

	public boolean counted = false;
	
	private static final long serialVersionUID = 1L;

	/*Government Assets Include The Following
	 * Properties (privately owned chunks within a town/nation)
	 * Corporations (their values calculate asset value!)
	 * Items (registered trade items)
	 * Government Balance
	 */
	
	public static final int GEMS = 25; //$25 per gem in asset bank
	
	public static class Property extends GovernmentAsset {
		private static final long serialVersionUID = 1L;
		
		public LandClaim claim;
		
		public Property(LandClaim claim) {
			this.claim = claim;
		}
		
	}
	
	public static class ItemAsset extends GovernmentAsset {
		private static final long serialVersionUID = 1L;
		
		public String itemID;
		public int quantity;
		
		public ItemAsset(String itemID, int quantity) {
			this.itemID = itemID;
			this.quantity = quantity;
		}
	}

}