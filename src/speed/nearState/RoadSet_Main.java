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

public class RoadSet_Main {
	//roadId——不同时间交通状态
	private static List<NearState_POJO> nearRoadList=null;
	private static Set<Integer> roadSet=new HashSet<Integer>();
	
//	static String startTime="2015-02-02 05:00:00";
//	static String endTime="2015-02-02 22:00:00";
	
	public void start() {
		
		for(int i=5;i<=15;i+=5)   //预测间隔分钟
		{
			System.out.println("*************时间间隔："+i);
			nearRoadList=getNearRoadList(i);
			roadSet=new HashSet<Integer>();
			for(NearState_POJO nr:nearRoadList)
			{
				int linkId=nr.linkId;
				roadSet.add(linkId);
				List<Integer> roadIdList=nr.roadIdList;
				roadSet.addAll(roadIdList);
			}
			insertRoadSet(i);
			
		}
	}
	
	private void insertRoadSet(int i)
	{
		String insertSql="insert into ROAD_SET_"+i+" values(?)";
		JDBC jdbc=new JDBC();
		PreparedStatement stmt=null;
		try{
			Connection conn=jdbc.getConnect();
			stmt=conn.prepareStatement(insertSql);
			// 方式2：批量提交
			conn.setAutoCommit(false);
			for(int roadId:roadSet)
			{
				stmt.setInt(1, roadId);
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

	
	public static void main(String[] args) {
		long sTime = System.currentTimeMillis();
		new RoadSet_Main().start();
		long eTime = System.currentTimeMillis();
		System.out.println("Time: " + (eTime-sTime)/1000f/60f + "min");
	}
}


