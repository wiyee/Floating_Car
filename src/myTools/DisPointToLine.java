package myTools;

public class DisPointToLine
{ 
	private double first_lon;
	private double first_lat;
	private double end_lon;
	private double end_lat;


	/**
	 *输入直线段2坐标点
	 * @param first_lon  起始经度
	 * @param first_lat  起始纬度
	 * @param end_lon  终点经度
	 * @param end_lat   终点纬度
	 */
	 public DisPointToLine(double first_lon, double first_lat, double end_lon, double end_lat)
	{
		this.first_lon=first_lon;
		this.first_lat=first_lat;
		this.end_lon=end_lon;
		this.end_lat=end_lat;
	}
	
	
	private double verticalDist(double pointLon, double pointLat, double disFirst_End)
	{
		double[] vec1={first_lon-end_lon,first_lat-end_lat};
		double[] vec2={first_lon-pointLon,first_lat-pointLat};
		return Math.abs(cross(vec1,vec2))/disFirst_End; 
	}
	
	/**
	 * 算二维向量的外积
	 * @param vec1
	 * @param vec2
	 * @return
	 */
	private double cross(double[] vec1, double[] vec2)
	{
		return 	vec1[0]*vec2[1]-vec1[1]*vec2[0];
	}
	
	
	
	
	private  double Calculation(double first_lon, double first_lat, double end_lon, double end_lat)
	{
		double disX=first_lon-end_lon;
		double disY=first_lat-end_lat;
		return Math.sqrt(disX*disX+disY*disY);
	}
	
	
	/**
	 * 得到点到线的距离
	 * @return
	 */
	 public double getNearDis(double pointLon, double pointLat)
	{
		double disFirst_End=Calculation(first_lon,first_lat,end_lon,end_lat);
		double r=interior(pointLon,pointLat)/(disFirst_End*disFirst_End);
		if(r>0&&r<1)
		{
			return verticalDist(pointLon,pointLat,disFirst_End);
		}
		double disFirst_Sour=Calculation(first_lon,first_lat,pointLon,pointLat);
		double disEnd_Sour=Calculation(pointLon,pointLat,end_lon,end_lat);
			 return disEnd_Sour<disFirst_Sour? disEnd_Sour : disFirst_Sour;
	}
	
	
	private double interior(double pointLon, double pointLat)
	{
		double Lon=(pointLon-first_lon)*(end_lon-first_lon);
		double Lat=(pointLat-first_lat)*(end_lat-first_lat);
		return Lon+Lat;
	}

}
