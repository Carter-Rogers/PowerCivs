package powercivs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import powercivs.GovernmentElection.ElectionCandidate;
import powercivs.nations.LandClaim;

public abstract class GovernmentEntity implements Serializable{

	public ArrayList<GovernmentAsset> assets = new ArrayList<GovernmentAsset>();
	public  ArrayList<ElectionCandidate> candidates = new ArrayList<ElectionCandidate>();
	
	protected GovernmentElection presidential;
	
	private static final long serialVersionUID = 1L;

	boolean has(String itemID) {
		for(GovernmentAsset asset : assets) {
			if(((GovernmentAsset.ItemAsset)asset).itemID.equals(itemID)) {
				return true;
			}else {
				continue;
			}
		}
		return false;
	}
	
	public int indexOf(String itemID) {
		for(GovernmentAsset asset : assets) {
			if(((GovernmentAsset.ItemAsset)asset).itemID.equals(itemID)) {
				return assets.indexOf(asset);
			}else {
				continue;
			}
		}
		return -1;
	}
	
	@SuppressWarnings("deprecation")
	public void addAsset(UUID uuid, int quantity) {
		Player p = Bukkit.getPlayer(uuid);
		
		Bukkit.broadcastMessage(p.getItemInHand().getType().name());
		
		if(has(p.getItemInHand().getType().name())) {
			if(p.getItemInHand().getAmount() >= quantity) {
				((GovernmentAsset.ItemAsset)assets.get(indexOf(p.getItemInHand().getType().name()))).quantity += quantity;			
				if(p.getItemInHand().getAmount() > quantity) {
					p.getItemInHand().setAmount(p.getItemInHand().getAmount() - quantity);
				}else{

					p.getInventory().removeItem(p.getItemInHand());
				}
			}
		}else {
			if(p.getItemInHand().getAmount() >= quantity) {
				assets.add(new GovernmentAsset.ItemAsset(p.getItemInHand().getType().name(), quantity));				
				if(p.getItemInHand().getAmount() > quantity) {
					p.getItemInHand().setAmount(p.getItemInHand().getAmount() - quantity);
				}else{
					p.getInventory().removeItem(p.getItemInHand());
				}
			}
		}
	}
	
	public void addAsset(LandClaim claim) {
		
	}
	
}