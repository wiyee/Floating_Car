package speed.nearState;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dbTable.NEAR_STATE;
import dbTable.pojo.NearState_POJO;
import pojo.NearState;
import pojo.Traffic;
import myTools.StaticMethod;
import myTools.StaticValue;
import jdbc.JDBC;

public class State_Main {
	//roadId——不同时间交通状态
	private static List<List<Traffic>> trafficListArr=null;
	private static List<NearState_POJO> nearRoadList=null;
	private static Map<Integer,Clazz> clazzMap=new HashMap<Integer, Clazz>();
//	private static Set<Integer> roadSet=new HashSet<Integer>();
	private static Set<Integer> badLinkSet=new HashSet<Integer>();
//	Set<Integer> useRoadMap;
	
//	static String startTime="2015-02-02 05:00:00";
//	static String endTime="2015-02-02 22:00:00";
	
	static String startTime = "2015-02-01 00:00:00";
	static String endTime="2015-02-03 00:00:00";
	
	
	public void start() {
		
		for(int i=5;i<=5;i+=5)   //预测间隔分钟
		{
			System.out.println("*************时间间隔："+i);
//			useRoadMap=getUserRoadSet(i);
			badLinkSet=new HashSet<Integer>();
			nearRoadList=getNearRoadList(i);
//			roadSet=new HashSet<Integer>();
			clazzMap=new HashMap<Integer, Clazz>();
//			for(NR nr:nearRoadList)
//			{
//				int linkId=nr.linkId;
//				roadSet.add(linkId);
//				List<Integer> roadIdList=nr.roadIdList;
//				roadSet.addAll(roadIdList);
//			}
//			insertRoadSet(i);
			trafficListArr=getTrafficList(i);
			for(List<Traffic> trafficList:trafficListArr)
			{
//				double maxSpeed=-Double.MAX_VALUE;
//				double maxGcl=-Double.MAX_VALUE;
//				double maxIndata=-Double.MAX_VALUE;
//				double maxOutdata=-Double.MAX_VALUE;
				double aveSpeed=0;
				double aveGcl=0;
				double aveIndata=0;
				double aveOutdata=0;
				
				double sumSpeed=0;
				double sumGcl=0;
				double sumIndata=0;
				double sumOutdata=0;
				for(Traffic tf:trafficList)
				{
					if(tf.getGcl()!=0)
					{
						aveSpeed+=tf.getSpeed();
						aveGcl+=tf.getGcl();
						aveIndata+=tf.getIndata();
						aveOutdata+=tf.getOutdata();
						
						sumSpeed++;
						sumGcl++;
						sumIndata++;
						sumOutdata++;
					}
//					maxSpeed=Math.max(maxSpeed, tf.getSpeed());
//					maxGcl=Math.max(maxGcl, tf.getGcl());
//					maxIndata=Math.max(maxIndata, tf.getIndata());
//					maxOutdata=Math.max(maxOutdata, tf.getOutdata());
				}
				aveSpeed/=sumSpeed;
				aveGcl/=sumGcl;
				aveIndata/=sumIndata;
				aveOutdata/=sumOutdata;
				for(Traffic tf:trafficList)
				{
					int linkId=tf.getLinkId();
					if(!clazzMap.containsKey(linkId))
					{
						clazzMap.put(linkId, new Clazz());
					}
					Clazz clazz=clazzMap.get(linkId);
					double speed=0;
					double gcl=0;
					double indata=0;
					double outdata=0;
					if(tf.getGcl()!=0)
					{
						speed=1/(1+Math.exp(-(tf.getSpeed()-aveSpeed)));
						gcl=1/(1+Math.exp(-(tf.getGcl()-aveGcl)));
						indata=1/(1+Math.exp(-(tf.getIndata()-aveIndata)));
						outdata=1/(1+Math.exp(-(tf.getOutdata()-aveOutdata)));
					}
					clazz.speedList.add(speed);
					clazz.gclList.add(gcl);
					clazz.indataList.add(indata);
					clazz.outdataList.add(outdata);
				}
			}
			System.out.println(i+"中间完成");
			System.out.println("坏数据："+badLinkSet.size()+"/"+trafficListArr.size());
			for(int e=nearRoadList.size()-1;e>-1;e--)
			{
				NearState_POJO nr=nearRoadList.get(e);
				int linkId=nr.linkId;
				List<Integer> roadIdList=nr.roadIdList;

				if(!clazzMap.containsKey(linkId))
				{
					nearRoadList.remove(e);
					continue;
				}
				
				List<Double> cuSpeedList=clazzMap.get(linkId).speedList;
				List<List<List<Double>>> diffLists=nr.diff;
				List<List<List<Double>>> neDiffLists=nr.neDiff;
				boolean isDel=true;
				for(int k=0;k<roadIdList.size();k++)
				{
					int roadId=roadIdList.get(k);
					Clazz clazz=clazzMap.get(roadId);
					if(clazz!=null&&!badLinkSet.contains(roadId))
					{
						List<Double> speedList=clazz.speedList;
						List<Double> gclList=clazz.gclList;
//						List<Double> widthList=clazz.widthList;
//						List<Double> lengthList=clazz.lengthList;
						List<Double> indataList=clazz.indataList;
						List<Double> outdataList=clazz.outdataList;
						for(int j=0;j<Math.min(cuSpeedList.size(),speedList.size());j++)
						{
							double cuSpeed=cuSpeedList.get(j);
							if(cuSpeed!=0&&speedList.get(j)!=0)
							{
								isDel=false;
								double speed=speedList.get(j);
								double gcl=gclList.get(j);
//								double width=widthList.get(j);
//								double length=lengthList.get(j);
								double indata=indataList.get(j);
								double outdata=outdataList.get(j);
								diffLists.get(0).get(k).add(Math.pow(1-Math.abs(cuSpeed-speed),2));
								diffLists.get(1).get(k).add(Math.pow(1-Math.abs(cuSpeed-gcl),2));
//								diffLists.get(2).get(k).add(Math.pow(1-Math.abs(cuSpeed-width),2));
//								diffLists.get(3).get(k).add(Math.pow(1-Math.abs(cuSpeed-length),2));
								diffLists.get(2).get(k).add(Math.pow(1-Math.abs(cuSpeed-indata),2));
								diffLists.get(3).get(k).add(Math.pow(1-Math.abs(cuSpeed-outdata),2));
								neDiffLists.get(0).get(k).add(Math.pow(1-Math.abs(cuSpeed-(1-speed)),2));
								neDiffLists.get(1).get(k).add(Math.pow(1-Math.abs(cuSpeed-(1-gcl)),2));
//								neDiffLists.get(2).get(k).add(Math.pow(1-Math.abs(cuSpeed-(1-width)),2));
//								neDiffLists.get(3).get(k).add(Math.pow(1-Math.abs(cuSpeed-(1-length)),2));
								neDiffLists.get(2).get(k).add(Math.pow(1-Math.abs(cuSpeed-(1-indata)),2));
								neDiffLists.get(3).get(k).add(Math.pow(1-Math.abs(cuSpeed-(1-outdata)),2));
							}
						}
					}
				}
				if(isDel){
					nearRoadList.remove(e);
					continue;
				}
				nr.finish();
			}
			System.out.println(i+"插入");
			NEAR_STATE.writeToDB(nearRoadList, i);
		}
	}
	
//	private void insertRoadSet(int i)
//	{
//		String insertSql="insert into ROAD_SET_"+i+" values(?)";
//		JDBC jdbc=new JDBC();
//		PreparedStatement stmt=null;
//		try{
//			Connection conn=jdbc.getConnect();
//			stmt=conn.prepareStatement(insertSql);
//			// 方式2：批量提交
//			conn.setAutoCommit(false);
//			for(int roadId:roadSet)
//			{
//				stmt.setInt(1, roadId);
//				stmt.addBatch();
//			}
//			stmt.executeBatch();
//			conn.commit();
//		}catch(Exception e)
//		{
//			e.printStackTrace();
//		}finally{
//			if(stmt!=null)
//			{
//				try
//				{
//					stmt.close();
//				}catch(Exception e)
//				{
//					e.printStackTrace();
//				}
//			}
//			jdbc.close();
//		}
//	}
	
	private Set<Integer> getUserRoadSet(int i) {
		Set<Integer> res=new HashSet<Integer>();
		String sql="select distinct(ROADID) from USEROAD_"+i;
		JDBC jdbc=new JDBC();
		try
		{
			ResultSet rs=jdbc.executeQuery(sql);
			while(rs.next()){
				res.add(rs.getInt(1));
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			jdbc.close();
		}
		return res;
	}
	
	private void insertNearState(int i) {
		System.out.println(i+"插入");
		String insertSql="insert into NEAR_STATE_"+i+"(LINKID, WEIGHT_STATE, SYMBOL) values(?,?,?)";
		JDBC jdbc=new JDBC();
		PreparedStatement stmt=null;
		try{
			Connection conn=jdbc.getConnect();
			stmt=conn.prepareStatement(insertSql);
			// 方式2：批量提交
			conn.setAutoCommit(false);
			for(NearState_POJO nr:nearRoadList)
			{
				stmt.setInt(1, nr.linkId);
				stmt.setString(2, nr.getWeightState());
				stmt.setString(3, nr.getSymbol());
				stmt.addBatch();
			}
			stmt.executeBatch();
			conn.commit();
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			if(stmt!=null)
			{
				try
				{
					stmt.close();
				}catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			jdbc.close();
		}
		
	}


	private List<NearState_POJO> getNearRoadList(int i) {
		List<NearState_POJO> list=new ArrayList<NearState_POJO>();
		String sql="select ID,LINKID,ROADS_5,WEIGHTS_5 from NEAR_ROAD_"+i+" where ROADS_3 is not null";
		JDBC jdbc=new JDBC();
		try
		{
			ResultSet rs=jdbc.executeQuery(sql);
			while(rs.next()){
				int id=rs.getInt(1);
				int linkId=rs.getInt(2);
				String roads_5=rs.getString(3);
				String weights_5=rs.getString(4);
				NearState_POJO nr=new NearState_POJO(id, linkId, roads_5, weights_5);
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


	private List<List<Traffic>> getTrafficList(int i) {
		Set<Integer> roadSet=StaticMethod.getRoadSet(i);
		String sql="select ID,LINKID,SPEED,FROM_TIME,GCL,WIDTH,LENGTH,INDATA,OUTDATA from TRAFFIC_"+i+" where FROM_TIME between "+StaticMethod.turnDbDate(startTime)+" and "+ StaticMethod.turnDbDate(endTime) +" order by FROM_TIME,LINKID";
		JDBC jdbc=new JDBC();
		List<List<Traffic>> trafficListArr=new ArrayList<List<Traffic>>();
		try
		{
			ResultSet rs=jdbc.executeQuery(sql);
			long fromMs=-1;
			List<Traffic> cuTraffic=null;
			
			while(rs.next()){
				int linkId=rs.getInt(2);
				if(roadSet.contains(linkId))
				{
					int id=rs.getInt(1);
					double speed=rs.getDouble(3);
					long cuFromMs=rs.getTimestamp(4).getTime();
					int gcl=rs.getInt(5);
					double width=rs.getDouble(6);
					double length=rs.getDouble(7);
					double indata=rs.getDouble(8);
					double outdata=rs.getDouble(9);
					Traffic t=new Traffic(id, linkId, speed, cuFromMs, gcl, width, length, indata, outdata);
					if(cuFromMs!=fromMs)
					{
						fromMs=cuFromMs;
						cuTraffic=new ArrayList<Traffic>();
						cuTraffic.add(t);
						trafficListArr.add(cuTraffic);
					}else
					{
						cuTraffic.add(t);
					}
				}
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			jdbc.close();
		}
		return trafficListArr;
	}
	
	public static void main(String[] args) {
		long sTime = System.currentTimeMillis();
		new State_Main().start();
		long eTime = System.currentTimeMillis();
		System.out.println("Time: " + (eTime-sTime)/1000f/60f + "min");
	}
}


class Clazz{
	public List<Double> speedList=new ArrayList<Double>();
	public List<Double> gclList=new ArrayList<Double>();
	public List<Double> widthList=new ArrayList<Double>();
	public List<Double> lengthList=new ArrayList<Double>();
	public List<Double> indataList=new ArrayList<Double>();
	public List<Double> outdataList=new ArrayList<Double>();
}

