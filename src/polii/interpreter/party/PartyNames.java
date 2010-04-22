package polii.interpreter.party;

import java.util.LinkedList;
import java.util.List;

public class PartyNames {

	
	//FIXME: recode to take date of document into account
	//FIXME: refactor to retrieve partynames from repository
	public static List<String> partyShortNames()
	{
		List<String> names = new LinkedList<String>();
	
		names.add("D66");
		names.add("CDA");
		names.add("Verdonk");
		names.add("PVV");
		names.add("VVD");
		names.add("SGP");
		names.add("GroenLinks");
		names.add("ChristenUnie");
		names.add("PvdD");
		names.add("PvdA");

		
		
		return names;
	}
	
	
}
