package speed.showRoadSpeed;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import jdbc.JDBC;
import pojo.RoadMap;

public class GetRoadAndLatLon {

	public static Map<String, Set<String>> roadLonLatMap = new HashMap<>();
	
	public static void main(String[] args) {
		
		jdbc.JDBC jdbc = new JDBC();
		String sql = "select * from MAP";
		ResultSet rs = jdbc.executeQuery(sql);
		
		try {
			while (rs.next()) {
//				int roadID = rs.getInt("ID");
				String roadName = rs.getString("NAME");
				Double first_jd = rs.getDouble("FIRST_JD");
				Double first_wd = rs.getDouble("FIRST_WD");
				Double center_jd = rs.getDouble("CENTER_JD");
				Double center_wd = rs.getDouble("CENTER_WD");
				Double end_jd = rs.getDouble("END_JD");
				Double end_wd = rs.getDouble("END_WD");
				if (roadName != null) {
					if(roadLonLatMap.get(roadName) != null){
						Set<String> tmpSet = roadLonLatMap.get(roadName);
						tmpSet.add(first_jd + "," + first_wd);
						tmpSet.add(center_jd + "," + center_wd);
						tmpSet.add(end_jd + "," + end_wd);
						roadLonLatMap.put(roadName,tmpSet);
					}else {
						Set<String> tmpSet = new TreeSet<>();
						tmpSet.add(first_jd + "," + first_wd);
						tmpSet.add(center_jd + "," + center_wd);
						tmpSet.add(end_jd + "," + end_wd);
						roadLonLatMap.put(roadName,tmpSet);
					}
					
				}
				
			}
			System.out.println(roadLonLatMap);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			jdbc.close();
		}
	}
}
