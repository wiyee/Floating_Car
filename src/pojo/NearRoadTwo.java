package pojo;

import java.util.List;

import myTools.StaticMethod;
import myTools.StaticValue;

public class NearRoadTwo {
	public int ID;
	public int LINKID;
	public List<Integer> ROADS_1;
	public List<Double> WEIGHTS_1;
	public List<Integer> ROADS_2;
	public List<Double> WEIGHTS_2;
	public List<Integer> ROADS_3;
	public List<Double> WEIGHTS_3;
	public List<Integer> ROADS_4;
	public List<Double> WEIGHTS_4;
	public List<Integer> ROADS_5;
	public List<Double> WEIGHTS_5;
	public List<Integer> ROADS_6;
	public List<Double> WEIGHTS_6;
	
	public NearRoadTwo(NearRoad nr) {
		ID=nr.ID;
		LINKID=nr.LINKID;
		ROADS_1 = StaticMethod.string2IntList(nr.ROADS_1);
		WEIGHTS_1 = StaticMethod.string2DoubleList(nr.WEIGHTS_1);
		ROADS_2 = StaticMethod.string2IntList(nr.ROADS_2);
		WEIGHTS_2 = StaticMethod.string2DoubleList(nr.WEIGHTS_2);
		ROADS_3 = StaticMethod.string2IntList(nr.ROADS_3);
		WEIGHTS_3 = StaticMethod.string2DoubleList(nr.WEIGHTS_3);
		ROADS_4 = StaticMethod.string2IntList(nr.ROADS_4);
		WEIGHTS_4 = StaticMethod.string2DoubleList(nr.WEIGHTS_4);
		ROADS_5 = StaticMethod.string2IntList(nr.ROADS_5);
		WEIGHTS_5 = StaticMethod.string2DoubleList(nr.WEIGHTS_5);
		ROADS_6 = StaticMethod.string2IntList(nr.ROADS_6);
		WEIGHTS_6 = StaticMethod.string2DoubleList(nr.WEIGHTS_6);
	}
	
	public List<Integer> getRoads()
	{
		switch (StaticValue.roadNum) {
		case 1: return ROADS_1;
		case 2: return ROADS_2;
		case 3: return ROADS_3;
		case 4: return ROADS_4;
		case 5: return ROADS_5;
		default: return ROADS_6;
		}
	}
	
	public List<Double> getWeights()
	{
		switch (StaticValue.roadNum) {
		case 1: return WEIGHTS_1;
		case 2: return WEIGHTS_2;
		case 3: return WEIGHTS_3;
		case 4: return WEIGHTS_4;
		case 5: return WEIGHTS_5;
		default: return WEIGHTS_6;
		}
	}
}
