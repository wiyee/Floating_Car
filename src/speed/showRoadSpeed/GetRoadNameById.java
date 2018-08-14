package speed.showRoadSpeed;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import jdbc.JDBC;
import myTools.StaticMethod;

public class GetRoadNameById {

	
	public static void main(String[] args) {
		jdbc.JDBC jdbc = new JDBC();
		String sql = "select count(*) as A from v$process";
		ResultSet rSet = jdbc.executeQuery(sql);
		try {
			while(rSet.next()){
				int ca = rSet.getInt("A");
				System.out.println(ca);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			jdbc.close();
		}
	}
	private void write2DB(int roadId, String[] lonlat, double realSpeed,double preSpeed,int i){
		String sql="insert into speed_result_" + i + "(ROAD_ID,NAME,FIRST_JD,FIRST_WD,CENTER_JD,CENTER_WD,END_JD,END_WD,PRE_SPEED,REAL_SPEED) values"+StaticMethod.nMark(10);
		JDBC jdbc=new JDBC();
		PreparedStatement stmt=null;
		try
		{
			Connection conn=jdbc.getConnect();
			stmt=conn.prepareStatement(sql);
			// 方式2：批量提交
			conn.setAutoCommit(false);
			stmt.setInt(1, roadId);
			stmt.setString(2, lonlat[6]);
			stmt.setDouble(3, Double.parseDouble(lonlat[0]));
			stmt.setDouble(4, Double.parseDouble(lonlat[1]));
			stmt.setDouble(5, Double.parseDouble(lonlat[2]));
			stmt.setDouble(6, Double.parseDouble(lonlat[3]));
			stmt.setDouble(7, Double.parseDouble(lonlat[4]));
			stmt.setDouble(8, Double.parseDouble(lonlat[5]));
			stmt.setDouble(9, preSpeed);
			stmt.setDouble(10, realSpeed);
			stmt.addBatch();
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
	public static String[] getRoadName(int id) {
		String[] lonlat = new String[7];
		jdbc.JDBC jdbc = new JDBC();
		try {
		
		
		String sql = "select NAME,FIRST_JD,FIRST_WD,CENTER_JD,CENTER_WD,END_JD,END_WD from MAP where ID = " + id;
		ResultSet rs = jdbc.executeQuery(sql);
		while(rs.next()){
			String name = rs.getString("NAME");
			Double first_jd = rs.getDouble("FIRST_JD");
			Double first_wd = rs.getDouble("FIRST_WD");
			Double center_jd = rs.getDouble("CENTER_JD");
			Double center_wd = rs.getDouble("CENTER_WD");
			Double end_jd = rs.getDouble("END_JD");
			Double end_wd = rs.getDouble("END_WD");
			lonlat[0] = first_jd + "";
			lonlat[1] = first_wd + "";
			lonlat[2] = center_jd + "";
			lonlat[3] = center_wd + "";
			lonlat[4] = end_jd + "";
			lonlat[5] = end_wd + "";
			lonlat[6] = name;
		}
		} catch (Exception e) {
			System.out.println(e);
		} finally {
			jdbc.close();
		}
		return lonlat;
	}

}
