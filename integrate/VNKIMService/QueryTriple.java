package org.me.VNKIMService;

import java.io.BufferedReader;
import java.io.FileReader;

//import org.apache.xerces.parsers.DOMParser;
//import org.w3c.dom.Document;
//import org.w3c.dom.Element;
//import org.w3c.dom.NodeList;

/*class TripleType
{
public ItemType subject = null;
public ItemType rel = null;
public ItemType object = null;
public String relationName = "";
public int direction = 1;//1: left->right 2: right->left
public boolean isDelete = false;
}*/

public class QueryTriple
{
	public static int MaxItem = 100;
	public int length = 0;
	public int currindex = 0;
	public TripleType[] tripleset;	
	public int count=0;
	
	public QueryTriple()
	{
		tripleset = new TripleType[MaxItem];
	}
	
	public void reset()
	{
		length = 0;
		currindex = 0;
		count = 0;
	}
	public void SetItemRow(QueryBuffer buffer, ItemType item, int row)
	{
		item.row = row;
		if (buffer.totalrow < row)
			buffer.totalrow = row;
	}

	public void SetItemCol(QueryBuffer buffer, ItemType item, int col)
	{
		item.col = col;
		if (buffer.totalcol < col)
			buffer.totalcol = col;
	}

	public static boolean IsDirectRelation(String value) throws Exception
	{
		String path = GateNamedEntity.serverpath + "directrelation.txt";
		BufferedReader in = new BufferedReader(new FileReader(path));

		String dictentry;
		while ( (dictentry = in.readLine()) != null)
		{
			if (dictentry.compareTo("")!=0 && dictentry.compareToIgnoreCase(value)==0)
				return true;
		}
		in.close();

		
		return false;		
	}
	public void LoadDataNew(QueryBuffer buffer) throws Exception
	{
		int i=0;
		int j=2;
		int k=1;
		TripleType tmp = new TripleType();
		tmp.subject = buffer.getItem(i);
		tmp.rel = buffer.getItem(j);
		tmp.object = buffer.getItem(k);
		/*if (length == 0)
		{
			//SetItemRow(buffer, tmp.subject, 1);
			//SetItemCol(buffer, tmp.subject, 1);
		}
		
		tmp.rel.subindex = i;
		tmp.rel.objindex = k;
		tmp.subject.totalrel++;*/
		//SetItemRow(buffer, tmp.rel, tmp.subject.row -1 + tmp.subject.totalrel);
		//SetItemCol(buffer, tmp.rel, tmp.subject.col + 1);
		
		//SetItemRow(buffer, tmp.object, tmp.rel.row);
		//SetItemCol(buffer, tmp.object, tmp.rel.col + 1);
		
		tripleset[length] = tmp;
		length++;
		/*int subjindex = 0;
		int nearestUE = -1;
		int firstUE = -1;
		for(int i=0; i < buffer.length; i++)
		{
			if (buffer.getItem(i).classType.compareTo("RW")==0 )
			{
				if (buffer.getItem(i-1).classType.compareTo("CONJ")==0 )
					buffer.setItem(i-1, firstUE);
				
				TripleType tmp = new TripleType();
				if (length == 0) // first element
				{
					tmp.subject = buffer.getItem(i-1);
					SetItemRow(buffer, tmp.subject, 1);
					SetItemCol(buffer, tmp.subject, 1);
					if (tmp.subject.classType.compareTo("UE")==0)
					{
						nearestUE = i-1;
						if (firstUE == -1)
							firstUE = nearestUE;
					}
					subjindex = i-1;
				}
				else
				{
					if (buffer.getItem(i-1).classType.compareTo("UE")==0 || nearestUE==-1 || buffer.getItem(i-1).className.compareToIgnoreCase("JobPosition")==0  
						|| IsDirectRelation(buffer.getItem(i).value)==true)
					{
						tmp.subject = buffer.getItem(i-1);
						subjindex = i-1;
					}
					else
					{
						tmp.subject = buffer.getItem(nearestUE);
						subjindex = nearestUE;
					}						
				}
				tmp.rel = buffer.getItem(i);
				tmp.rel.subindex = subjindex;
				tmp.rel.objindex = i+1;
				tmp.subject.totalrel++;
				SetItemRow(buffer, tmp.rel, tmp.subject.row -1 + tmp.subject.totalrel);
				SetItemCol(buffer, tmp.rel, tmp.subject.col + 1);
				
				tmp.object = buffer.getItem(i+1);
				SetItemRow(buffer, tmp.object, tmp.rel.row);
				SetItemCol(buffer, tmp.object, tmp.rel.col + 1);
				if (tmp.object.classType.compareTo("UE")==0)
				{
					nearestUE = i+1;
					if (firstUE == -1)
						firstUE = nearestUE;
				}
				tripleset[length] = tmp;
				length++;					
			}
		}*/
	}
	public void LoadData(QueryBuffer buffer) throws Exception
	{
		int subjindex = 0;
		int nearestUE = -1;
		int firstUE = -1;
		for(int i=0; i < buffer.length; i++)
		{
			if (buffer.getItem(i).classType.compareTo("RW")==0 )
			{
				if (buffer.getItem(i-1).classType.compareTo("CONJ")==0 )
					buffer.setItem(i-1, firstUE);
				
				TripleType tmp = new TripleType();
				if (length == 0) // first element
				{
					tmp.subject = buffer.getItem(i-1);
					SetItemRow(buffer, tmp.subject, 1);
					SetItemCol(buffer, tmp.subject, 1);
					if (tmp.subject.classType.compareTo("UE")==0)
					{
						nearestUE = i-1;
						if (firstUE == -1)
							firstUE = nearestUE;
					}
					subjindex = i-1;
				}
				else
				{
					if (buffer.getItem(i-1).classType.compareTo("UE")==0 || nearestUE==-1 || buffer.getItem(i-1).className.compareToIgnoreCase("JobPosition")==0  
						|| IsDirectRelation(buffer.getItem(i).value)==true)
					{
						tmp.subject = buffer.getItem(i-1);
						subjindex = i-1;
					}
					else
					{
						tmp.subject = buffer.getItem(nearestUE);
						subjindex = nearestUE;
					}						
				}
				tmp.rel = buffer.getItem(i);
				tmp.rel.subindex = subjindex;
				tmp.rel.objindex = i+1;
				tmp.subject.totalrel++;
				SetItemRow(buffer, tmp.rel, tmp.subject.row -1 + tmp.subject.totalrel);
				SetItemCol(buffer, tmp.rel, tmp.subject.col + 1);
				
				tmp.object = buffer.getItem(i+1);
				SetItemRow(buffer, tmp.object, tmp.rel.row);
				SetItemCol(buffer, tmp.object, tmp.rel.col + 1);
				if (tmp.object.classType.compareTo("UE")==0)
				{
					nearestUE = i+1;
					if (firstUE == -1)
						firstUE = nearestUE;
				}
				tripleset[length] = tmp;
				length++;					
			}
		}
	}
		

	public void LoadDataOld(QueryBuffer buffer)
	{
		int j = 0;
		for(int i=0; i < buffer.length; i = i + 3)
		{			
			if (i >= 3)
				j = i - 1;
			TripleType tmp = new TripleType();
			tmp.subject = buffer.getItem(j);
			if ( j + 1 < buffer.length)
				tmp.rel = buffer.getItem(j+1);
			if ( j + 2 < buffer.length)
				tmp.object = buffer.getItem(j+2);
			tripleset[length] = tmp;
			length++;
		}
	}

	public void LoadDataOld2(QueryBuffer buffer)
	{

		for(int i=0; i < buffer.length; i++)
		{
			if (buffer.getItem(i).classType.compareTo("RW")==0 )
			{
				TripleType tmp = new TripleType();
				//if (buffer.getItem(i-1).classType.compareTo("CONJ")==0)
					//tmp.subject = buffer.getItem(i-2);
				//else
				tmp.subject = buffer.getItem(i-1);
				tmp.rel = buffer.getItem(i);
				if ( i + 1 < buffer.length)
					tmp.object = buffer.getItem(i+1);
				tripleset[length] = tmp;
				length++;				
			}			
		}
	}
	
	public TripleType getItem(int index)
	{
		if (index > length)
			return null;
		return tripleset[index];
	}
		
}