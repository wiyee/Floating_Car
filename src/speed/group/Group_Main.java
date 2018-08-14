package speed.group;

import java.util.List;

import pojo.Class_Number;
import myTools.Jenks;
import myTools.StaticMethod;
import myTools.StaticValue;
import jdbc.JDBC;

public class Group_Main {
	
	static Class_Number lengths=getLengths();
	//length表中的数据需要自行导入，从map中
	static Class_Number speed5=getSpeed5();
	static Class_Number speed10=getSpeed10();
	static Class_Number speed15=getSpeed15();
	static Class_Number gcls5=getGcls5();
	static Class_Number gcls10=getGcls10();
	static Class_Number gcls15=getGcls15();
	static Class_Number indatas5=getIndatas5();
	static Class_Number indatas10=getIndatas10();
	static Class_Number indatas15=getIndatas15();
	static Class_Number outdatas5=getOutdatas5();
	static Class_Number outdatas10=getOutdatas10();
	static Class_Number outdatas15=getOutdatas15();
	static int maxClassNum=StaticValue.maxClassNum;
	
	@SuppressWarnings("unchecked")
	public void start() {
		System.out.println("********全部数据读取完毕！");
		Jenks jenks_Length=new Jenks(lengths.getDataList(), lengths.getValueList(), maxClassNum);
		
		Jenks jenks_Speed5=new Jenks(speed5.getDataList(), speed5.getValueList(), maxClassNum);
		Jenks jenks_Speed10=new Jenks(speed10.getDataList(), speed10.getValueList(), maxClassNum);
		Jenks jenks_Speed15=new Jenks(speed15.getDataList(), speed15.getValueList(), maxClassNum);
		Jenks jenks_Gcl5=new Jenks(gcls5.getDataList(), gcls5.getValueList(), maxClassNum);
		Jenks jenks_Gcl10=new Jenks(gcls10.getDataList(), gcls10.getValueList(), maxClassNum);
		Jenks jenks_Gcl15=new Jenks(gcls15.getDataList(), gcls15.getValueList(), maxClassNum);
		Jenks jenks_Indata5=new Jenks(indatas5.getDataList(), indatas5.getValueList(), maxClassNum);
		Jenks jenks_Indata10=new Jenks(indatas10.getDataList(), indatas10.getValueList(), maxClassNum);
		Jenks jenks_Indata15=new Jenks(indatas15.getDataList(), indatas15.getValueList(), maxClassNum);
		Jenks jenks_Outdata5=new Jenks(outdatas5.getDataList(), outdatas5.getValueList(), maxClassNum);
		Jenks jenks_Outdata10=new Jenks(outdatas10.getDataList(), outdatas10.getValueList(), maxClassNum);
		Jenks jenks_Outdata15=new Jenks(outdatas15.getDataList(), outdatas15.getValueList(), maxClassNum);
		
		for(int classNum=2;classNum<=maxClassNum;classNum++)
		{
			System.out.println("classNum:"+classNum);
			//lengths
			Object[] objs_Length=jenks_Length.getJenks_Our(classNum);
			List<Double> k_Length=(List<Double>) objs_Length[0];
			String kStr_Length=StaticMethod.addString(",", k_Length);
			Double v_Length=(Double) objs_Length[1];
			
			//speed5
			Object[] objs_Speed5=jenks_Speed5.getJenks_Our(classNum);
			List<Double> k_Speed5=(List<Double>) objs_Speed5[0];
			String kStr_Speed5=StaticMethod.addString(",", k_Speed5);
			Double v_Speed5=(Double) objs_Speed5[1];
			
			//speed10
			Object[] objs_Speed10=jenks_Speed10.getJenks_Our(classNum);
			List<Double> k_Speed10=(List<Double>) objs_Speed10[0];
			String kStr_Speed10=StaticMethod.addString(",", k_Speed10);
			Double v_Speed10=(Double) objs_Speed10[1];
			
			//gcls15
			Object[] objs_Speed15=jenks_Speed15.getJenks_Our(classNum);
			List<Double> k_Speed15=(List<Double>) objs_Speed15[0];
			String kStr_Speed15=StaticMethod.addString(",", k_Speed15);
			Double v_Speed15=(Double) objs_Speed15[1];
			
			//gcls5
			Object[] objs_Gcl5=jenks_Gcl5.getJenks_Our(classNum);
			List<Double> k_Gcl5=(List<Double>) objs_Gcl5[0];
			String kStr_Gcl5=StaticMethod.addString(",", k_Gcl5);
			Double v_Gcl5=(Double) objs_Gcl5[1];
			
			//gcls10
			Object[] objs_Gcl10=jenks_Gcl10.getJenks_Our(classNum);
			List<Double> k_Gcl10=(List<Double>) objs_Gcl10[0];
			String kStr_Gcl10=StaticMethod.addString(",", k_Gcl10);
			Double v_Gcl10=(Double) objs_Gcl10[1];
			
			//gcls15
			Object[] objs_Gcl15=jenks_Gcl15.getJenks_Our(classNum);
			List<Double> k_Gcl15=(List<Double>) objs_Gcl15[0];
			String kStr_Gcl15=StaticMethod.addString(",", k_Gcl15);
			Double v_Gcl15=(Double) objs_Gcl15[1];
			
			//indatas5
			Object[] objs_Indata5=jenks_Indata5.getJenks_Our(classNum);
			List<Double> k_Indata5=(List<Double>) objs_Indata5[0];
			String kStr_Indata5=StaticMethod.addString(",", k_Indata5);
			Double v_Indata5=(Double) objs_Indata5[1];
			
			//indatas10
			Object[] objs_Indata10=jenks_Indata10.getJenks_Our(classNum);
			List<Double> k_Indata10=(List<Double>) objs_Indata10[0];
			String kStr_Indata10=StaticMethod.addString(",", k_Indata10);
			Double v_Indata10=(Double) objs_Indata10[1];
			
			//indatas15
			Object[] objs_Indata15=jenks_Indata15.getJenks_Our(classNum);
			List<Double> k_Indata15=(List<Double>) objs_Indata15[0];
			String kStr_Indata15=StaticMethod.addString(",", k_Indata15);
			Double v_Indata15=(Double) objs_Indata15[1];
			
			//outdatas5
			Object[] objs_Outdata5=jenks_Outdata5.getJenks_Our(classNum);
			List<Double> k_Outdata5=(List<Double>) objs_Outdata5[0];
			String kStr_Outdata5=StaticMethod.addString(",", k_Outdata5);
			Double v_Outdata5=(Double) objs_Outdata5[1];
			
			//outdatas10
			Object[] objs_Outdata10=jenks_Outdata10.getJenks_Our(classNum);
			List<Double> k_Outdata10=(List<Double>) objs_Outdata10[0];
			String kStr_Outdata10=StaticMethod.addString(",", k_Outdata10);
			Double v_Outdata10=(Double) objs_Outdata10[1];
			
			//outdatas15
			Object[] objs_Outdata15=jenks_Outdata15.getJenks_Our(classNum);
			List<Double> k_Outdata15=(List<Double>) objs_Outdata15[0];
			String kStr_Outdata15=StaticMethod.addString(",", k_Outdata15);
			Double v_Outdata15=(Double) objs_Outdata15[1];
			
			GROUP("GROUP_"+classNum,kStr_Length,v_Length,
					kStr_Speed5,v_Speed5,kStr_Speed10,v_Speed10,kStr_Speed15,v_Speed15,
					kStr_Gcl5,v_Gcl5,kStr_Gcl10,v_Gcl10,kStr_Gcl15,v_Gcl15,
					kStr_Indata5,v_Indata5,kStr_Indata10,v_Indata10,kStr_Indata15,v_Indata15,
					kStr_Outdata5,v_Outdata5,kStr_Outdata10,v_Outdata10,kStr_Outdata15,v_Outdata15
					);
		}
	}
	
	private void GROUP(String tableName, String kStr_Length, Double v_Length,
			String kStr_Speed5, Double v_Speed5, String kStr_Speed10, Double v_Speed10, String kStr_Speed15, Double v_Speed15, 
			String kStr_Gcl5,  Double v_Gcl5, String kStr_Gcl10, Double v_Gcl10, String kStr_Gcl15, Double v_Gcl15, 
			String kStr_Indata5, Double v_Indata5, String kStr_Indata10, Double v_Indata10, String kStr_Indata15, Double v_Indata15,
			String kStr_Outdata5, Double v_Outdata5, String kStr_Outdata10, Double v_Outdata10, String kStr_Outdata15, Double v_Outdata15)
	{
		String insertSql = "insert into "+tableName+"(LENGTH, LENGTH_W, "
				+ "SPEED_5, SPEED_W5, SPEED_10, SPEED_W10, SPEED_15, SPEED_W15, "
				+ "GCL_5, GCL_W5, GCL_10, GCL_W10, GCL_15, GCL_W15, "
				+ "INDATA_5, INDATA_W5, INDATA_10, INDATA_W10, INDATA_15, INDATA_W15, "
				+ "OUTDATA_5, OUTDATA_W5, OUTDATA_10, OUTDATA_W10, OUTDATA_15, OUTDATA_W15) values("
				+"\'"+kStr_Length+"\',"+v_Length
				+",\'"+kStr_Speed5+"\',"+v_Speed5+",\'"+kStr_Speed10+"\',"+v_Speed10+",\'"+kStr_Speed15+"\',"+v_Speed15
				+",\'"+kStr_Gcl5+"\',"+v_Gcl5+",\'"+kStr_Gcl10+"\',"+v_Gcl10+",\'"+kStr_Gcl15+"\',"+v_Gcl15
				+",\'"+kStr_Indata5+"\',"+v_Indata5+",\'"+kStr_Indata10+"\',"+v_Indata10+",\'"+kStr_Indata15+"\',"+v_Indata15
				+",\'"+kStr_Outdata5+"\',"+v_Outdata5+",\'"+kStr_Outdata10+"\',"+v_Outdata10+",\'"+kStr_Outdata15+"\',"+v_Outdata15
				+")";

		new JDBC().execute(insertSql);
	}
	
	
	private static Class_Number getLengths() {
		String sql="select value,count(value) from LENGTHS group by value order by value";
		System.out.println("读取lengths");
		return StaticMethod.getListFromDB(sql);
	}
	
	private static Class_Number getSpeed5() {
		String sql="select TRUNC(value,1),count(TRUNC(value,1)) from SPEED_5 where VALUE!=0 group by TRUNC(value,1) order by TRUNC(value,1)";
		System.out.println("读取speed5");
		return StaticMethod.getListFromDB(sql);
	}

	private static Class_Number getSpeed10() {
		String sql="select TRUNC(value,1),count(TRUNC(value,1)) from SPEED_10 where VALUE!=0 group by TRUNC(value,1) order by TRUNC(value,1)";
		System.out.println("读取speed10");
		return StaticMethod.getListFromDB(sql);
	}

	private static Class_Number getSpeed15() {
		String sql="select TRUNC(value,1),count(TRUNC(value,1)) from SPEED_15 where VALUE!=0 group by TRUNC(value,1) order by TRUNC(value,1)";
		System.out.println("读取speed15");
		return StaticMethod.getListFromDB(sql);
	}
	
	private static Class_Number getGcls5() {
		String sql="select value,count(value) from GCLS_5 where VALUE!=0 group by value order by value";
		System.out.println("读取gcls5");
		return StaticMethod.getListFromDB(sql);
	}

	
	private static Class_Number getGcls10() {
		String sql="select value,count(value) from GCLS_10 where VALUE!=0 group by value order by value";
		System.out.println("读取gcls10");
		return StaticMethod.getListFromDB(sql);
	}


	private static Class_Number getGcls15() {
		String sql="select value,count(value) from GCLS_15 where VALUE!=0 group by value order by value";
		System.out.println("读取gcls15");
		return StaticMethod.getListFromDB(sql);
	}


	private static Class_Number getIndatas5() {
		String sql="select TRUNC(value,0),count(TRUNC(value,0)) from INDATAS_5 where VALUE!=0 group by TRUNC(value,0) order by TRUNC(value,0)";
		System.out.println("读取indatas5");
		return StaticMethod.getListFromDB(sql);
	}


	private static Class_Number getIndatas10() {
		String sql="select TRUNC(value,0),count(TRUNC(value,0)) from INDATAS_10 where VALUE!=0 group by TRUNC(value,0) order by TRUNC(value,0)";
		System.out.println("读取indatas10");
		return StaticMethod.getListFromDB(sql);
	}

	private static Class_Number getIndatas15() {
		String sql="select TRUNC(value,0),count(TRUNC(value,0)) from INDATAS_15 where VALUE!=0 group by TRUNC(value,0) order by TRUNC(value,0)";
		System.out.println("读取indatas15");
		return StaticMethod.getListFromDB(sql);
	}

	private static Class_Number getOutdatas5() {
		String sql="select TRUNC(value,1),count(TRUNC(value,1)) from OUTDATAS_5 where VALUE!=0 group by TRUNC(value,1) order by TRUNC(value,1)";
		System.out.println("读取outdatas5");
		return StaticMethod.getListFromDB(sql);
	}


	private static Class_Number getOutdatas10() {
		String sql="select TRUNC(value,1),count(TRUNC(value,1)) from OUTDATAS_10 where VALUE!=0 group by TRUNC(value,1) order by TRUNC(value,1)";
		System.out.println("读取outdatas10");
		return StaticMethod.getListFromDB(sql);
	}

	private static Class_Number getOutdatas15() {
		String sql="select TRUNC(value,1),count(TRUNC(value,1)) from OUTDATAS_15 where VALUE!=0 group by TRUNC(value,1) order by TRUNC(value,1)";
		System.out.println("读取outdatas15");
		return StaticMethod.getListFromDB(sql);
	}
	
	public static void main(String[] args) {
		new Group_Main().start();
	}
	
}


