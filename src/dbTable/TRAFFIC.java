package dbTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jdbc.JDBC;
import myTools.StaticMethod;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.Connection;
import java.sql.PreparedStatement;

import pojo.Traffic;
import dbTable.pojo.Traffic_POJO;

public class TRAFFIC {

	
	public static void writeToDB(Map<String, Traffic_POJO> traffic, String startTime, String endTime, int tableIdx)
	{
		String tableName="TRAFFIC_"+tableIdx;
		long id=StaticMethod.getID(tableName);
		String insertSql = "insert into "+tableName+"(ID,LINKID,SPEED,FROM_TIME,TO_TIME,GCL,WIDTH,LENGTH,INDATA,OUTDATA) values"
				+StaticMethod.nMark(10);
		JDBC jdbc=new JDBC();
		Timestamp startDate=Timestamp.valueOf(startTime);
		Timestamp endDate=Timestamp.valueOf(endTime);
		PreparedStatement stmt=null;
		try
		{
			Connection conn=jdbc.getConnect();
			stmt=conn.prepareStatement(insertSql);
			// 方式2：批量提交
			conn.setAutoCommit(false);
			int n = 0;
			for (String roadId:traffic.keySet()) {
				Traffic_POJO tp=traffic.get(roadId);
				stmt.setLong(1, ++id);
				stmt.setString(2, roadId);
				stmt.setDouble(3, tp.getSpeed());    //速度
				stmt.setTimestamp(4, startDate);
				stmt.setTimestamp(5, endDate);
				stmt.setDouble(6, tp.getGCL());
				stmt.setDouble(7, tp.getWIDTH());
				stmt.setDouble(8, tp.getLDCD());
				stmt.setDouble(9, tp.getINDATA());
				stmt.setDouble(10, tp.getOUTDATA());
				stmt.addBatch();
				n++;
				if(n%5000==0){
					stmt.executeBatch();
					n=0;
				}
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
	
	
	public static List<Traffic> getTrafficList(int i, String startTime, String endTime)
	{
		List<Traffic> trafficList=new ArrayList<Traffic>();
		String sql="select ID,LINKID,SPEED,FROM_TIME,GCL,WIDTH,LENGTH,INDATA,OUTDATA from TRAFFIC_"+i+" where LINKID in(select * from ROAD_SET_"+i+") and FROM_TIME between "+StaticMethod.turnDbDate(startTime)+" and "+ StaticMethod.turnDbDate(endTime) +" order by LINKID,FROM_TIME";
		JDBC jdbc=new JDBC();
		try
		{
			ResultSet rs=jdbc.executeQuery(sql);
			while(rs.next()){
				int id=rs.getInt(1);
				int linkId=rs.getInt(2);
				double speed=rs.getDouble(3);
				long fromMs=rs.getTimestamp(4).getTime();
				int gcl=rs.getInt(5);
				double width=rs.getDouble(6);
				double length=rs.getDouble(7);
				double indata=rs.getDouble(8);
				double outdata=rs.getDouble(9);
				Traffic t=new Traffic(id, linkId, speed, fromMs, gcl, width, length, indata, outdata);
				trafficList.add(t);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			jdbc.close();
		}
		return trafficList;
	}

}
