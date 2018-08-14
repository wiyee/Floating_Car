package pojo;

public class RoadMap
{
	Double first_lon;
	Double first_lat;
	Double center_lon;
	Double center_lat;
	Double end_lon;
	Double end_lat;
	Double length;
	Double width;
	
	Double disFirst_En;
	
	public RoadMap(Double first_lon, Double first_lat, Double center_lon,
			Double center_lat, Double end_lon, Double end_lat, Double length,
			Double width) {
		super();
		this.first_lon = first_lon;
		this.first_lat = first_lat;
		this.center_lon = center_lon;
		this.center_lat = center_lat;
		this.end_lon = end_lon;
		this.end_lat = end_lat;
		this.length = length;
		this.width = width;
	}
	


	public Double getLength() {
		return length;
	}




	public Double getWidth() {
		return width;
	}




	/**
	 * 计算路线段距离  (坐标距离)
	 * @return
	 */
	private void disF_E()
	{
		double disLon=first_lon-end_lon;
		double disLat=first_lat-end_lat;
		 disFirst_En=Math.sqrt(disLon*disLon+disLat*disLat);
	}
	
	
	/**
	 * @return the disFirst_En
	 */
	public Double getDisFirst_En()
	{
		return disFirst_En;
	}



	/**
	 * @return the first_lon
	 */
	public Double getFirst_lon()
	{
		return first_lon;
	}
	/**
	 * @param first_lon the first_lon to set
	 */
	public void setFirst_lon(Double first_lon)
	{
		this.first_lon=first_lon;
	}
	/**
	 * @return the first_lat
	 */
	public Double getFirst_lat()
	{
		return first_lat;
	}
	/**
	 * @param first_lat the first_lat to set
	 */
	public void setFirst_lat(Double first_lat)
	{
		this.first_lat=first_lat;
	}
	/**
	 * @return the center_lon
	 */
	public Double getCenter_lon()
	{
		return center_lon;
	}
	/**
	 * @param center_lon the center_lon to set
	 */
	public void setCenter_lon(Double center_lon)
	{
		this.center_lon=center_lon;
	}
	/**
	 * @return the center_lat
	 */
	public Double getCenter_lat()
	{
		return center_lat;
	}
	/**
	 * @param center_lat the center_lat to set
	 */
	public void setCenter_lat(Double center_lat)
	{
		this.center_lat=center_lat;
	}
	/**
	 * @return the end_lon
	 */
	public Double getEnd_lon()
	{
		return end_lon;
	}
	/**
	 * @param end_lon the end_lon to set
	 */
	public void setEnd_lon(Double end_lon)
	{
		this.end_lon=end_lon;
	}
	/**
	 * @return the end_lat
	 */
	public Double getEnd_lat()
	{
		return end_lat;
	}
	/**
	 * @param end_lat the end_lat to set
	 */
	public void setEnd_lat(Double end_lat)
	{
		this.end_lat=end_lat;
		disF_E();
	}
	
	

}
