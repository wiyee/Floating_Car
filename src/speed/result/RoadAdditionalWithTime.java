package speed.result;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import jdbc.JDBC;
import myTools.StaticMethod;

/*
 *所有时刻的道路速度补充。
 */
public class RoadAdditionalWithTime {
	// public static Map<String, Map<Integer, Result>> roadMap = new
	// HashMap<>();
	// public static Map<String, Map<Integer, MapInfo>> map = new HashMap<>();
	public static Set<String> roadNameSet = new HashSet<>();// 保存已有数据的道路名
	public static Set<Integer> roadIdSet = new HashSet<>();// 保存已有数据的道路id
	public static Set<Integer> noNameRoadIdSet = new HashSet<>();// 保存没有名字的道路id
	private static String startTime = "2015-02-03 07:00:00";
	private static final String endTime = "2015-02-03 22:00:00";
	
	
	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		for (int i = 10; i <= 10; i += 5) {

			while(Timestamp.valueOf(startTime).getTime()<=Timestamp.valueOf(endTime).getTime()){
				// 先将roadIdSeth和roadNameSet中的上一时段数据清空
				roadNameSet.clear();
				roadIdSet.clear();
				getResult(i,startTime);
				Iterator<String> it = roadNameSet.iterator();
				while (it.hasNext()) {
					String str = it.next();
					getMap(i, str);
				}
				roadComplement(i);
				System.out.println("已完成时间：" + startTime);
				startTime = StaticMethod.nextTime(startTime, i * 60 * 1000);
			}
		}
		long end = System.currentTimeMillis();
		System.out.println("total time:" + (end - start) / 1000f + " s");
	}

	public static void getMap(int i, String roadName) {
		jdbc.JDBC jdbc = new JDBC();
		// List<MapInfo> list = new ArrayList<>();
		try {
			String sql = "select * from MAP_BAIDU where NAME = '" + roadName
					+ "' and CENTER_JD between 120.677138 and 120.724138 and CENTER_WD between 27.989368 and 28.004864";
			// System.out.println(sql);
			ResultSet rs = jdbc.executeQuery(sql);
			while (rs.next()) {
				// Map<Integer, MapInfo> argMap = new HashMap<>();
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
					int[] a = getSpeed(5);					
					result.setPreSpeed(a[0]);
					result.setRealSpeed(a[1]);
					roadIdSet.add(result.getRoadId());
					write2db(i, result);
				}

			}
		} catch (Exception e) {
			System.out.println(e);
		} finally {
			jdbc.close();
		}
	}

	// 找出已经有数据的道路，不用补充数据。
	public static void getResult(int i,String time) {

		jdbc.JDBC jdbc = new JDBC();
		try {
			String sql = "select ROAD_ID,NAME from SPEED_RESULT_ALL_" + i + " where FROM_TIME = " + StaticMethod.turnDbDate(time);
			ResultSet rs = jdbc.executeQuery(sql);
			while (rs.next()) {
				int id = rs.getInt("ROAD_ID");
				if (rs.getString("NAME") != null) {
					roadIdSet.add(id);
					roadNameSet.add(rs.getString("NAME"));
				}
				else {
					roadIdSet.add(id);
				}
			}
		} catch (Exception e) {
			System.out.println(e);
		} finally {
			jdbc.close();
		}
	}

	public static void write2db(int i, Result result) {
		String sql = "insert into SPEED_RESULT_ALL_" + i
				+ "(ROAD_ID,NAME,FIRST_JD,FIRST_WD,CENTER_JD,CENTER_WD,END_JD,END_WD,PRE_SPEED,REAL_SPEED,FROM_TIME) values"
				+ StaticMethod.nMark(11);
		Timestamp startDate=Timestamp.valueOf(startTime);
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
			stmt.setTimestamp(11, startDate);
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

	public static int[] getSpeed(int n){
		int[] num = new int[2];
		Random random = new Random();
		int i = random.nextInt(100);
		if (i <= n){
			int j = random.nextInt(20);
			num[0]=j+30;
			int k = random.nextInt(26);
			num[1] = num[0] + k - 13;
			return num;
		}
		num[0]=100;
		num[1]=100;
		return num;
	}
	
	//补充name为null的路段
	public static void roadComplement(int i) {
		jdbc.JDBC jdbc = new JDBC();
		try {
			String sql = "select * from MAP_BAIDU where NAME is null and CENTER_JD between 120.677138 and 120.724138 and CENTER_WD between 27.989368 and 28.004864";
			ResultSet rs = jdbc.executeQuery(sql);
			while (rs.next()) {

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
			}
		} catch (Exception e) {
			System.out.println(e);
		} finally {
			jdbc.close();
		}
	}

}
