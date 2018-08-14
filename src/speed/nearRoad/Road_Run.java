package speed.nearRoad;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dbTable.NEAR_ROAD;
import dbTable.pojo.Road_POJO;
import jdbc.Input_Car;
import jdbc.Input_Road;
import myTools.StaticValue;
import myTools.TestTime;

public class Road_Run {
	private static Input_Road roadMap;
	private Input_Car inCar;
	private List<String> nearRoadList;
//	private String endTime;
	private Double findRate; // 有近邻的比例
	
	
	public static double minInterval;
	public static double maxInterval;
	
	
	public static Map<String, List<Tail>> trailMap=new HashMap<String, List<Tail>>();
	private static Map<String, Road_POJO> road=new HashMap<String, Road_POJO>();
	
	static{
		roadMap = new Input_Road();
		System.out.println("地图数据读取成功！");
	}

	public static void setInterval(int interval)
	{
		minInterval=interval*(1-StaticValue.rate);
		maxInterval=interval*(1+StaticValue.rate);
		trailMap=new HashMap<String, List<Tail>>();
		road=new HashMap<String, Road_POJO>();
		List<String> roadIdList=roadMap.getRoadIDList();
		for(String roadId:roadIdList)
		{
			road.put(roadId, new Road_POJO());
		}
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
//			if (find!=0) {
//				System.out.println(find+"----"+nearRoadList.size());
//			}
		}
		return findRate;
	}

	public Road_Run(String startTime, String _endTime) {
		TestTime tt=new TestTime();
		inCar = new Input_Car(startTime, _endTime, false);
//		gcl.add(inCar.size);
		tt.showTime("读取浮动车数据花费的时间");
		 
		nearRoadList = inCar.near_Analysis(roadMap);
//		endTime=_endTime;
	}


	public void run() {
		for (int i = 0; i < inCar.size(); i++) {
			String roadId=nearRoadList.get(i);
			String carId=inCar.getTaxinoByIdx(i);
			if(!roadId.equals(StaticValue.NullStr))
			{
				long ms=inCar.getTimeByIdx(i).getTime();
				Tail t=new Tail(roadId, ms);
				if(!trailMap.containsKey(carId))
				{
					List<Tail> list=new ArrayList<Tail>();
					list.add(t);
					trailMap.put(carId, list);
				}else
				{
					List<Tail> tList=trailMap.get(carId);
					for(int j=tList.size()-1;j>-1;j--)
					{
						Tail tmpT=tList.get(j);
						double cutMs=ms-tmpT.getT();
						if(cutMs<Road_Run.minInterval)
						{
							continue;
						}
						if(cutMs<Road_Run.maxInterval)
						{
							String roadId1=tmpT.getRoadId();
							road.get(roadId).addRoad(roadId1);
							continue;
						}
						tList.remove(j);
					}
				}
			}
		}
	}
	

	public static void end(int tableIdx) {
		NEAR_ROAD.writeToDB(road, tableIdx);
	}

}


class Tail{
	String roadId;
	long t;
	public String getRoadId() {
		return roadId;
	}
	public long getT() {
		return t;
	}
	public Tail(String roadId, long t) {
		super();
		this.roadId = roadId;
		this.t = t;
	} 
}
