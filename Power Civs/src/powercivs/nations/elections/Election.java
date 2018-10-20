package powercivs.nations.elections;

import java.io.Serializable;

public abstract class Election implements Serializable{

	private static final long serialVersionUID = 1L;
	protected String nationName;
	protected final ElectionType type;
	protected final String ELECTION_NAME;
	
	public Election(String nationName, ElectionType type, String ELECTION_NAME) {
		this.nationName = nationName;
		this.type = type;
		this.ELECTION_NAME = ELECTION_NAME;
	}

	public String getElectionName() {
		return ELECTION_NAME;
	}
	
	public String getNationName() {
		return nationName;
	}
	
	public ElectionType getElectionType() {
		return type;
	}

	public static enum ElectionType {
		LEADER;
	}
	
}