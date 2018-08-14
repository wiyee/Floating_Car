package myTools;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;

public class StaticValue {
	
	/**
	 * 固定参数
	 */
	public static final String path="C:\\Users\\wiyee\\Desktop\\最新成果\\数据\\";
	public static final double LimitDis=85.0/20000;
	public static final long twoHour=2*60*60*1000;
	public static final double Second=1000;
	public static final long startMs=Timestamp.valueOf("2015-02-02 00:00:00").getTime();
	public static final int maxClassNum=6;   //最大聚类数
	
	public static String trainStartTime="2015-02-01 06:00:00";
	public static String trainEndTime="2015-02-03 22:00:00";
//	
//	public static String runStartTime="2015-02-02 00:00:00";
//	public static String runEndTime="2015-02-03 00:00:00";
	public static String runStartTime="2015-02-02 00:00:00";
	public static String runEndTime="2015-02-03 00:00:00";
	
	public static final double rate=0.4;  //时间跨度阈值
	
	
	/**
	 * NN_Forcast算法参数
	 */
	public static int roadNum=3;   //k：近邻路段数
	public static int stateNum=4;  //M：相关状态数
	public static int groupNum=5;  //nc：分段数
	
	
	/**
	 * C-WSVM算法参数
	 */
	public static final int C=1000;    //惩罚系数
	public static final double tol = 0.001;   //可容忍偏差
	public static int back=6;    //序列长度
	public static final int kA=6;   //核函数参数
	
	
	
	/**
	 * 数据库
	 */
	public static final String name="LS3";
	public static final String pwd="123";
    public static final String ip="10.1.18.155";
    public static final String dbName="orcl";
    public static String driver;
    public static String con;
    
    /**
     * 地图参数
     */
    public static final double begin_lon=120.5;
	public static final double begin_lat=27.8;
	public static final double end_lon=120.9;
	public static final double end_lat=28.15;     /*长方形区域*/
	public static final double dist=(double)700.0000001/100000;  //邻近区域半径
    
    /**
     * 基础设置
     */
    public static final double EARTH_RADIUS=6378.137;
    public static final DateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
    public static final double MaxDouble=99999.99999;
    public static final String NullStr="null";
    public static final int block=5*60*1000;    //默认一层为5分钟，单位（毫秒）
	public static final double alpha=0;      //训练得出的衰减参数

	static{
		String[] strArr=StaticMethod.setDataBase(StaticMethod.DbSystem.ORACLE,ip,dbName);
		driver=strArr[0];
		con=strArr[1];
	}
	
}
