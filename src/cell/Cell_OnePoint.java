package cell;

import java.util.ArrayList;

import myTools.StaticValue;


/**
 * 得到单个点周围的区域内容
 * @author home
 *
 */
public class Cell_OnePoint extends Cell_Super
{
	//邻近区域周围格子
    static int[][] offset={
    	{0,0},{-1,-1},{-1,0},{-1,1},{0,1},{1,1},{1,0},{1,-1},{0,-1}
    };
	
	
	/**
	 * 得到周围格子中的点编号
	 * @param lon 经度
	 * @param lat  纬度
	 * @return 邻近表
	 */
	public static ArrayList<String> nearTable(double lon,double lat)
	{
		ArrayList<String> res=new ArrayList<String>();
		for(int i=0;i<9;i++)
		{
			res.addAll(getSet(lon+offset[i][0]*StaticValue.dist,lat+offset[i][1]*StaticValue.dist));
		}
		return res;
	}

	
}
