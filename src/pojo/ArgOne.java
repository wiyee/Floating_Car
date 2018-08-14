package pojo;

import myTools.StaticValue;

public class ArgOne {
	public double u;
	public double[] aList_speed;
	public double[] aList_gcl;
	public double[] aList_inData;
	public double[] aList_outData;

	public double[] bList_speed;
	public double[] bList_gcl;
	public double[] bList_inData;
	public double[] bList_outData;
	
	
	public double[] cList_speed;
	public double[] cList_gcl;
	public double[] cList_inData;
	public double[] cList_outData;
	
	
	public double[] dList_speed;
	public double[] dList_gcl;
	public double[] dList_inData;
	public double[] dList_outData;
	
	
	private static final double initV=0.1;
	
	
	
	public ArgOne(double[] aList_speed, double[] aList_gcl, double[] aList_inData, double[] aList_outData) {
		super();
		this.aList_speed = aList_speed;
		this.bList_speed = aList_gcl;
		this.cList_speed = aList_inData;
		this.dList_speed = aList_outData;
	}

	public ArgOne() {
		int groupNum=StaticValue.groupNum;
		u=initV;
		aList_speed=new double[groupNum];
		aList_gcl=new double[groupNum];
		aList_inData=new double[groupNum];
		aList_outData=new double[groupNum];
		
		bList_speed=new double[groupNum];
		bList_gcl=new double[groupNum];
		bList_inData=new double[groupNum];
		bList_outData=new double[groupNum];
		
		cList_speed=new double[groupNum];
		cList_gcl=new double[groupNum];
		cList_inData=new double[groupNum];
		cList_outData=new double[groupNum];
		
		dList_speed=new double[groupNum];
		dList_gcl=new double[groupNum];
		dList_inData=new double[groupNum];
		dList_outData=new double[groupNum];
		for(int i=0;i<groupNum;i++)
		{
			aList_speed[i]=initV;
			aList_gcl[i]=initV;
			aList_inData[i]=initV;
			aList_outData[i]=initV;
			
			bList_speed[i]=initV;
			bList_gcl[i]=initV;
			bList_inData[i]=initV;
			bList_outData[i]=initV;
			
			cList_speed[i]=initV;
			cList_gcl[i]=initV;
			cList_inData[i]=initV;
			cList_outData[i]=initV;
			
			dList_speed[i]=initV;
			dList_gcl[i]=initV;
			dList_inData[i]=initV;
			dList_outData[i]=initV;
		}
	}
	
	public String getASpeedStr(){  return getString(aList_speed);}
	
	public String getAGCLStr(){  return getString(aList_gcl);}
	
	public String getAInDataStr(){  return getString(aList_inData);}
	
	public String getAOutDataStr(){  return getString(aList_outData);}
	
	public String getBSpeedStr(){  return getString(bList_speed);}
	
	public String getBGCLStr(){  return getString(bList_gcl);}
	
	public String getBInDataStr(){  return getString(bList_inData);}
	
	public String getBOutDataStr(){  return getString(bList_outData);}
	
	
	public String getCSpeedStr(){  return getString(cList_speed);}
	
	public String getCGCLStr(){  return getString(cList_gcl);}
	
	public String getCInDataStr(){  return getString(cList_inData);}
	
	public String getCOutDataStr(){  return getString(cList_outData);}
	
	
	public String getDSpeedStr(){  return getString(dList_speed);}
	
	public String getDGCLStr(){  return getString(dList_gcl);}
	
	public String getDInDataStr(){  return getString(dList_inData);}
	
	public String getDOutDataStr(){  return getString(dList_outData);}
	
	private String getString(double[] dArr)
	{
		StringBuffer res=new StringBuffer();
		for(double obj: dArr)
		{
			String v=String.format("%.4f", obj);
			res.append(v+",");
		}
		String str=res.substring(0, res.length()-",".length());
		return str;
	}
}
