package speed.args;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import dbTable.GROUP;
import dbTable.TRAFFIC;
import dbTable.pojo.Group_POJO;
import jdbc.JDBC;
import myTools.StaticMethod;
import myTools.StaticValue;
import pojo.ArgOne;
import pojo.GroupList;
import pojo.NearRoad;
import pojo.NearRoadTwo;
import pojo.NearState;
import pojo.Traffic;

public class UsedRoadMain {
	
//	public static final int itMax=100;
	public static long h;
	public static long minFromMs;
//	public static final double lamad=0.5;
	public Map<Integer,List<Long>> useRoadMap;
	
//	public static final int argNum=StaticValue.groupNum;
//	public static final double r_u=0.001;
//	public static final double r_aSpeed=0.0001;
//	public static final double r_aGcl=0.0001;
//	public static final double r_aInData=0.0001;
//	public static final double r_aOutData=0.0001;
	
	public static Map<Integer,Map<Long,Traffic>> trafficMap;
	public static GroupList gl;
	
	
	public UsedRoadMain() {
		for(int i=5;i<=15;i+=5){
			System.out.println("时间间隔:"+i);
			h=i*60*1000;
			trafficMap=getTrafficMap(i);
			//路段id——
			Map<Integer,NearState> nsMap=getNsMap(i);
			//路段id——
			Map<Integer,NearRoadTwo> nrMap=getNrMap(i,trafficMap.keySet());
			//路段id——fromMs——
			Set<Integer> roadIdSet=new HashSet<Integer>(nrMap.keySet());
			//路段id——
			useRoadMap=new HashMap<Integer,List<Long>>();
			int idx=1;
			for(int roadId:roadIdSet)
			{
				NearRoadTwo nr=nrMap.get(roadId);
				NearState ns=nsMap.get(roadId);
				Map<Long,Traffic> inTrafficMap=trafficMap.get(roadId);
				if(ns==null)
				{
					continue;
				}
				List<Integer> roads=nr.getRoads();
				flag1:
				for(Long ms:inTrafficMap.keySet())
				{
					long foreMs=ms-h;
					for(int j=0;j<roads.size();j++)
					{
						int nearRoadId=roads.get(j);
						Map<Long,Traffic> tmpTrafficMap=trafficMap.get(nearRoadId);
						if(tmpTrafficMap==null||!tmpTrafficMap.containsKey(foreMs)||
								tmpTrafficMap.get(foreMs).getSpeed()<0.001)
						{
							continue flag1;
						}
					}
					if(!useRoadMap.containsKey(roadId))
					{
						useRoadMap.put(roadId, new ArrayList<Long>());
					}
					useRoadMap.get(roadId).add(ms);
				}
			}
			writeToUSERROAD(i);
		}
	}
	

	private void writeToUSERROAD(int i)
	{
		String sql="insert into USERROAD_"+i+"(ROADID,FROMMS) values"+StaticMethod.nMark(2);
		JDBC jdbc=new JDBC();
		PreparedStatement stmt=null;
		try
		{
			Connection conn=jdbc.getConnect();
			stmt=conn.prepareStatement(sql);
			// 方式2：批量提交
			conn.setAutoCommit(false);
			for(int roadId:useRoadMap.keySet()) {
				List<Long> list=useRoadMap.get(roadId);
				for(long fromMs:list)
				{
					stmt.setInt(1, roadId);
					stmt.setLong(2, fromMs);
					stmt.addBatch();
				}
			}
			stmt.executeBatch();
			conn.commit();
		}catch (Exception e) {
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
	


	private Map<Integer, Map<Long, Traffic>> getTrafficMap(int i) {   
		List<Traffic> trafficList=new ArrayList<Traffic>();
		String sql="select ID,LINKID,SPEED,FROM_TIME from TRAFFIC_"+i+" where SPEED>0 and FROM_TIME between "+StaticMethod.turnDbDate(StaticValue.trainStartTime)+" and "+ StaticMethod.turnDbDate(StaticValue.trainEndTime) +" order by LINKID,FROM_TIME";
		JDBC jdbc=new JDBC();
		try
		{
			ResultSet rs=jdbc.executeQuery(sql);
			while(rs.next()){
				int id=rs.getInt(1);
				int linkId=rs.getInt(2);
				double speed=rs.getDouble(3);
				long fromMs=rs.getTimestamp(4).getTime();
				int gcl=0;
				double width=0;
				double length=0;
				double indata=0;
				double outdata=0;
				Traffic t=new Traffic(id, linkId, speed, fromMs, gcl, width, length, indata, outdata);
				trafficList.add(t);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			jdbc.close();
		}
		Map<Integer, Map<Long, Traffic>> res=new HashMap<Integer, Map<Long,Traffic>>();
		Traffic cuTf=trafficList.get(0);
		int cuLinkId=cuTf.getLinkId();
		Map<Long,Traffic> inMap=new HashMap<Long, Traffic>();
		inMap.put(cuTf.getFromMs(), cuTf);
		res.put(cuLinkId, inMap);
		minFromMs=cuTf.getFromMs()+h;
		for(int j=1;j<trafficList.size();j++)
		{
			Traffic tmp=trafficList.get(j);
			int tmpLinkId=tmp.getLinkId();
			long fromMs=tmp.getFromMs();
			if(cuLinkId!=tmpLinkId)
			{
				cuLinkId=tmpLinkId;
				inMap=new HashMap<Long, Traffic>();
				res.put(cuLinkId, inMap);
				inMap.put(fromMs, tmp);
				continue;
			}
			inMap.put(fromMs, tmp);
		}
		return res;
	}


	private Map<Integer, NearRoadTwo> getNrMap(int i, Set<Integer> roadIdSet) {
		Map<Integer,NearRoadTwo> res=new HashMap<Integer, NearRoadTwo>();
		List<NearRoad> nrList=StaticMethod.getNearRoadList(i);
		for(NearRoad nr:nrList)
		{
			int linkId=nr.LINKID;
			if(roadIdSet.contains(linkId))
			{
				res.put(linkId, new NearRoadTwo(nr));
			}
		}
		return res;
	}

	private Map<Integer,NearState> getNsMap(int i)
	{
		Map<Integer,NearState> res=new HashMap<Integer, NearState>();
		List<NearState> nsList=StaticMethod.getNearStateList(i);
		for(NearState ns:nsList)
		{
			res.put(ns.linkid, ns);
		}
		return res;
	}
	
	
	public static void main(String[] args) {
		Long start_time = System.currentTimeMillis();
		new UsedRoadMain();
		Long end_time = System.currentTimeMillis();
		System.out.println("time:"+ (end_time-start_time)/1000f/60f +"min");
	}

}

