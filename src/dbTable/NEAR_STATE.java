package dbTable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

import dbTable.pojo.NearState_POJO;
import jdbc.JDBC;

public class NEAR_STATE {
	
	public static void writeToDB(List<NearState_POJO> nearRoadList, int tableIdx)
	{
		String tableName="NEAR_STATE_"+tableIdx+"_YY";
		String insertSql="insert into "+tableName+"(LINKID, WEIGHT_STATE, SYMBOL) values(?,?,?)";
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
}
