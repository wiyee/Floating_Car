package speed.traffic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

public class R_RealTime {
	private static String startTime="2015-02-03 07:50:00";
	
	private static final int roadNum=300;
	
	List<RealTime> list=null;
	
	private static String writeUrl="C:\\Users\\wiyee\\Desktop\\最新成果\\RealTime\\";
	
	public R_RealTime() {
		Group_POJO group=GROUP.getGroup();
		for(int i=5;i<=15;i+=5)
		{
			list=new ArrayList<RealTime>();
			long h=i*60*1000;
			//路段id——fromMs
			Map<Integer,List<Long>> useRoadMap=getUserRoadMap(i);
			//路段id——fromMs——
			Map<Integer,Map<Long,Traffic>> trafficMap=getTrafficMap(i);
			Set<Integer> roadIdSet=new HashSet<Integer>(useRoadMap.keySet());
			//路段id——
			Map<Integer,NearRoadTwo> nrMap=getNrMap(i,roadIdSet);
			GroupList gl=new GroupList(group, i);
			Map<Integer,Map<Integer,ArgOne>> argMap=getArgMap(i);
			for(int roadId:roadIdSet)
			{
				Map<Long,Traffic> cuTfMap=trafficMap.get(roadId);
				List<Long> fromMsList=useRoadMap.get(roadId);
				NearRoadTwo nr=nrMap.get(roadId);
				Map<Integer,ArgOne> aoMap=argMap.get(roadId);
				if(aoMap==null)
				{
					continue;
				}
				for(long fromMs:fromMsList)
				{
					List<Integer> roads=nr.getRoads();
					List<Double> weights=nr.getWeights();
					double speed1=cuTfMap.get(fromMs-h).getSpeed();
					double speed=cuTfMap.get(fromMs).getSpeed();
					if(speed==0)
					{
						continue;
					}
					double foreSpeed=0;
					double sum=0;
					for(int idx:aoMap.keySet())
					{
						int nrRoadId=roads.get(idx);
						Map<Long,Traffic> tfMap=trafficMap.get(nrRoadId);
						if(tfMap==null)
						{
							continue;
						}
						double tmpSpeed=StaticMethod.forcastSpeed(fromMs, h, tfMap, aoMap.get(idx), gl);
						if(tmpSpeed==-1)
						{
							continue;
						}
						if(tmpSpeed<-50||tmpSpeed>100)
						{
							continue;
						}
						double w=weights.get(idx);
						sum+=w;
						foreSpeed+=w*tmpSpeed;
					}
					if(sum==0)
					{
						continue;
					}
					foreSpeed/=sum;
					list.add(new RealTime(roadId, speed1, speed, foreSpeed));
					System.out.println(roadId + speed1 + speed + foreSpeed);
				}
			}
//			writeToFile(i);
		}
	}
	
	private Map<Integer, NearRoadTwo> getNrMap(int i, Set<Integer> roadIdSet) {
		Map<Integer,NearRoadTwo> res=new HashMap<Integer, NearRoadTwo>();
		List<NearRoad> nrList=StaticMethod.getNearRoadList(i);
		for(NearRoad nr:nrList)
		{
			int linkId=nr.LINKID;
			if(roadIdSet.contains(linkId))
			{
				res.put(linkId, new NearRoadTwo(nr));
			}
		}
		return res;
	}
	
	private Map<Integer, Map<Long, Traffic>> getTrafficMap(int i) {
		String endTime=StaticMethod.nextTime(startTime, i*60*1000);
		List<Traffic> trafficList=TRAFFIC.getTrafficList(i, startTime, endTime);
		Map<Integer, Map<Long, Traffic>> res=new HashMap<Integer, Map<Long,Traffic>>();
		Traffic cuTf=trafficList.get(0);
		int cuLinkId=cuTf.getLinkId();
		Map<Long,Traffic> inMap=new HashMap<Long, Traffic>();
		inMap.put(cuTf.getFromMs(), cuTf);
		res.put(cuLinkId, inMap);
		for(int j=1;j<trafficList.size();j++)
		{
			Traffic tmp=trafficList.get(j);
			int tmpLinkId=tmp.getLinkId();
			long fromMs=tmp.getFromMs();
			if(cuLinkId!=tmpLinkId)
			{
				cuLinkId=tmpLinkId;
				inMap=new HashMap<Long, Traffic>();
				res.put(cuLinkId, inMap);
				inMap.put(fromMs, tmp);
				continue;
			}
			inMap.put(fromMs, tmp);
		}
		return res;
	}
	
	private Map<Integer, Map<Integer,ArgOne>> getArgMap(int i) {
		Map<Integer, Map<Integer,ArgOne>> res=new HashMap<Integer, Map<Integer,ArgOne>>();
		String sql="select ROADID,IDX,ALIST_SPEED,ALIST_GCL,ALIST_INDATA,ALIST_OUTDATA from ARGS_"+i;
		JDBC jdbc=new JDBC();
		try
		{
			ResultSet rs=jdbc.executeQuery(sql);
			while(rs.next()){
				Integer roadId=rs.getInt(1);
				Integer idx=rs.getInt(2);
				if(idx<StaticValue.roadNum)
				{
					String ALIST_SPEED=rs.getString(3);
					String ALIST_GCL=rs.getString(4);
					String ALIST_INDATA=rs.getString(5);
					String ALIST_OUTDATA=rs.getString(6);
					double[] aList_speed=StaticMethod.string2DoubleArr(ALIST_SPEED);
					double[] aList_gcl=StaticMethod.string2DoubleArr(ALIST_GCL);
					double[] aList_inData=StaticMethod.string2DoubleArr(ALIST_INDATA);
					double[] aList_outData=StaticMethod.string2DoubleArr(ALIST_OUTDATA);
					ArgOne ao=new ArgOne(aList_speed,aList_gcl,aList_inData,aList_outData);
					StaticMethod.addToMap(res, roadId, idx, ao);
				}
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			jdbc.close();
		}
		return res;
	}
	
	private void writeToFile(int i) {
		String url=writeUrl+i+"("+startTime.split("\\s+")[1].substring(0,2)+")";
		File file1=new File(url+"_speed1.txt");
		File file2=new File(url+"_speed2.txt");
		File file3=new File(url+"_speed3.txt");
		OutputStreamWriter write1 = null;
		OutputStreamWriter write2 = null;
		OutputStreamWriter write3 = null;
		try{
			write1 = new OutputStreamWriter(new FileOutputStream(file1),"UTF-8");//考虑到编码格式
			BufferedWriter bufferedWriter1 = new BufferedWriter(write1);
			 if (!file1.exists()) {
				    file1.createNewFile();
				   }
			 
			 write2 = new OutputStreamWriter(new FileOutputStream(file2),"UTF-8");//考虑到编码格式
			BufferedWriter bufferedWriter2 = new BufferedWriter(write2);
			 if (!file2.exists()) {
				    file2.createNewFile();
				   }
			 
			 write3 = new OutputStreamWriter(new FileOutputStream(file3),"UTF-8");//考虑到编码格式
			BufferedWriter bufferedWriter3 = new BufferedWriter(write3);
			 if (!file3.exists()) {
				    file3.createNewFile();
				   }
			 
			 bufferedWriter1.write("id,speed");
			 bufferedWriter1.newLine();
			 bufferedWriter2.write("id,speed");
			 bufferedWriter2.newLine();
			 bufferedWriter3.write("id,speed");
			 bufferedWriter3.newLine();
			 
			 for(RealTime rt:list)
			 {
				 int roadId=rt.id;
				 bufferedWriter1.write(roadId+","+rt.speed1);
				 bufferedWriter1.newLine();
				 bufferedWriter2.write(roadId+","+rt.speed2);
				 bufferedWriter2.newLine();
				 bufferedWriter3.write(roadId+","+rt.speed3);
				 bufferedWriter3.newLine();
			 }
			 bufferedWriter1.close();
			 bufferedWriter2.close();
			 bufferedWriter3.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private Map<Integer, List<Long>> getUserRoadMap(int i) {
		Map<Integer, List<Long>> res=new HashMap<Integer, List<Long>>();
		while(true)
		{
			res=new HashMap<Integer, List<Long>>();
			long startMs=Timestamp.valueOf(startTime).getTime()+i*60*1000;
			String sql="select ROADID,FROMMS from USERROAD_"+i+" where FROMMS = "+startMs+" order by ROADID";
			JDBC jdbc=new JDBC();
			try
			{
				ResultSet rs=jdbc.executeQuery(sql);
				int cuRoadId=0;
				List<Long> cuList=new ArrayList<Long>();
				if(rs.next())
				{
	//				outSum++;
					cuRoadId=rs.getInt(1);
					long fromMs=rs.getLong(2);
					cuList.add(fromMs);
					res.put(cuRoadId, cuList);
				}
				while(rs.next()){
	//				outSum++;
					int tmpRoadId=rs.getInt(1);
					long fromMs=rs.getLong(2);
					if(tmpRoadId!=cuRoadId)
					{
						cuList=new ArrayList<Long>();
						cuList.add(fromMs);
						cuRoadId=tmpRoadId;
						res.put(cuRoadId, cuList);
					}
					cuList.add(fromMs);
				}
				System.out.println(startTime+":  "+res.size());
				if(res.size()>roadNum)
				{
					break;
				}
				startTime=StaticMethod.nextTime(startTime, i*60*1000);
			}catch(Exception e)
			{
				e.printStackTrace();
			}finally{
				jdbc.close();
			}
		}
		return res;
	}
	
	
	public static void main(String[] args) {
		new R_RealTime();
	}
	
}


class RealTime{
	
	int id;
	double speed1;
	double speed2;
	double speed3;
	
	public RealTime(int id, double speed1, double speed2, double speed3) {
		super();
		this.id = id;
		this.speed1 = speed1;
		this.speed2 = speed2;
		this.speed3 = speed3;
	}
	
}
