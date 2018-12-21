package powercivs;

import java.io.Serializable;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import powercivs.nations.Citizen;
import powercivs.nations.CitizenManager;
import powercivs.nations.Nation;
import powercivs.nations.NationManager;

public class GovernmentElection implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public boolean done = false;
	
	public int population = 0;
	
	protected Nation nation;
	
	protected GovernmentOfficial position = GovernmentOfficial.PRESIDENT;
	
	public GovernmentElection(GovernmentOfficial position, Nation nation) {
		this.position = position;
		this.nation = nation;
		int index = 0;
		for (Citizen c : CitizenManager.citizens) {
			if (NationManager.playerBelongs(c) == nation) {
				index += 1;
			}
		}
		population = index;
	}
	
	public void registerCandidate(ElectionCandidate candidate) {
		
		if(nation.candidates.contains(candidate)) {
			return;
		}else {
			nation.candidates.add(candidate);
			NationManager.getNationByCit(candidate.player).broadcastToCitizens(candidate.player + " has registered as a candidate in the " + position.name + " race! Be sure to vote with /n vote <candidate_name>");
		}
	}
	
	@SuppressWarnings("deprecation")
	public void endElection() {
		int max = Integer.MIN_VALUE;
		for(int i = 0; i < nation.candidates.size(); i++) {
			if(nation.candidates.get(i).votes > max) {
				max = nation.candidates.get(i).votes;
			}
		}
		
		try {
			for(ElectionCandidate candidate : nation.candidates) {
				if(candidate.votes == max) {
					
					NationManager.getNationByCit(candidate.player).broadcastToCitizens(ChatColor.GOLD + "The Official Winner Of The Election Is " + candidate.player + "! This Candidate Received " + (candidate.votes ) + " Of Votes!");
					
					NationManager.getNationByCit(candidate.player).setMayor(Bukkit.getPlayer(candidate.player));
					nation.candidates.clear();
					done = true;
				}
			}
		}catch(Exception e) {
			return;
		}
		
		
	}
	
	public String getPosition() {
		return position.name;
	}

	public static class ElectionCandidate implements Serializable{
		private static final long serialVersionUID = 1L;
		protected Nation nation;
		protected String player;
		
		public ArrayList<String> voted = new ArrayList<String>();
		
		protected int votes = 0; // for elections
		
		public ElectionCandidate(String player) {
			this.player = player;
			this.nation = NationManager.getNationByCit(player);
		}
		
		@SuppressWarnings("deprecation")
		public void addVote(String citizen) {
			if(voted.contains(citizen)) {
				Bukkit.getPlayer(citizen).sendMessage(ChatColor.RED + "CHEATING IS NOT THE WAY TO WIN!");
				return;
			}else {
				votes += 1;
				voted.add(citizen);				
			}
		}
		
		public String getPlayer() {
			return player;
		}
		
	}
	
	public enum GovernmentOfficial {
		PRESIDENT("president");
		
		public String name;
		
		GovernmentOfficial(String name) {
			this.name= name;
		}
	}
	
}