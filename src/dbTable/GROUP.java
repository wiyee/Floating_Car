package dbTable;

import java.sql.ResultSet;

import dbTable.pojo.Group_POJO;
import jdbc.JDBC;
import myTools.StaticValue;

public class GROUP {
	
	public static Group_POJO getGroup()
	{
		String sql="select GCL_5,INDATA_5,OUTDATA_5,GCL_10,INDATA_10,OUTDATA_10,GCL_15,INDATA_15,OUTDATA_15,SPEED_5,SPEED_10,SPEED_15 from GROUP_"+StaticValue.groupNum;
		JDBC jdbc=new JDBC();
		try
		{
			ResultSet rs=jdbc.executeQuery(sql);
			if(rs.next()){
				String GCL_5=rs.getString(1);
				String INDATA_5=rs.getString(2);
				String OUTDATA_5=rs.getString(3);
				String GCL_10=rs.getString(4);
				String INDATA_10=rs.getString(5);
				String OUTDATA_10=rs.getString(6);
				String GCL_15=rs.getString(7);
				String INDATA_15=rs.getString(8);
				String OUTDATA_15=rs.getString(9);
				String SPEED_5=rs.getString(10);
				String SPEED_10=rs.getString(11);
				String SPEED_15=rs.getString(12);
				return new Group_POJO(GCL_5, INDATA_5, OUTDATA_5, GCL_10, INDATA_10, OUTDATA_10, GCL_15, INDATA_15, OUTDATA_15, SPEED_5, SPEED_10, SPEED_15);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			jdbc.close();
		}
		return null;
	}
}
