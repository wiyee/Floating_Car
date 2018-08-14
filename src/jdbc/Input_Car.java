package jdbc;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import myTools.StaticValue;
import cell.Cell_OnePoint;

/**
 * 实时数据 流程：1、setValue从数据库中得到数据； 2、nearAnalysis做近邻分析； 3、makeSpeed计算速度等； 4、getFindRate、getUseRate分别得到："近邻发现率"、"速度利用率"
 * 
 * @author home
 *
 */
public class Input_Car
{
	private List<String> vehicle_no=new ArrayList<String>();
	private List<Timestamp> terminal_time=new ArrayList<Timestamp>();
	private List<BigDecimal> lon=new ArrayList<BigDecimal>();
	private List<BigDecimal> lat=new ArrayList<BigDecimal>();
	private List<BigDecimal> vec1=new ArrayList<BigDecimal>();
	private double findRate;     // 找到近邻数据的比例
	public int size;
	public static String TABLENAME;
	
	
	public Input_Car(String startTime, String endTime, boolean isOrder)
	{
		if(isOrder)
		{
			getAllOrder(startTime, endTime);
		}else
		{
			getAllNotOrder(startTime,endTime);
		}
	}
	
	private void getSize(String startTime, String endTime)
	{
		String sql="select count(*) from "+TABLENAME+" where "
				+" terminal_time  between to_date('"+startTime+"','yyyy-mm-dd hh24:mi:ss')  and to_date('"+endTime
				+"','yyyy-mm-dd hh24:mi:ss') and vehicle_no is not null order by vehicle_no,terminal_time";
		JDBC jdbc=new JDBC();
		try
		{
			ResultSet rs=jdbc.executeQuery(sql);
			rs.next();
			size=rs.getInt(1);
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			jdbc.close();
		}
	}
	
	
	
	private void getAllOrder(String startTime, String endTime)
	{
		String sql="select DISTINCT vehicle_no,terminal_time,lon,lat,vec1 from "+TABLENAME+" where "
				+" terminal_time  >= to_date('"+startTime+"','yyyy-mm-dd hh24:mi:ss')  and terminal_time < to_date('"+endTime
				+"','yyyy-mm-dd hh24:mi:ss') and vehicle_no is not null order by vehicle_no,terminal_time";
		getAll(sql);
		getSize(startTime, endTime);
	}
	
	private void getAllNotOrder(String startTime, String endTime)
	{
		String sql="select DISTINCT vehicle_no,terminal_time,lon,lat,vec1 from "+TABLENAME+" where "
				+" terminal_time  >= to_date('"+startTime+"','yyyy-mm-dd hh24:mi:ss')  and terminal_time < to_date('"+endTime
				+"','yyyy-mm-dd hh24:mi:ss') and vehicle_no is not null order by terminal_time";
		getAll(sql);
		getSize(startTime, endTime);
	}
	
	private void getAll(String sql)
	{
		JDBC jdbc=new JDBC();
		try
		{
			ResultSet rs=jdbc.executeQuery(sql);
			while(rs.next()){
				String vehicle_no=rs.getString(1);
				Timestamp terminal_time=rs.getTimestamp(2);
				BigDecimal lon=rs.getBigDecimal(3);
				BigDecimal lat=rs.getBigDecimal(4);
				BigDecimal vec1=rs.getBigDecimal(5);
				this.vehicle_no.add(vehicle_no);
				this.terminal_time.add(terminal_time);
				this.lon.add(lon);
				this.lat.add(lat);
				this.vec1.add(vec1);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			jdbc.close();
		}
	}
	

	public int size()
	{
		return vehicle_no.size();
	}

	public BigDecimal getLonByIdx(int idx)
	{
		return lon.get(idx);
	}

	public BigDecimal getLatByIdx(int idx)
	{
		return lat.get(idx);
	}

	public Timestamp getTimeByIdx(int idx)
	{
		return terminal_time.get(idx);
	}

	public String getTaxinoByIdx(int idx)
	{
		return vehicle_no.get(idx);
	}

	/**
	 * 得到近邻数据的比例
	 * 
	 * @return
	 */
	public double getFindRate()
	{
		return findRate;
	}

	public List<String> near_Analysis(Input_Road nearFeature)
	{
		List<String> road_id=new ArrayList<String>();
		int findNum=0;    // 找到近邻的个数
		double[][] inFeature=getINFeature();
		int inCnt=inFeature[0].length;
		for(int i=0; i<inCnt; i++)
		{
			List<String> tmpTable=Cell_OnePoint.nearTable(inFeature[0][i], inFeature[1][i]);
			String tmp=nearFeature.nearRoad(inFeature[0][i], inFeature[1][i], tmpTable);
			if(!tmp.equals(StaticValue.NullStr))
				findNum++;
			road_id.add(tmp);
		}
		findRate=(double)findNum/size();
//		System.out.println(road_id);
		return road_id;
	}

	/**
	 * 取lon和lat属性
	 * 
	 * @return
	 */
	private double[][] getINFeature()
	{
		int size=this.size();
		double[][] result=new double[2][size];
		for(int i=0; i<size; i++)
		{
			result[0][i]=lon.get(i).doubleValue();
			result[1][i]=lat.get(i).doubleValue();

		}
		return result;
	}

}
