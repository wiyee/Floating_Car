package speed.traffic;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dbTable.TRAFFIC;
import dbTable.pojo.Traffic_POJO;
import jdbc.Input_Car;
import jdbc.Input_Road;
import myTools.StaticMethod;
import myTools.StaticValue;
import myTools.TestTime;

public class Traffic_Run {
	private static Input_Road roadMap;
	private Input_Car inCar;
	
	
	private List<String> nearRoadList;
	private String endTime;
	private Double findRate; // 有近邻的比例
	static final double speedExpection=100;
	public static List<Integer> gcl=new ArrayList<Integer>();
	
	public static Map<String, String> outMap=new HashMap<String, String>();
	
	
	private Map<String, Traffic_POJO> traffic=new HashMap<String, Traffic_POJO>();
	
	
	static{
		roadMap = new Input_Road();
		System.out.println("地图数据读取成功！");
//		System.out.println("地图数据大小："+roadMap.getRoadIDList().size());
	}

	public double getFindRate() {
		if (findRate == null) {
			double find = 0;
			for (String nearRoad : nearRoadList) {
				if (!nearRoad.equals(StaticValue.NullStr)) {
					find++;
				}
			}
			findRate = find / nearRoadList.size();
		}
		return findRate;
	}

	public Traffic_Run(String startTime, String _endTime, int segment) {
		TestTime tt=new TestTime();
		inCar = new Input_Car(startTime, _endTime, true);
//		gcl.add(inCar.size);
		tt.showTime("读取浮动车数据花费的时间");
		nearRoadList = inCar.near_Analysis(roadMap);

		endTime=_endTime;
		Traffic_POJO.setEndTime(startTime,endTime,segment);
		List<String> roadIdList=roadMap.getRoadIDList();
		for(String roadId:roadIdList){

			traffic.put(roadId, new Traffic_POJO(roadMap.getLengthByID(roadId),roadMap.getWidthByID(roadId)));
		}
	}

//	private double getSpd(int i) {
//		BigDecimal lon1 = inCar.getLonByIdx(i);
//		BigDecimal lat1 = inCar.getLatByIdx(i);
//		BigDecimal lon2 = inCar.getLonByIdx(i + 1);
//		BigDecimal lat2 = inCar.getLatByIdx(i + 1);
//		long miSecond = inCar.getTimeByIdx(i + 1).getTime()
//				- inCar.getTimeByIdx(i).getTime();
//		double dist = StaticMethod.distance(lon1, lat1, lon2, lat2);
//		return StaticMethod.turnSpeed(dist, miSecond);
//	}


	private void getRoadSpeed() {
		if (!nearRoadList.get(0).equals(StaticValue.NullStr))
		{
			String roadid=nearRoadList.get(0);
			traffic.get(roadid).addGCL();
		}
		
		for (int i = 1; i < inCar.size(); i++) {
			String roadId=nearRoadList.get(i);
			if(!roadId.equals(StaticValue.NullStr)&&!roadId.equals(nearRoadList.get(i-1)))
			{
				traffic.get(roadId).addGCL();
				if(isSameCar(i))
				{
					String roadId1=nearRoadList.get(i-1);
					if(!roadId1.equals(StaticValue.NullStr))
					{
						traffic.get(roadId1).addOUTDATA();
					}
					traffic.get(roadId).addINDATA();
				}
			}
			
			if (isSameCar(i) && findRoad(i)) {
				double currDist = countDist(i);
				long miSecond = inCar.getTimeByIdx(i).getTime()
						- inCar.getTimeByIdx(i - 1).getTime();
				if (currDist > 0.01 && miSecond > 0) {
					double curSpeed = StaticMethod.turnSpeed(currDist, miSecond);
//					long curSecond = miSecond / 1000;
					if (curSpeed < speedExpection) {
						String roadid1 = nearRoadList.get(i - 1);
						String roadid2 = nearRoadList.get(i);
						long curTime = inCar.getTimeByIdx(i).getTime();
						addToSpeed(roadid2, curSpeed, curTime); // 把速度加入到道路中
						if (!roadid1.equals(roadid2)) {

							addToSpeed(roadid1, curSpeed, curTime); // 把速度加入到道路中
						}
					}
				}
			}
		}
	}
	

	private void addToSpeed(String roadId, Double spd, long time) {
		Traffic_POJO cs = traffic.get(roadId);
		cs.addSpeed(spd, time);
	}


	private boolean findRoad(int i) {
		if (nearRoadList.get(i - 1).equals(StaticValue.NullStr)) {
			return false;
		}
		if (nearRoadList.get(i).equals(StaticValue.NullStr)) {
			return false;
		}
		return true;
	}


	private double countDist(int i) {
		BigDecimal lon1 = inCar.getLonByIdx(i - 1);
		BigDecimal lat1 = inCar.getLatByIdx(i - 1);
		BigDecimal lon2 = inCar.getLonByIdx(i);
		BigDecimal lat2 = inCar.getLatByIdx(i);
		return StaticMethod.distance(lon1, lat1, lon2, lat2);
	}

	private boolean isSameCar(int i) {
		return inCar.getTaxinoByIdx(i - 1).equals(inCar.getTaxinoByIdx(i));
	}

	public void start(String startTime, String endTime, int tableIdx) {
		getRoadSpeed();

		TRAFFIC.writeToDB(traffic,startTime,endTime,tableIdx);
	}

}
