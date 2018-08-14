package result;

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
import pojo.NearState;
import pojo.Traffic;
import speed.args.ArgsMain;

public class R_NN_Forcast {
	public static String writeUrl="C:\\Users\\wiyee\\Desktop\\最新成果\\数据\\Our_Method\\result_";
	
	public static String writeUrl2="C:\\Users\\wiyee\\Desktop\\最新成果\\数据\\Our_Method\\res_";
	static String startTime="2015-02-01 05:00:00";
	static String endTime="2015-02-02 22:00:00";
	
	//0~2  2~4  4~6 ... 22~24
	public List<List<Double>> listArr=new ArrayList<List<Double>>();
	
	public double outSum;
	//<= 1km/h  2km/h  3km/h  4km/h  5km/h  10km/h
	public double[] vList;
	public double MAPE;
	public double RMSE;
//	List<Integer> list=new ArrayList<Integer>(){
//		{
//			add(5);add(10);add(15);
//		}
//	};
	
	public R_NN_Forcast() {
		Group_POJO group=GROUP.getGroup();

		for(int i=5;i<=15;i+=5)
		{                                                                                                   
//			i=20-i;
			vList=new double[6];
			MAPE=0;
			RMSE=0;
			outSum=0;
			for(int jj=0;jj<13;jj++)
			{
				listArr.add(new ArrayList<Double>());
			}
			long h=i*60*1000;
			//路段id——fromMs

			Map<Integer,List<Long>> useRoadMap=getUserRoadMap(i);
			//路段id——fromMs——
			Map<Integer,Map<Long,Traffic>> trafficMap=getTrafficMap(i);
//			Map<Integer,NearState> nsMap=getNsMap(i);
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
//				NearState ns=nsMap.get(roadId);
				Map<Integer,ArgOne> aoMap=argMap.get(roadId);
				if(cuTfMap==null||aoMap==null)
				{
					continue;
				}

				for(long fromMs:fromMsList)
				{
					List<Integer> roads=nr.getRoads();
					List<Double> weights=nr.getWeights();
					if(!cuTfMap.containsKey(fromMs))
					{
						continue;
					}
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
					outSum++;
					foreSpeed/=sum;
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
//			writeToFile(i);
			StaticMethod.writeToFile2(writeUrl2,i,listArr);
		}
	}
	
	private void writeToFile(int i) {                                                                                                                         i=20-i;
		File file1=new File(writeUrl+i+"(mk="+StaticValue.roadNum+",m="+StaticValue.stateNum+",nc="+StaticValue.groupNum+").txt");
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
	
	private Map<Integer,NearState> getNsMap(int i)
	{
		Map<Integer,NearState> res=new HashMap<Integer, NearState>();
		List<NearState> nsList=StaticMethod.getNearStateList(i);
		for(NearState ns:nsList)
		{
			res.put(ns.linkid, ns);
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
	
	
	private Map<Integer, Map<Long, Traffic>> getTrafficMap(int i) {                                                                                  
		List<Traffic> trafficList=TRAFFIC.getTrafficList(i, StaticValue.runStartTime, StaticValue.runEndTime);
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
		System.out.println("读取数据成功！");
		return res;
	}
	
	private Map<Integer, List<Long>> getUserRoadMap(int i) {
		Map<Integer, List<Long>> res=new HashMap<Integer, List<Long>>();
		long startMs=Timestamp.valueOf(StaticValue.runStartTime).getTime()+i*60*1000;
		long endMs=Timestamp.valueOf(StaticValue.runEndTime).getTime();

		
		String sql="select ROADID,FROMMS from USERROAD_"+i+" where FROMMS >= "+startMs+" and FROMMS <= "+endMs+" order by ROADID";
		JDBC jdbc=new JDBC();
		try
		{
			ResultSet rs=jdbc.executeQuery(sql);
			int cuRoadId=0;
			List<Long> cuList=new ArrayList<Long>();
			if(rs.next()){  
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
//		new ArgsMain();
		new R_NN_Forcast();
		
//		for(int mk=1;mk<=6;mk++)
//		{
//			StaticValue.roadNum=mk;
//			test();
//		}
//		StaticValue.roadNum=5;
//		for(int nc=2;nc<=6;nc++)
//		{
//			StaticValue.groupNum=nc;
//			test();
//		}
	}
	
	private static void test()
	{
		new ArgsMain();
		new R_NN_Forcast();
	}
}
