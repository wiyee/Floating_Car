package speed.args;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.sun.corba.se.spi.orbutil.fsm.Input;

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
import pojo.Traffic;

public class ArgsMain {

	public static long h;
	public static long minFromMs;

	public Map<Integer, List<ArgOne>> argMap;
	public Map<Integer, List<Long>> useRoadMap;

	static String startTime = "2015-02-01 05:00:00";
	static String endTime = "2015-02-02 22:00:00";

	public static Map<Integer, Map<Long, Traffic>> trafficMap;
	public static GroupList gl;

	public int maxNum = 100;

	public ArgsMain() {
		Agent.gbest = new ArgOne();// aList bList cList dList
		Group_POJO group = GROUP.getGroup();
		for (int i = 5; i <= 5; i += 5) {
			h = i * 60 * 1000;
			trafficMap = getTrafficMap(i);
			System.out.println("读取traffic表");
			Set<Integer> roadIdSet = new HashSet<Integer>(trafficMap.keySet());
			// roadSet中是否包含了所有的道路
			//System.out.println(roadIdSet.size());
			
			// 路段id——
			// Map<Integer,NearState> nsMap=getNsMap(i);
			// 路段id——
			// <道路id，近邻路段>
			Map<Integer, NearRoadTwo> nrMap = getNrMap(i, roadIdSet);
			// 路段id——fromMs——

			// 路段id——
			argMap = getArgMap(nrMap);
			useRoadMap = new HashMap<Integer, List<Long>>();
			gl = new GroupList(group, i);

			int idx = 1;
			for (int roadId : nrMap.keySet()) {
				NearRoadTwo nr = nrMap.get(roadId);
				Agent.cuTfMap = trafficMap.get(roadId);
				List<ArgOne> aoList = argMap.get(roadId);
				List<Integer> roads = nr.getRoads();

				// System.out.println();
				System.out.println("i=" + i + "————" + (idx++) + "/" + maxNum);
				// System.out.println();

				if (idx > maxNum) {
					break;
				}
				for (int j = 0; j < roads.size(); j++) {
					// System.out.println();
					System.out.println("j=" + j);
					// System.out.println();
					int nearRoadId = roads.get(j);
					ArgOne ao = aoList.get(j);
					Agent.nrTfMap = trafficMap.get(nearRoadId);
					if (Agent.nrTfMap == null) {
						aoList.remove(j);
						aoList.add(j, null);
						continue;
					}
					int n = 300; // 虫子个数
					int iter = 7; // 迭代次数为：2^iter
					double bestVal = Double.MAX_VALUE;
					int it = 0;
					do {
						it++;
						if (it > 4) {
							break;
						}
						PSO pso = new PSO(n, iter);
						pso.Search();
						// System.out.println();
						System.out.println("m_dBestFitness=" + Agent.m_dBestFitness);
						// System.out.println();
						if (Agent.m_dBestFitness < bestVal) {
							bestVal = Agent.m_dBestFitness;
							Agent.copyArgOne(ao, pso.getX());
						}
					} while (bestVal > 10);
				}
			}
			// }
			// writeToUSERROAD(i);

			writeToARGS(i);
		}
	}

	private void writeToARGS(int i) {
		String delSql = "truncate table ARGS_" + i;
		JDBC jdbc = new JDBC();
		jdbc.execute(delSql);

		String sql = "insert into ARGS_" + i + "(ROADID,IDX,ALIST_SPEED,ALIST_GCL,ALIST_INDATA,ALIST_OUTDATA) values"
				+ StaticMethod.nMark(6);
		jdbc = new JDBC();
		PreparedStatement stmt = null;
		StringBuffer str = new StringBuffer();
		for (int k = 0; k < StaticValue.groupNum; k++) {
			str.append("0.1000,");
		}
		String cutStr = str.substring(0, str.length() - 1);
		try {
			Connection conn = jdbc.getConnect();
			stmt = conn.prepareStatement(sql);
			// 方式2：批量提交
			conn.setAutoCommit(false);
			for (int roadId : argMap.keySet()) {
				List<ArgOne> aoList = argMap.get(roadId);
				for (int idx = 0; idx < aoList.size(); idx++) {
					ArgOne ao = aoList.get(idx);
					if (ao != null) {
						stmt.setInt(1, roadId);
						stmt.setDouble(2, idx);
						String aStr = ao.getASpeedStr();
						if (aStr.equals(cutStr)) {
							continue;
						}
						String bStr = ao.getBSpeedStr();
						String cStr = ao.getCSpeedStr();
						String dStr = ao.getDSpeedStr();
						stmt.setString(3, aStr);
						stmt.setString(4, bStr);
						stmt.setString(5, cStr);
						stmt.setString(6, dStr);
						stmt.addBatch();
					}
				}
			}
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

	private void writeToUSERROAD(int i) {
		String sql = "insert into USERROAD_" + i + "(ROADID,FROMMS) values" + StaticMethod.nMark(2);
		JDBC jdbc = new JDBC();
		PreparedStatement stmt = null;
		try {
			Connection conn = jdbc.getConnect();
			stmt = conn.prepareStatement(sql);
			// 方式2：批量提交
			conn.setAutoCommit(false);
			for (int roadId : useRoadMap.keySet()) {
				List<Long> list = useRoadMap.get(roadId);
				for (long fromMs : list) {
					stmt.setInt(1, roadId);
					stmt.setLong(2, fromMs);
					stmt.addBatch();
				}
			}
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

	private Map<Integer, List<ArgOne>> getArgMap(Map<Integer, NearRoadTwo> nrMap) {
		Map<Integer, List<ArgOne>> res = new HashMap<Integer, List<ArgOne>>();
		for (int roadId : nrMap.keySet()) {
			List<ArgOne> list = new ArrayList<ArgOne>();
			for (int i = 0; i < nrMap.get(roadId).getRoads().size(); i++) {
				list.add(new ArgOne());
			}
			res.put(roadId, list);
		}
		return res;
	}

	private Map<Integer, Map<Long, Traffic>> getTrafficMap(int i) {
		// 获取startTime到endTime的Traffic_i表中的数据
		List<Traffic> trafficList = TRAFFIC.getTrafficList(i, startTime, endTime);

		Map<Integer, Map<Long, Traffic>> res = new HashMap<Integer, Map<Long, Traffic>>();
		Traffic cuTf = trafficList.get(0);
		// System.out.println(cuTf.getId());
		int cuLinkId = cuTf.getLinkId(); // linkedId才是道路id
		Map<Long, Traffic> inMap = new HashMap<Long, Traffic>();
		inMap.put(cuTf.getFromMs(), cuTf);// getFromMS起始时间
		res.put(cuLinkId, inMap);
		minFromMs = cuTf.getFromMs() + h;
		for (int j = 1; j < trafficList.size(); j++) {
			Traffic tmp = trafficList.get(j);
			int tmpLinkId = tmp.getLinkId();
			long fromMs = tmp.getFromMs();
			if (cuLinkId != tmpLinkId) {
				cuLinkId = tmpLinkId;
				inMap = new HashMap<Long, Traffic>();
				res.put(cuLinkId, inMap);
				inMap.put(fromMs, tmp);
				continue;
			}
			inMap.put(fromMs, tmp);
			// System.out.println(inMap);
		}
		return res;
	}

	//
	private Map<Integer, NearRoadTwo> getNrMap(int i, Set<Integer> roadIdSet) {
		Map<Integer, NearRoadTwo> res = new HashMap<Integer, NearRoadTwo>();
		List<NearRoad> nrList = StaticMethod.getNearRoadList(i);
		for (NearRoad nr : nrList) {
			int linkId = nr.LINKID;
			if (roadIdSet.contains(linkId)) {
				res.put(linkId, new NearRoadTwo(nr));
			}
		}
		return res;
	}

	public static double calculateVal(ArgOne dpos) {
		double res = 0;
		int num = 0;
		for (long fromMs : Agent.cuTfMap.keySet()) {
			double speed = Agent.cuTfMap.get(fromMs).getSpeed();
			if (speed == 0) {
				continue;
			}
			double foreSpeed = StaticMethod.forcastSpeed(fromMs, h, Agent.nrTfMap, dpos, gl);
			if (foreSpeed == -1) {
				continue;
			}
			num++;
			res += Math.abs(foreSpeed - speed);
		}
		return res / num;
	}

	public static void main(String[] args) {
		Long start_time = System.currentTimeMillis();
		new ArgsMain();
		Long end_time = System.currentTimeMillis();
		System.out.println("time:" + (end_time - start_time) / 1000f / 60f + "min");
	}

}
