package dbTable.pojo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import myTools.StaticMethod;

public class Road_POJO {
	
	public enum Field{
    	ROAD, WEIGHT
    }
	
	Map<String,Integer> numMap=new HashMap<String, Integer>();
	
	double sum;
	List<Two_Tuple> numList=null;
	
	public void addRoad(String roadId)
	{
		if(!numMap.containsKey(roadId))
		{
			numMap.put(roadId, 0);
		}
		sum++;
		numMap.put(roadId, numMap.get(roadId)+1);
	}
	
	private void finish()
	{
		numList=new ArrayList<Two_Tuple>();
		for(String roadId:numMap.keySet())
		{
			double num=numMap.get(roadId)/sum;
			num=StaticMethod.myRound(num, 4);
			Two_Tuple tt=new Two_Tuple(roadId, num);
			numList.add(tt);
		}
		numMap=null;
		Collections.sort(numList);
	}
	
	public String getRoad(int n)
	{
		return getValue(n, Field.ROAD);
	}
	
	public String getWeight(int n)
	{
		return getValue(n, Field.WEIGHT);
	}
	
	private String getValue(int n, Field field)
	{
		if(numList==null)
		{
			finish();
		}
		int len=Math.min(n, numList.size());
		if(len==0)
		{
			return "";
		}
		
		String res="";
		for(int i=0;i<len;i++)
		{
			switch(field)
			{
				case ROAD:
					res+=numList.get(i).roadId+",";
					break;
				case WEIGHT:
					res+=numList.get(i).num+",";
			}
		}
		res=res.substring(0, res.length()-1);
		return res;
	}
}

class Two_Tuple implements Comparable<Two_Tuple>{
	String roadId;
	double num;
	
	
	public Two_Tuple(String roadId, double num) {
		super();
		this.roadId = roadId;
		this.num = num;
	}



	@Override
	public int compareTo(Two_Tuple o) {
		return -Double.compare(num, o.num);
	}
	
}