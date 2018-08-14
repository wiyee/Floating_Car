package dbTable.pojo;

import java.util.ArrayList;
import java.util.List;

import myTools.StaticMethod;

public class Group_POJO {
	private List<Double> GCL_5;
	private List<Double> INDATA_5;
	private List<Double> OUTDATA_5;
	private List<Double> GCL_10;
	private List<Double> INDATA_10;
	private List<Double> OUTDATA_10;
	private List<Double> GCL_15;
	private List<Double> INDATA_15;
	private List<Double> OUTDATA_15;
	private List<Double> SPEED_5;
	private List<Double> SPEED_10;
	private List<Double> SPEED_15;
	
	public Group_POJO(String gCL_5, String iNDATA_5, String oUTDATA_5,
			String gCL_10, String iNDATA_10, String oUTDATA_10, String gCL_15,
			String iNDATA_15, String oUTDATA_15, String sPEED_5,
			String sPEED_10, String sPEED_15) {
		super();
		GCL_5 = StaticMethod.string2DoubleList(gCL_5);
		INDATA_5 = StaticMethod.string2DoubleList(iNDATA_5);
		OUTDATA_5 = StaticMethod.string2DoubleList(oUTDATA_5);
		GCL_10 = StaticMethod.string2DoubleList(gCL_10);
		INDATA_10 = StaticMethod.string2DoubleList(iNDATA_10);
		OUTDATA_10 = StaticMethod.string2DoubleList(oUTDATA_10);
		GCL_15 = StaticMethod.string2DoubleList(gCL_15);
		INDATA_15 = StaticMethod.string2DoubleList(iNDATA_15);
		OUTDATA_15 = StaticMethod.string2DoubleList(oUTDATA_15);
		SPEED_5 = StaticMethod.string2DoubleList(sPEED_5);
		SPEED_10 = StaticMethod.string2DoubleList(sPEED_10);
		SPEED_15 = StaticMethod.string2DoubleList(sPEED_15);
//		System.out.println(gCL_5+"---"+iNDATA_5);
	}
	
	public List<Double> getGCL(int i)
	{
		switch(i)
		{
			case 5: return GCL_5;
			case 10: return GCL_10;
			default: return GCL_15;
		}
	}
	
	public List<Double> getInData(int i)
	{
		switch(i)
		{
			case 5: return INDATA_5;
			case 10: return INDATA_10;
			default: return INDATA_15;
		}
	}
	
	public List<Double> getOutData(int i)
	{
		switch(i)
		{
			case 5: return OUTDATA_5;
			case 10: return OUTDATA_10;
			default: return OUTDATA_15;
		}
	}
	
	public List<Double> getSpeed(int i)
	{
		switch(i)
		{
			case 5: return SPEED_5;
			case 10: return SPEED_10;
			default: return SPEED_15;
		}
	}
	
}
