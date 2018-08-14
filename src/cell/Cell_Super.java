package cell;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;

import myTools.StaticValue;

 class Cell_Super
{
	static Map<Double,ArrayList<String>> map=new HashMap<Double, ArrayList<String>>();     //格子中的list
	
	static int lat_Num=(int)((StaticValue.end_lat-StaticValue.begin_lat)/StaticValue.dist+1);
	
	/**
	 * 将所有点放到格子中
	 * @param lon  经度
	 * @param lat   纬度
	 */
	public static void addToMap(String id,double lon,double lat)
	{
		double idx=getCellNo(lon,lat);
		if(!map.containsKey(idx))     //有点的格子才新建list
		{
			map.put(idx, new ArrayList<String>());
		}
		map.get(idx).add(id);
	}
	
	/**
	 * 得到某个格子的点编号
	 * @param d 经度
	 * @param e
	 * @return
	 */
	protected static ArrayList<String> getSet(double lon, double lat)
	{
		double idx=getCellNo(lon, lat);
		if(map.containsKey(idx))   //格子中有没有list
		{
			return map.get(idx);      
		}
		return  new ArrayList<String>(0);//格子没有点，返回空list
	}
	
	/**
	 * 得到隐式二维数组格子编号
	 * 将纬度变成小数
	 * @param lon
	 * @param lat
	 * @return
	 */
	protected static double getCellNo(double lon, double lat)
	{
		int i=(int)((lon-StaticValue.begin_lon)/StaticValue.dist);
		int j=(int)((lat-StaticValue.begin_lat)/StaticValue.dist);
		return (double)j/lat_Num+i; 
	}
}
