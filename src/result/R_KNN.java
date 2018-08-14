package result;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dbTable.TRAFFIC;
import jdbc.JDBC;
import myTools.StaticMethod;
import myTools.StaticValue;
import pojo.NearRoad;
import pojo.NearRoadTwo;
import pojo.Traffic;

public class R_KNN {
	
	public static String writeUrl="C:\\Users\\wiyee\\Desktop\\最新成果\\数据\\KNN\\result_";
	
	public static String writeUrl2="C:\\Users\\wiyee\\Desktop\\最新成果\\数据\\KNN\\res_";
	
	//0~2  2~4  4~6 ... 22~24
	List<List<Double>> listArr=new ArrayList<List<Double>>();
	
	public static int back=6;
	public static int k=1;
	
	public static String runStartTime="2015-02-02 00:00:00";
	public static String runEndTime="2015-02-03 00:00:00";
	public static String trainStartTime="2015-02-03 05:00:00";
	public static String trainEndTime="2015-02-03 22:00:00";
	
//	public static final int back=3;
//	public static final int k=3;
	
	public long minFromMs=Long.MAX_VALUE;
	public long maxFromMs=0;
	double outSum;
	//<= 1km/h  2km/h  3km/h  4km/h  5km/h  10km/h 
	double[] vList;
	double MAPE;
	double RMSE;
	
	public R_KNN() {
		for(int i=5;i<=15;i+=5)
		{
			vList=new double[6];
			MAPE=0;
			RMSE=0;
			outSum=0;
			for(int jj=0;jj<12;jj++)
			{
				listArr.add(new ArrayList<Double>());
			}
			
			long h=i*60*1000;
			//路段id——fromMs
			Map<Integer,List<Long>> useRoadMap=getUserRoadMap(i);
			//路段id——fromMs——
			Map<Integer,Map<Long,Traffic>> trafficMap=getTrafficMap(i,runStartTime,runEndTime);
			Map<Integer,Map<Long,Traffic>> conTrafficMap=getTrafficMap(i,trainStartTime,trainEndTime);
			
			Set<Integer> roadIdSet=new HashSet<Integer>(useRoadMap.keySet());
			//路段id——
			Map<Integer,NearRoadTwo> nrMap=getNrMap(i,roadIdSet);
			oFlag:
			for(int roadId:roadIdSet)
			{
				System.out.println("i="+i+",roadId="+roadId);
				Map<Long,Traffic> cuTfMap=trafficMap.get(roadId);
				List<Long> fromMsList=useRoadMap.get(roadId);
				NearRoadTwo nr=nrMap.get(roadId);
				Map<Long,Traffic> concuTfMap=conTrafficMap.get(roadId);
				BackClass.nr=nr;
				
				List<BackClass> bcList=new ArrayList<BackClass>();
				fllag:
				for(long fromMs=minFromMs+back*h;fromMs<maxFromMs-h;fromMs+=h)
				{
					List<Double> speedList=new ArrayList<Double>();
					for(int bck=1;bck<=back;bck++)
					{
						long ms=fromMs-bck*h;
//						speedList.add(concuTfMap.get(ms).getSpeed());
//						flag:
						for(int nearRoadId:nr.getRoads())
						{
							Map<Long, Traffic> ctfMap=conTrafficMap.get(nearRoadId);
							if(ctfMap==null)
							{
								System.out.println(roadId);
								continue oFlag;
							}
							Traffic tf=ctfMap.get(ms);
							if(tf==null)
							{
								continue fllag;
							}
							speedList.add(tf.getSpeed());
						}
					}
					Traffic tf=concuTfMap.get(fromMs+h);
					if(tf==null)
					{
						continue;
					}
					BackClass bc=new BackClass(speedList, tf.getSpeed());
					bcList.add(bc);
				}
				flag2:
				for(long fromMs:fromMsList)
				{
					double speed=cuTfMap.get(fromMs).getSpeed();
					if(speed==0)
					{
						continue;
					}
					outSum++;
					List<Double> foreSpeedList=new ArrayList<Double>();
					for(int bck=1;bck<=back;bck++)
					{
						long ms=fromMs-bck*h;
//						foreSpeedList.add(cuTfMap.get(ms).getSpeed());
						for(int nearRoadId:nr.getRoads())
						{
							Map<Long, Traffic> tfMap=trafficMap.get(nearRoadId);
							Traffic tf=tfMap.get(ms);
							if(tf==null)
							{
								continue flag2;
							}
							foreSpeedList.add(tf.getSpeed());
						}
					}
					BackClass.conList=foreSpeedList;
					initBCList(bcList);
					Collections.sort(bcList);
					double sum=0;
					for(int jj=0;jj<k;jj++)
					{
						sum+=bcList.get(jj).val;
					}
					double foreSpeed=0;
					for(int jj=0;jj<k;jj++)
					{
						foreSpeed+=bcList.get(jj).val*bcList.get(jj).foreVal/sum;
					}
					double diff=Math.abs(foreSpeed-speed);
					int idx=getNum(diff);
					if(idx!=-1)
					{
						vList[idx]+=1;
					}
					MAPE+=diff/speed;
					RMSE+=diff*diff;
					listArr.get(StaticMethod.getHourIdx(fromMs)).add(foreSpeed-speed);
				}
			}
			RMSE/=outSum;
			for(int j=0;j<vList.length;j++)
			{
				vList[j]/=outSum;
			}
			MAPE/=outSum;
			RMSE=Math.sqrt(RMSE);
			//writeToFile(i);
			StaticMethod.writeToFile2(writeUrl2,i,listArr);
		}
	}
	
	void initBCList(List<BackClass> bcList)
	{
		for(BackClass bc:bcList)
		{
			bc.val=null;
		}
	}
	
	
	private void writeToFile(int i) {
		File file1=new File(writeUrl+i+".txt");
		OutputStreamWriter write = null;
		try{
			write = new OutputStreamWriter(new FileOutputStream(file1),"UTF-8");//考虑到编码格式
			BufferedWriter bufferedWriter = new BufferedWriter(write);
			 if (!file1.exists()) {
				    file1.createNewFile();
				   }
			 bufferedWriter.write("1km/h,2km/h,3km/h,4km/h,5km/h,10km/h,MAPE,RMSE");
			 bufferedWriter.newLine();
			 for(int j=1;j<vList.length;j++)
			 {
				 vList[j]+=vList[j-1];
			 }
			 String vListStr=StaticMethod.addString(",", vList);
			 bufferedWriter.write(vListStr+","+MAPE+","+RMSE);
			 bufferedWriter.newLine();
			 bufferedWriter.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
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
	
	private int getNum(double v){
		if(v>10)
		{
			return -1;
		}
		if(v>5)
		{
			return 5;
		}
		return Math.max((int) (Math.ceil(v)-1),0);
	}
	
	
	private Map<Integer, Map<Long, Traffic>> getTrafficMap(int i,String startTime,String endTime) {                                               i=20-i;
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
			minFromMs=Math.min(minFromMs, fromMs);
			maxFromMs=Math.max(maxFromMs, fromMs);
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
		System.out.println("读取数据成功！");
		return res;
	}
	
	
	private Map<Integer, List<Long>> getUserRoadMap(int i) {
		Map<Integer, List<Long>> res=new HashMap<Integer, List<Long>>();
		long startMs=Timestamp.valueOf(runStartTime).getTime()+i*60*1000;
		long endMs=Timestamp.valueOf(runEndTime).getTime();
		String sql="select ROADID,FROMMS from USERROAD_"+i+" where FROMMS >= "+startMs+" and FROMMS <= "+endMs+" order by ROADID";
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
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			jdbc.close();
		}
		return res;
	}


	public static void main(String[] args) {
		new R_KNN();
	}
	
}


class BackClass implements Comparable<BackClass>{
	
 	public static List<Double> conList;
 	public static NearRoadTwo nr;
	
	List<Double> backList=null;
	public Double val=null;  //距离值
	public double foreVal;   //预测值

	public BackClass(List<Double> backList,double foreVal) {
		super();
		this.backList = backList;
		this.foreVal = foreVal;
	}
	
	 double getVal()
	{
		if(val==null)
		{
			val=0d;
			List<Double> weightList=nr.getWeights();
			int nrSize=weightList.size();
			for(int i=0;i<conList.size();i++)
			{
				val+=Math.abs(backList.get(i)-conList.get(i))*weightList.get(i%nrSize);
			}
		}
		return val;
	}

	@Override
	public int compareTo(BackClass o) {
		return Double.compare(getVal(), o.getVal());
	}
	
}