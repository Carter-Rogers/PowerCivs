package powercivs.nations.elections;

public class LeaderElection extends Election{

	private static final long serialVersionUID = 1L;

	public LeaderElection(String nationName, String ELECTION_NAME) {
		super(nationName, ElectionType.LEADER, ELECTION_NAME);
	}

	
	
}
