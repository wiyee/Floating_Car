package speed.result;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jdbc.JDBC;
import myTools.StaticMethod;

public class RoadAdditional {
	// public static Map<String, Map<Integer, Result>> roadMap = new
	// HashMap<>();
	// public static Map<String, Map<Integer, MapInfo>> map = new HashMap<>();
	public static Set<String> roadNameSet = new HashSet<>();
	public static Set<Integer> roadIdSet = new HashSet<>();
	public static Set<Integer> idSet = new HashSet<>();

	public static void main(String[] args) {
		for (int i = 15; i <= 15; i += 5) {
			getResult(i);
			getIdSet(i);
//			System.out.println(idSet);
			Iterator<String> it = roadNameSet.iterator();
			while (it.hasNext()) {
				String str = it.next();
				getMap(i,str);
			}

		}
	}

	public static void getMap(int i,String roadName) {
		jdbc.JDBC jdbc = new JDBC();
		// List<MapInfo> list = new ArrayList<>();
		try {
			String sql = "select * from MAP_BAIDU where NAME = '" + roadName + "'";
//			System.out.println(sql);
			ResultSet rs = jdbc.executeQuery(sql);
			while (rs.next()) {
//				Map<Integer, MapInfo> argMap = new HashMap<>();
				Result result = new Result();
				result.setRoadId(rs.getInt("ID"));
				result.setRoadName(rs.getString("NAME"));
				result.setFirstJd(rs.getDouble("FIRST_JD"));
				result.setFirstWd(rs.getDouble("FIRST_WD"));
				result.setCenterJd(rs.getDouble("CENTER_JD"));
				result.setCenterWd(rs.getDouble("CENTER_WD"));
				result.setEndJd(rs.getDouble("END_JD"));
				result.setEndWd(rs.getDouble("END_WD"));
				if (!roadIdSet.contains(result.getRoadId())) {
					result.setPreSpeed(100);
					result.setRealSpeed(100);
					write2db(i, result);
				}
				// list.add(result);
			}
		} catch (Exception e) {
			System.out.println(e);
		} finally {
			jdbc.close();
		}
	}

	public static void getIdSet(int i){
		jdbc.JDBC jdbc = new JDBC();
		try {
			String sql = "SELECT LINKID from TRAFFIC_" + i + " WHERE TO_TIME = to_date('2015-02-01 00:15:00', 'yyyy-mm-dd hh24:mi:ss') AND SPEED >0";
//			System.out.println(sql);
			ResultSet rs = jdbc.executeQuery(sql);
			while (rs.next()) {
				idSet.add(rs.getInt("LINKID"));
//				System.out.println(rs.getInt("LINKID"));
			}
		} catch (Exception e) {
			System.out.println(e);
		} finally {
			jdbc.close();
		}
	}
	
	public static void getResult(int i) {
		jdbc.JDBC jdbc = new JDBC();
		try {
			String sql = "select ROAD_ID,NAME from SPEED_RESULT_" + i;
			ResultSet rs = jdbc.executeQuery(sql);
			while (rs.next()) {
				// Map<Integer, Result> roadIdMap = new HashMap<>();
				// Result result = new Result();
				// result.setRoadId();
				// result.setRoadName();
				// result.setFirstJd(rs.getDouble("FIRST_JD"));
				// result.setFirstWd(rs.getDouble("FIRST_WD"));
				// result.setCenterJd(rs.getDouble("CENTER_JD"));
				// result.setCenterWd(rs.getDouble("CENTER_WD"));
				// result.setEndJd(rs.getDouble("END_JD"));
				// result.setEndWd(rs.getDouble("END_WD"));
				// result.setPreSpeed(rs.getDouble("PRE_SPEED"));
				// result.setRealSpeed(rs.getDouble("REAL_SPEED"));
				if (rs.getString("NAME") != null) {
					roadIdSet.add(rs.getInt("ROAD_ID"));
					roadNameSet.add(rs.getString("NAME"));
				}

			}
		} catch (Exception e) {
			System.out.println(e);
		} finally {
			jdbc.close();
		}
	}

	public static void write2db(int i, Result result) {
		String sql = "insert into SPEED_RESULT_" + i
				+ "(ROAD_ID,NAME,FIRST_JD,FIRST_WD,CENTER_JD,CENTER_WD,END_JD,END_WD,PRE_SPEED,REAL_SPEED) values"
				+ StaticMethod.nMark(10);

		JDBC jdbc = new JDBC();
		PreparedStatement stmt = null;
		try {
			Connection conn = jdbc.getConnect();
			stmt = conn.prepareStatement(sql);
			// 方式2：批量提交
			conn.setAutoCommit(false);
			stmt.setInt(1, result.roadId);
			stmt.setString(2, result.getRoadName());
			stmt.setDouble(3, result.getFirstJd());
			stmt.setDouble(4, result.getFirstWd());
			stmt.setDouble(5, result.getCenterJd());
			stmt.setDouble(6, result.getCenterWd());
			stmt.setDouble(7, result.getEndJd());
			stmt.setDouble(8, result.getEndWd());
			stmt.setInt(9, result.getPreSpeed());
			stmt.setInt(10, result.getRealSpeed());
			stmt.addBatch();
			stmt.executeBatch();
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			jdbc.close();
		}
	}

}
