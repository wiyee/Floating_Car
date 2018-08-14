package dbTable.pojo;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import myTools.StaticValue;


/**
 * 路段长度、速度、时间、路段过车量
 * 
 * @author home
 *
 */
public class Traffic_POJO
{
	private Double LENGTH;     //单位（km）
	private Double SPEED;
	private Double GCL=0d;
	private Double WIDTH;
	private Double INDATA=0d;
	private Double OUTDATA=0d;
	private static String startTime;
	private static String endTime;
	private static int segment;
	

	
	private static long endMs;
	private List<ArrayList<Double>> speedList=new ArrayList<ArrayList<Double>>();    //多层速度列表
	
	
	private static Integer layerNum;        //层的个数
	
	
	public Double getWIDTH() {
		return WIDTH;
	}

	public Double getGCL() {
		return GCL;
	}

	public void addGCL() {
		GCL++;
	}

	/**
	 * 道路占有率
	 */
	public Double getINDATA() {
		return GCL/(LENGTH*WIDTH);
	}

	public void addINDATA() {
		INDATA++;
	}

	/**
	 * 交通流密度
	 */
	public Double getOUTDATA() {
		return GCL/LENGTH;
	}

	public void addOUTDATA() {
		OUTDATA++;
	}

	public static void setEndTime(String _startTime, String _endTime, int _segment)
	{
		startTime=_startTime;
		endTime=_endTime;
		endMs=Timestamp.valueOf(_endTime).getTime();
		segment=_segment;
	}
	
	public Traffic_POJO(double ldcd, double width)
	{
		LENGTH=ldcd;
		WIDTH=width;
		if(layerNum==null)
		{
			layerNum=segment/StaticValue.block;
		}
		for(int i=0; i<layerNum; i++)        
		{
			speedList.add(new ArrayList<Double>());
		}
	}
	
	
	public void addSpeed(Double spd,long time)
	{
		int listNum=getListNum(time);
		speedList.get(listNum).add(spd);
	}
	
	private int getListNum(long time)
	{
		return (int)((endMs-time)/StaticValue.block);
	}
	
	
	/**
	 * @return the lDCD
	 */
	public Double getLDCD()
	{
		return LENGTH;
	}
	/**
	 * @return the speed
	 */
	public Double getSpeed()
	{
		if(SPEED==null)
		{
			List<Double> maxValueList=new ArrayList<Double>();
			for(int i=0; i<layerNum; i++)
			{  
				List<Double> spdList=speedList.get(i);
				int listNum=spdList.size();
				if(listNum>0)
				{
					Collections.sort(spdList);    //升序排序
					maxValueList.add(spdList.get((listNum-1)));     //添加最大的速度
				}
			}
			SPEED=weighting(maxValueList);
		}
		return SPEED;
	}
	
	
	
	private Double weighting(List<Double> maxValueList)
	{
		if(maxValueList.size()==0)
		{
			return 0d;
		}
		double res=0;
		double sum=0;
		for(int i=0; i<maxValueList.size(); i++)
		{
			double exponent=1.0/(5*(i+1));
			double weight=Math.pow(exponent, StaticValue.alpha);
			res+=maxValueList.get(i)*weight;
			sum+=weight;
		}
		return res/sum;
	}

	/**
	 * @return the time  单位（秒）
	 */
	Double getTime()
	{
		return 3600*LENGTH/getSpeed();
	}

	public static String getStartTime() {
		return startTime;
	}

	public static String getEndTime() {
		return endTime;
	}
	
}
