package dbTable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Map;

import dbTable.pojo.Road_POJO;
import jdbc.JDBC;
import myTools.StaticMethod;

public class NEAR_ROAD {
	
	public static void writeToDB(Map<String, Road_POJO> road, int tableIdx)
	{
		String tableName="NEAR_ROAD_"+tableIdx;
		long id=StaticMethod.getID(tableName);
		String insertSql = "insert into "+tableName+"(ID,LINKID,ROADS_1,WEIGHTS_1,ROADS_2,WEIGHTS_2,ROADS_3,WEIGHTS_3,ROADS_4,WEIGHTS_4，ROADS_5,WEIGHTS_5,ROADS_6,WEIGHTS_6) values"
				+StaticMethod.nMark(14);
		JDBC jdbc=new JDBC();
		PreparedStatement stmt=null;
		try
		{
			Connection conn=jdbc.getConnect();
			stmt=conn.prepareStatement(insertSql);
			// 方式2：批量提交
			conn.setAutoCommit(false);
//			conn.setAutoCommit(true);
//			int n = 0;
			for(String roadId:road.keySet()) {
				Road_POJO rp=road.get(roadId);
//				n++;
				stmt.setLong(1, ++id);
				stmt.setString(2, roadId);
				stmt.setString(3, rp.getRoad(1));
				stmt.setString(4, rp.getWeight(1));
				stmt.setString(5, rp.getRoad(2));
				stmt.setString(6, rp.getWeight(2));
				stmt.setString(7, rp.getRoad(3));
				stmt.setString(8, rp.getWeight(3));
				stmt.setString(9, rp.getRoad(4));
				stmt.setString(10, rp.getWeight(4));
				stmt.setString(11, rp.getRoad(5));
				stmt.setString(12, rp.getWeight(5));
				stmt.setString(13, rp.getRoad(6));
				stmt.setString(14, rp.getWeight(6));
				stmt.addBatch();
//				stmt.executeBatch();
//				if (n%10==0) {
//					stmt.executeBatch();
//					n=0;
//				}
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
	
}
