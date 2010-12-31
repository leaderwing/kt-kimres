package org.me.VNKIMService;

import BayesianInferences.Inference;
import BayesianNetworks.BayesNet;
import BayesianNetworks.DiscreteFunction;
import BayesianNetworks.DiscreteVariable;

class MLRelationExtraction
{
	public static boolean HasOrganization(QueryBuffer buffer) throws Exception
	{
		boolean found = false;
		for(int i=0; i<buffer.length; i++)
		{
			ItemType tmp = buffer.getItem(i); 
			if (ProcessingQuery.IsChildClass(tmp.className, "Organization"))
			{
				found = true;
				break;				
			}			
		}
		
		return found;		
	}
		
	public static void findRelationbyML(QueryTriple tripleset, QueryBuffer buffer) throws Exception
	{
		for(int i=0; i < tripleset.length; i++)
		{				
			TripleType querytriple = tripleset.getItem(i);
			ItemType subject = querytriple.subject;
			ItemType rel = querytriple.rel;
			ItemType object = querytriple.object;
			
			if (rel.className.compareTo("RW")==0 && rel.value.compareTo("in")==0 && rel.relcount==0 && rel.delete==false)
			{
				boolean person = false, location = false, organization = false, born = false, live = false, work = false, in = false;
				if (ProcessingQuery.IsChildClass(subject.className, "Person") || ProcessingQuery.IsChildClass(object.className, "Person"))
					person = true;
				if (ProcessingQuery.IsChildClass(subject.className, "Location") || ProcessingQuery.IsChildClass(object.className, "Location"))
					location = true;
				if (person == false)
					continue;
				String middlestr = buffer.getmiddlestring(rel.subindex, rel.objindex);
				middlestr = middlestr.trim();
				String wordlist[] = middlestr.split(" ");
				for(int j=0; j < wordlist.length; j++)
				{
					if ( wordlist[j].startsWith("live"))
						live = true;
					if (wordlist[j].startsWith("work"))
						work = true;
					if (wordlist[j].startsWith("born"))
						born = true;
					if (wordlist[j].startsWith("in"))
						in = true;					
				}

				if (HasOrganization(buffer))
					organization = true;
				String relation = MLFindRelation(person, location, organization, born, work, live, in);
				if ( relation.compareTo("") != 0)
				{
					int dir = 2;
					if (ProcessingQuery.IsChildClass(subject.className, "Person"))
						dir = 1;
									
					querytriple.rel.InsertRelation(relation, dir, querytriple);
				}
				
			}			
			
		}
	}
	
	public static void setobserved(BayesNet network, String var)
	{
		DiscreteVariable pv = network.get_probability_variable(var);
		if (pv != null) 
		{	
			pv.set_observed_value("true");
		}		
	}
	
	public static double relation_probability(String relation, boolean person, boolean location, boolean organization, boolean born, boolean work, boolean live, boolean in) throws Exception
	{
		double rate = 0.0;
		BayesNet network;
		DiscreteVariable pv;
		Inference inf;
		DiscreteFunction result;
		  
		WorkLiveBornIn nwclass = new WorkLiveBornIn();
		network = (BayesNet)(nwclass);
		//set observed
		if (person == true)
			setobserved(network, "person");
		if (location == true)
			setobserved(network, "location");
		if (organization == true)
			setobserved(network, "organization");
		if (work == true)
			setobserved(network, "work");		
		if (live == true)
			setobserved(network, "live");		
		if (born == true)
			setobserved(network, "born");		
		if (in == true)
			setobserved(network, "in");		

		
		pv = network.get_probability_variable(relation);
	    if (pv != null) 
	    {
	    	inf = new Inference(network, false);
	        inf.inference(relation);
	        result = inf.get_result();
	        rate = result.get_value(0); // true value  result.get_value(1)-- false value 
	    }		  
		
		return rate;
	}
	
	public static String MLFindRelation(boolean person, boolean location, boolean organization, boolean born, boolean work, boolean live, boolean in) throws Exception
	{
		double maxrate = 0;
		String relation = "";
		
		double livein_rate = relation_probability("live_in", person, location, organization, born, work, live, in);
		double workin_rate = relation_probability("work_in", person, location, organization, born, work, live, in);
		double bornin_rate = relation_probability("born_in", person, location, organization, born, work, live, in);
	
		if (livein_rate > maxrate)
		{
			relation = "live_in";
			maxrate = livein_rate;
		}
		
		if (workin_rate > maxrate)
		{
			relation = "work_in";
			maxrate = workin_rate;			
		}

		if (bornin_rate > maxrate)
		{
			relation = "born_in";
			maxrate = bornin_rate;			
		}
		
		return relation;
	}
	
}