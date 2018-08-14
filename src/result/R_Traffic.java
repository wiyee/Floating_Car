//package result;
//
//import java.sql.ResultSet;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Set;
//import jdbc.JDBC;
//import myTools.StaticMethod;
//import speed.traffic.TRAFFIC;
//
//public class R_Traffic {
//	
//	List<List<SpeedOne>> speedOneList=new ArrayList<List<SpeedOne>>();
//	List<List<GclOne>> gclOneList=new ArrayList<List<GclOne>>();
//	List<List<InDataOne>> inDataOneList=new ArrayList<List<InDataOne>>();
//	List<List<OutDataOne>> outDataOneList=new ArrayList<List<OutDataOne>>();
//	
//	List<Double> speedList=new ArrayList<Double>();
//	List<Double> gclList=new ArrayList<Double>();
//	List<Double> inDataList=new ArrayList<Double>();
//	List<Double> outDataList=new ArrayList<Double>();
//	private static final int time=10;
//	public String fromTime;
//    public String toTime;
//    public int segment;  //回滚的时间
//    public int forward;  //向前更新时间
//	
//	public R_Traffic() {
//		segment=time*60*1000;
//		forward=time*60*1000;
//		TRAFFIC.TABLENAME="TRAFFIC_"+time;
//		for(int d=1;d<=3;d++)    //3天
//		{
//			fromTime="2015-02-0"+d+" 00:00:00";
//			toTime="2015-02-0"+(d+1)+" 00:00:00";
//			init();
//			getAllList(10);
//			writeToDB();
//		}
//	}
//	
//	
//	private void writeToDB()
//	{
//		
//		
//		
//	}
//	
//	
//	private void init()
//	{
//		speedOneList=new ArrayList<List<SpeedOne>>();
//		gclOneList=new ArrayList<List<GclOne>>();
//		inDataOneList=new ArrayList<List<InDataOne>>();
//		outDataOneList=new ArrayList<List<OutDataOne>>();
//		
//		speedList=new ArrayList<Double>();
//		gclList=new ArrayList<Double>();
//		inDataList=new ArrayList<Double>();
//		outDataList=new ArrayList<Double>();
//	}
//	
//	private void getAllList(int i) {
//		Set<Integer> roadSet=StaticMethod.getRoadSet(i);
//		String sql="select ID,LINKID,SPEED,FROM_TIME,GCL,WIDTH,LENGTH,INDATA,OUTDATA from TRAFFIC_"+i+" where FROM_TIME between "+StaticMethod.turnDbDate(fromTime)+" and "+ StaticMethod.turnDbDate(toTime) +" order by FROM_TIME,LINKID";
//		JDBC jdbc=new JDBC();
//		try
//		{
//			ResultSet rs=jdbc.executeQuery(sql);
//			long fromMs=-1;
//			List<SpeedOne> cuSpeed=null;
//			List<GclOne> cuGcl=null;
//			List<InDataOne> cuInData=null;
//			List<OutDataOne> cuOutData=null;
//			int size=roadSet.size();
//			
//			double speedSum=0;
//			double gclSum=0;
//			double inDataSum=0;
//			double outDataSum=0;
//			
//			while(rs.next()){
//				int linkId=rs.getInt(2);
//				if(roadSet.contains(linkId))
//				{
//					int id=rs.getInt(1);
//					double speed=rs.getDouble(3);
//					long cuFromMs=rs.getTimestamp(4).getTime();
//					int gcl=rs.getInt(5);
//					double width=rs.getDouble(6);
//					double length=rs.getDouble(7);
//					double indata=rs.getDouble(8);
//					double outdata=rs.getDouble(9);
//					SpeedOne so=new SpeedOne(linkId,speed);
//					GclOne go=new GclOne(linkId, gcl);
//					InDataOne ino=new InDataOne(linkId, indata);
//					OutDataOne outo=new OutDataOne(linkId, outdata);
//					if(cuFromMs!=fromMs)
//					{
//						fromMs=cuFromMs;
//						cuSpeed=new ArrayList<SpeedOne>();
//						cuSpeed.add(so);
//						speedOneList.add(cuSpeed);
//						
//						cuGcl=new ArrayList<GclOne>();
//						cuGcl.add(go);
//						gclOneList.add(cuGcl);
//						
//						cuInData=new ArrayList<InDataOne>();
//						cuInData.add(ino);
//						inDataOneList.add(cuInData);
//						
//						cuOutData=new ArrayList<OutDataOne>();
//						cuOutData.add(outo);
//						outDataOneList.add(cuOutData);
//						
//						speedList.add(speedSum);
//						gclList.add(gclSum);
//						inDataList.add(inDataSum);
//						outDataList.add(outDataSum);
//					}else
//					{
//						cuSpeed.add(so);
//						cuGcl.add(go);
//						cuInData.add(ino);
//						cuOutData.add(outo);
//						
//						speedSum+=so.value/size;
//						gclSum+=go.value/size;
//						inDataSum+=ino.value/size;
//						outDataSum+=outo.value/size;
//					}
//				}
//			}
//		}catch(Exception e)
//		{
//			e.printStackTrace();
//		}finally{
//			jdbc.close();
//		}
//	}
//	
//}
//
//class SpeedOne implements Comparable<SpeedOne>{
//	int linkId;
//	double value;
//	
//	public SpeedOne(int linkId, double value) {
//		super();
//		this.linkId = linkId;
//		this.value = value;
//	}
//
//
//
//	@Override
//	public int compareTo(SpeedOne o) {
//		return Integer.compare(linkId, o.linkId);
//	}
//}
//
//class GclOne implements Comparable<SpeedOne>{
//	int linkId;
//	double value;
//	
//	public GclOne(int linkId, double value) {
//		super();
//		this.linkId = linkId;
//		this.value = value;
//	}
//
//	@Override
//	public int compareTo(SpeedOne o) {
//		return Integer.compare(linkId, o.linkId);
//	}
//}
//
//class InDataOne implements Comparable<SpeedOne>{
//	int linkId;
//	double value;
//	
//	
//	public InDataOne(int linkId, double value) {
//		super();
//		this.linkId = linkId;
//		this.value = value;
//	}
//
//
//	@Override
//	public int compareTo(SpeedOne o) {
//		return Integer.compare(linkId, o.linkId);
//	}
//}
//
//class OutDataOne implements Comparable<SpeedOne>{
//	int linkId;
//	double value;
//	
//	public OutDataOne(int linkId, double value) {
//		super();
//		this.linkId = linkId;
//		this.value = value;
//	}
//
//	@Override
//	public int compareTo(SpeedOne o) {
//		return Integer.compare(linkId, o.linkId);
//	}
//}