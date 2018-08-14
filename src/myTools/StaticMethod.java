package myTools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dbTable.pojo.Group_POJO;
import pojo.ArgOne;
import pojo.Class_Number;
import pojo.GroupList;
import pojo.NearRoad;
import pojo.NearRoadTwo;
import pojo.NearState;
import pojo.Traffic;
import result.R_NN_Forcast;
import result.R_KNN;
import speed.args.ArgsMain;
import jdbc.JDBC;

public class StaticMethod {
	
	public enum DbSystem{
    	ORACLE, MYSQL, SQLSERVER, DB2, POINTBASE
    }
	
	/**
	 * 更改数据库系统
	 */
	public static String[] setDataBase(DbSystem dbSystem, String ip, String dbName)
	{
		String driver="";
		String con="";
		switch (dbSystem) {
		case ORACLE:
			driver="oracle.jdbc.driver.OracleDriver";
			con="jdbc:oracle:thin:@"+ip+":1521:"+dbName;
			break;
			
		case MYSQL:
			driver="com.mysql.jdbc.Driver";
			con="jdbc:mysql://"+ip+":3306/"+dbName;
			break;
			
		case SQLSERVER:
			driver="com.microsoft.sqlserver.jdbc.SQLServerDriver";
			con="jdbc:microsoft:sqlserver://"+ip+":1433;DatabaseName="+dbName;
			break;
			
		case DB2:
			driver="com.ibm.db2.jcc.DB2Driver";
			con="jdbc:db2://"+ip+":5000/"+dbName;
			break;
			
		case POINTBASE:
			driver="com.pointbase.jdbc.jdbcUniversalDriver";
			con="jdbc:pointbase:server://"+ip+":9092/"+dbName;
			break;
		}
		return new String[]{driver,con};
	}
	
	public static String turnDbDate(String timeStr)
	{
		return "to_date('"+timeStr+"', 'yyyy-mm-dd hh24:mi:ss') ";
	}
	
	
	public static Double[] turnDoubleBy(double[] d)
    {
		Double[] res=new Double[d.length];
		for(int i=0;i<d.length;i++)
		{
			res[i]=new Double(d[i]);
		}
		   return res;
	 }
	
	public static void changeArr(double[][] arr)
	{
		arr[0][2]+=0.2;arr[0][3]-=0.2;
		arr[1][2]-=0.1;arr[1][3]+=0.1;
		if(arr[0][2]>0.8){arr[0][2]-=0.1;arr[0][3]+=0.1;};
		arr[2][2]-=0.1;arr[2][3]+=0.1;
		arr[3][2]-=0.1;arr[3][3]+=0.1;
	}
			
	
	public static String addString(String spit,double[] objs)
	{
    	StringBuffer res=new StringBuffer();
		for(Object obj: objs)
		{
			res.append(obj+spit);
		}
		return res.substring(0, res.length()-spit.length());
	}
	
	public static <T,V> void addToMapList(Map<T,Map<T,List<V>>> map,T key1,T key2,V v)
	{
		if(!map.containsKey(key1))
		{
			map.put(key1, new HashMap<T, List<V>>());
		}
		Map<T, List<V>> resInMap=map.get(key1);
		if(!resInMap.containsKey(key2))
		{
			resInMap.put(key2, new ArrayList<V>());
		}
		resInMap.get(key2).add(v);
	}
	
	public static <T,V> void addToMap(Map<T,Map<T,V>> map,T key1,T key2,V v)
	{
		if(!map.containsKey(key1))
		{
			map.put(key1, new HashMap<T, V>());
		}
		Map<T, V> resInMap=map.get(key1);
		resInMap.put(key2, v);
	}
	
	public static <T> String addString(String spit,List<T> objs)
	{
    	StringBuffer res=new StringBuffer();
		for(T obj: objs)
		{
			res.append(obj+spit);
		}
		return res.substring(0, res.length()-spit.length());
	}
	
    
	/**
	 * 算速度，单位：km/h
	 * @param tmpDis 单位km
	 * @param millisecond 单位ms
	 * @return
	 */
	public static double turnSpeed(double tmpDis, long millisecond)
	{
		double second=(double)millisecond/1000;
		return (tmpDis/second)*3600;      
	}
	
	/**
	 * 角度转弧度
	 * @param d
	 * @return
	 */
	public static double rad(double d)
	{
		return d*Math.PI/180.0;
	}

	/**
	 * 将值加入到历史值中，求平均
	 * @param d 历史平均值  
	 * @param tmpSpeed  当前值
	 * @param i   历史值个数
	 * @return
	 */
	public static double ave(double historyVal, double nowVal, int num)
	{
		double totalSpeed=historyVal*num+nowVal;
		return totalSpeed/(num+1);
	}
	
	
	/**
	 * 两地理坐标点的距离，单位km
	 * @param lon1
	 * @param lat1
	 * @param lon2
	 * @param lat2
	 * @return
	 */
	public static double distance(double lon1, double lat1, double lon2, double lat2)
	{
		double radLat1=rad(lat1);
		double radLat2=rad(lat2);
		double radLon1=rad(lon1);
		double radLon2=rad(lon2);
//		double a=radLat1-radLat2;
		double b=radLon1-radLon2;
//		double s=2*Math.asin(Math.sqrt(Math.pow(Math.sin(a/2), 2)+Math.cos(radLon1)*Math.cos(radLon2)
//				*Math.pow(Math.sin(b/2), 2)));
		double s=Math.acos(Math.cos(radLat1)*Math.cos(radLat2)*Math.cos(b)+Math.sin(radLat1)*Math.sin(radLat2));
		s=s*StaticValue.EARTH_RADIUS;
		return s;
	}
	
	
	
	public static double distance(BigDecimal lon1, BigDecimal lat1, BigDecimal lon2, BigDecimal lat2)
	{
		return StaticMethod.distance(lon1.doubleValue(), lat1.doubleValue(), lon2.doubleValue(), lat2.doubleValue());
	}
	
	public static String nMark(int n) {
		StringBuffer sb = new StringBuffer("(");
		for (int i = 0; i < n; i++) {
			sb.append("?,");
		}
		return sb.substring(0, sb.length() - 1) + ")";
	}
	
	public static long getID(String tableName)
	{
		String sql="select count(*) from "+tableName;
		JDBC jdbc=new JDBC();
		ResultSet rs=jdbc.executeQuery(sql);
		try
		{
			rs.next();
			return rs.getLong(1);
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try
			{
				rs.close();
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			jdbc.close();
		}
		return 0l;
	}
	
	
	public static double myRound(double number,int index){
        double result = 0;
        double temp = Math.pow(10, index);
        result = Math.round(number*temp)/temp;
        return result;
    }
	
	public static Class_Number getListFromDB(String sql){
		JDBC jdbc=new JDBC();
		List<Double> dataList=new ArrayList<Double>();
		List<Integer> valueList=new ArrayList<Integer>();
		try
		{
			ResultSet rs=jdbc.executeQuery(sql);
			while(rs.next()){
				dataList.add(rs.getDouble(1));
				valueList.add(rs.getInt(2));
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			jdbc.close();
		}
		return new Class_Number(dataList,valueList);
	}
	
	public static List<NearRoad> getNearRoadList(int i) {
		List<NearRoad> list=new ArrayList<NearRoad>();
		String sql="select ID,LINKID,ROADS_1,WEIGHTS_1,ROADS_2,WEIGHTS_2,ROADS_3,WEIGHTS_3,ROADS_4,WEIGHTS_4,ROADS_5,WEIGHTS_5,ROADS_6,WEIGHTS_6 from NEAR_ROAD_"+i+" where ROADS_1 is not null";
		JDBC jdbc=new JDBC();
		try
		{
			ResultSet rs=jdbc.executeQuery(sql);
			while(rs.next()){
				int ID=rs.getInt(1);
				int LINKID=rs.getInt(2);
				String ROADS_1=rs.getString(3);
				String WEIGHTS_1=rs.getString(4);
				String ROADS_2=rs.getString(5);
				String WEIGHTS_2=rs.getString(6);
				String ROADS_3=rs.getString(7);
				String WEIGHTS_3=rs.getString(8);
				String ROADS_4=rs.getString(9);
				String WEIGHTS_4=rs.getString(10);
				String ROADS_5=rs.getString(11);
				String WEIGHTS_5=rs.getString(12);
				String ROADS_6=rs.getString(13);
				String WEIGHTS_6=rs.getString(14);
				NearRoad nr=new NearRoad(ID, LINKID, ROADS_1, WEIGHTS_1, ROADS_2, WEIGHTS_2, ROADS_3, WEIGHTS_3, ROADS_4, WEIGHTS_4, ROADS_5, WEIGHTS_5, ROADS_6, WEIGHTS_6);
				list.add(nr);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			jdbc.close();
		}
		
		return list;
	}
	
	public static List<NearState> getNearStateList(int i) {
		List<NearState> list=new ArrayList<NearState>();
		String sql="select LINKID,WEIGHT_STATE,SYMBOL from NEAR_STATE_"+i;
		JDBC jdbc=new JDBC();
		try
		{
			ResultSet rs=jdbc.executeQuery(sql);
			while(rs.next()){
				int linkid=rs.getInt(1);
				String weightState=rs.getString(2);
				String symbol=rs.getString(3);
				NearState nr=new NearState(linkid, weightState, symbol);
				list.add(nr);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			jdbc.close();
		}
		
		return list;
	}
	
	
	public static Set<Integer> getRoadSet(int i)
	{
		Set<Integer> res=new HashSet<Integer>();
		String sql="select VALUE from ROAD_SET_"+i;
		JDBC jdbc=new JDBC();
		try
		{
			ResultSet rs=jdbc.executeQuery(sql);
			while(rs.next()){
				int roadId=rs.getInt(1);
				res.add(roadId);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			jdbc.close();
		}
		return res;
	}
	
	
	
	public static List<Integer> string2IntList(String str)
	{
		String[] strArr=str.split(",");
		List<Integer> list=new ArrayList<Integer>();
		for(String tmp:strArr)
		{
			list.add(Integer.valueOf(tmp));
		}
		return list;
	}
	
	public static List<Double> string2DoubleList(String str)
	{
		String[] strArr=str.split(",");
		List<Double> list=new ArrayList<Double>();
		for(String tmp:strArr)
		{
			list.add(Double.valueOf(tmp));
		}
		return list;
	}
	
	public static double[] string2DoubleArr(String str)
	{
		List<Double> list=string2DoubleList(str);
		double[] res=new double[list.size()];
		for(int i=0;i<list.size();i++)
		{
			res[i]=list.get(i);
		}
		return res;
	}
	
	public static double[] itA(int roadId, long fromMs, long h,
			Map<Integer, Map<Long, Traffic>> trafficMap, int nearRoadId,
			ArgOne ao, GroupList gl) {
		double[] res=new double[5];
		long nearFromMs=fromMs-h;
			double inSum=0;
				Map<Long, Traffic> tfMap=trafficMap.get(nearRoadId);
				double ts=getTs(tfMap.get(nearFromMs),0);
				List<Double> tsGroup=getTsGroup(gl,0);
				int idx=getIdx(ts, tsGroup);
				res[0]=idx;
				res[1]+=ts*ts*ts;
				res[2]+=ts*ts;
				res[3]+=ts;
				res[4]+=1;
//			res[i]+=inSum;
		return res;
	}

	public static double forcastSpeed(long fromMs, long h, Map<Long, Traffic> tfMap, ArgOne ao, GroupList gl) {
		double res=0;
		double outSum=0;
//		double[] wsArr=ns.weightState;
//		List<Integer> nearRoads=nr.getRoads();
//		List<Double> weightRoad=nr.getWeights();
		long nearFromMs=fromMs-h;
//		for(int i=0;i<wsArr.length;i++)
//		{
			double inSum=0;
//			double ws=wsArr[0];
//			double[] tsA=getTsA(ao,0);
//			for(int j=0;j<nearRoads.size();j++)
//			{
//				int nearRoadId=nearRoads.get(j);
//				double wr=weightRoad.get(j);
//				if(getTs(tfMap.get(nearFromMs),0)==0)
//				{
//					return -1;
//				}
				if(!tfMap.containsKey(nearFromMs))
				{
					return -1;
				}
				double ts=getTs(tfMap.get(nearFromMs),0);
				List<Double> tsGroup=getTsGroup(gl,0);
				int idx=getIdx(ts, tsGroup);
				//wiyee 09-08
//				System.out.println(idx);
//				System.out.println(ao.aList_speed[idx]);
				
				inSum+=ao.aList_speed[idx]*Math.pow(ts,3)+ao.bList_speed[idx]*Math.pow(ts, 2)+ao.cList_speed[idx]*ts+ao.dList_speed[idx];
//			}
			outSum+=inSum;
//		}
		res+=outSum;
//		if((res+"").equals("Infinity"))
//		{
//			System.out.println();
//		}
		return res;
	}
	
	
	private static double[] getTsA(ArgOne ao,int i)
	{
		return ao.aList_speed;
//		switch (i) {
//		case 0: return ao.aList_speed;
//		case 1: return ao.aList_gcl;
//		case 2: return ao.aList_inData;
//		default: return ao.aList_outData;
//		}
	}
	
	private static double getTs(Traffic traffic,int i)
	{
		return traffic.getSpeed();
//		switch (i) {
//		case 0: return traffic.getSpeed();
//		case 1: return traffic.getGcl();
//		case 2: return traffic.getIndata();
//		default: return traffic.getOutdata();
//		}
	}
	
	
	private static List<Double> getTsGroup(GroupList gl,int i)
	{
		switch (i) {
		case 0: return gl.speedGroup;
		case 1: return gl.gclGroup;
		case 2: return gl.inDataGroup;
		default: return gl.outDataGroup;
		}
	}
	
	public static int getIdx(double v, List<Double> vList)
	{
		int res=0;
		for(;res<vList.size();res++)
		{
			if(v<vList.get(res))
			{
				break;
			}
		}
		return res;
	}
	
	public static String nextTime(String timeStr, int next)
	 {
	    long ts = Timestamp.valueOf(timeStr).getTime();
	    ts += next;
	    return StaticValue.SDF.format(new Timestamp(ts));
	 }

	public static int getHourIdx(long ms)
	{
//		System.out.println("------------");
//		System.out.println(ms);
//		System.out.println(ms-StaticValue.startMs);
//		System.out.println(StaticValue.twoHour);
//		System.out.println((ms-StaticValue.startMs)/StaticValue.twoHour);
//		System.out.println("------------");
		return (int) ((ms-StaticValue.startMs)/StaticValue.twoHour);
	}

	public static void writeToFile2(String writeUrl, int i, List<List<Double>> listArr) {
		File file1=new File(writeUrl+i+".txt");
		OutputStreamWriter write = null;
		try{
			write = new OutputStreamWriter(new FileOutputStream(file1),"UTF-8");//考虑到编码格式
			BufferedWriter bufferedWriter = new BufferedWriter(write);
			 if (!file1.exists()) {
				    file1.createNewFile();
				   }
			 for(int j=1;j<listArr.size();j++)
			 {
				  List<Double> list=listArr.get(j);
				  bufferedWriter.write("[");
				  for(int k=0;k<list.size();k++)
				  {
					  if(k>1000)
					  {
						  break;
					  }
					  bufferedWriter.write(String.format("%.2f,", list.get(k)));
				  }
				  bufferedWriter.write("],");
				  bufferedWriter.newLine();
			 }
			 bufferedWriter.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	
	
}
