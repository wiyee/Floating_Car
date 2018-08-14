package speed.traffic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import jdbc.JDBC;

/*
 * （1）几个静态方法，在运行完Traffic_Main后产生的三个表Traffic_5,Traffic_10,Traffic_15
 * 中将交通速度(speed)，交通流密度(INDATA)，路段过车量(GCL)，交通流密度(OUTDATA)分别保存在单独的表中。
 * （2）将Map中路段长度（LENGTH）取出保存在LENGTH表中。
 * （3）将NEAR_STATE_5/10/15中的LINKID取出保存到Road_Set_5/10/15
 */
public class ExtractSqlData {
	
	/*
	 * fromTable:导出表
	 * toTable:导入表
	 * keyColumn:导入列
	 * i为导入列字段类型，0为double，1为int
	 */
	public static void extractData(String fromTable,String toTable,String keyColumn,int i) {
		String sql="select " + keyColumn + " from "+ fromTable;
		String insertSql = "insert into " +toTable+ " values(?)";
		System.out.println(sql);
		System.out.println(insertSql);
		JDBC jdbc=new JDBC();
		PreparedStatement stmt = null; 
		int n=0;
		try{
			ResultSet rs=jdbc.executeQuery(sql);
			Connection conn = jdbc.getConnect();
			stmt = conn.prepareStatement(insertSql);
			while(rs.next()){
				n++;
				if (i==0) {
					double tmp = rs.getDouble(keyColumn);
					stmt.setDouble(1, tmp);
				}else {
					int tmp = rs.getInt(keyColumn);
					stmt.setInt(1, tmp);
				}
				stmt.addBatch();
//              System.out.println(speed);
				if (n%10000 == 0) {
					stmt.executeBatch();
				}
			}
			stmt.executeBatch();
		}catch(Exception e) {
			e.printStackTrace();
		}finally{
			jdbc.close();
		}
	}
	
}
