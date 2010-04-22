package polii.aggregators.moties;

import java.util.LinkedList;
import java.util.List;

public class VoteResult {

	
	private List<String> votedYes;
	private List<String> votedNo;
	private String subject;
	
	public VoteResult()
	{
		votedYes = new LinkedList<String>();
		votedNo = new LinkedList<String>();
		subject = new String();
	}
	
	public void addYesVote(String name)
	{
		votedYes.add(name);
	}
	
	public void addNoVote(String name)
	{
		votedNo.add(name);
	}

	public void setSubject(String name)
	{
		subject = name;
	}
	
	public List<String> getYesVotes()
	{
		return votedYes;
	}
	
	public List<String> getNoVotes()
	{
		return votedNo;
	}
	
	public String getSubject()
	{
		return subject;
	}
	
	/**
	 * In some parliament proceedings a no vote is not always explicitly written down. Therefore substract the yes-voters from all things can vote and add these as no-votes
	 * @param all
	 */
	
	public void addNoVotersByDefault(List<String> all)
	{
		List<String> no_voters = new LinkedList<String>(all);
		no_voters.removeAll(votedYes);
		votedNo = no_voters;
	}
	
	
}
