package jdbc;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pojo.RoadMap;
import myTools.DisPointToLine;
import myTools.StaticValue;
import cell.Cell_OnePoint;

/**
 * 每条路段的信息，包括："起点"、"中点"、"终点"、"路段长度"
 * 
 * @author home
 *
 */
public class Input_Road
{
	double NULL;
	private Map<String, RoadMap> map=new HashMap<String, RoadMap>();
	
	public double getDistByID(String id)
	{
		return map.get(id).getDisFirst_En();
	}
	
	public double getLengthByID(String id)
	{
		return map.get(id).getLength();
	}
	
	public double getWidthByID(String id)
	{
		return map.get(id).getWidth();
	}
	
	public Input_Road()
	{
//		String[] fields=new String[] {"LinkID","first_JD","first_WD","center_JD","center_WD","end_JD","end_WD"};
		String sql="select ID, FIRST_JD, FIRST_WD, CENTER_JD, CENTER_WD, END_JD, END_WD, LENGTH, WIDTH from Map "+
					"where CENTER_JD>"+StaticValue.begin_lon+" and CENTER_JD<"+StaticValue.end_lon+
					" and CENTER_WD>"+StaticValue.begin_lat+" and CENTER_WD<"+StaticValue.end_lat;
		JDBC jdbc=new JDBC();
		try
		{
			ResultSet rs=jdbc.executeQuery(sql);
			while(rs.next()){
				String linkId=remDecimal(rs.getString(1));
				Double first_JD=rs.getDouble(2);
				Double first_WD=rs.getDouble(3);
				Double center_JD=rs.getDouble(4);
				Double center_WD=rs.getDouble(5);
				Double end_JD=rs.getDouble(6);
				Double end_WD=rs.getDouble(7);
				Double length=rs.getDouble(8);
				Double width=rs.getDouble(9);
				map.put(linkId, new RoadMap(first_JD,first_WD,center_JD,center_WD,end_JD,end_WD,length,width));
				Cell_OnePoint.addToMap(linkId,center_JD, center_WD);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			jdbc.close();
		}
	}

	public List<String> getRoadIDList()
	{
		return new ArrayList<String>(map.keySet());
	}
	
	String remDecimal(String str)
	{
		if(str.contains("."))
		{
			int intStr=Double.valueOf(str).intValue();
			return ""+intStr;
		}
		return str;
	}
	
	
	/**
	 * 得到近邻表
	 * 
	 * @param _lon
	 * @param _lat
	 * @return
	 */
	public List<String> nearRoadTable(double _lon, double _lat)
	{
		return Cell_OnePoint.nearTable(_lon, _lat);
	}
	

	/**
	 * 得到最近路序号
	 * 
	 * @param lon
	 * @param lat
	 * @param nearTable
	 * @return
	 */
	public String nearRoad(double lon, double lat, List<String> nearTable)
	{
		String index=StaticValue.NullStr;
		int cnt=nearTable.size();
		if(cnt>0)
		{
			double min=StaticValue.LimitDis;
			for(int i=0; i<cnt; i++)
			{
				String tmp=nearTable.get(i);
				DisPointToLine disP2L=new DisPointToLine(map.get(tmp).getFirst_lon(), map.get(tmp).getFirst_lat(), map
						.get(tmp).getEnd_lon(), map.get(tmp).getEnd_lat());
				double tmpMin=disP2L.getNearDis(lon, lat);
				if(tmpMin<min)
				{
					min=tmpMin;
					index=tmp;
				}
			}
		}
		return index;
	}


}
