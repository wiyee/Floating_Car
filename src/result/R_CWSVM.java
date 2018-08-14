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

import dbTable.TRAFFIC;
import jdbc.JDBC;
import myTools.CW_SVM;
import myTools.StaticMethod;
import myTools.StaticValue;
import pojo.SVMModel;
import pojo.Traffic;

public class R_CWSVM {
	
	public static String writeUrl="C:\\Users\\wiyee\\Desktop\\最新成果\\数据\\CWSVM\\result1_";
	public static String writeUrl2="C:\\Users\\wiyee\\Desktop\\最新成果\\数据\\CWSVM\\res1_";
	
	public long minFromMs=Long.MAX_VALUE;
	public long maxFromMs=0;
	
	public static String startTime="2015-02-02 00:00:00";
	public static String endTime="2015-02-03 00:00:00";
	
	//0~2  2~4  4~6 ... 22~24
	public List<List<Double>> listArr=new ArrayList<List<Double>>();
	
	public double outSum;
	//<= 1km/h  2km/h  3km/h  4km/h  5km/h  10km/h 
	public double[] vList;
	public double MAPE;
	public double RMSE;
	
	public static final int num=100;
	
	public R_CWSVM() {
		for(int i=10;i<=10;i+=5)
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
			System.out.println("开始读取训练数据！");
			Map<Integer,Map<Long,Traffic>> conTrafficMap=getTrafficMap(i, startTime, endTime);

			List<double[]> xList=new ArrayList<double[]>();
			List<Double> yList=new ArrayList<Double>();
			System.out.println("开始构造x、y向量！");
			flg:
			for(int roadId:conTrafficMap.keySet())
			{
				Map<Long,Traffic> coTfMap=conTrafficMap.get(roadId);
				for(long fromMs=minFromMs+StaticValue.back*h;fromMs<maxFromMs-h;fromMs+=h)
				{
					double cuSpeed=coTfMap.get(fromMs).getSpeed();
					if(cuSpeed==0)
					{
						continue;
					}
					double[] x0=new double[StaticValue.back];
					int sum0=0;
					for(int bck=1;bck<=StaticValue.back;bck++)
					{
						long ms=fromMs-bck*h;
						Traffic tf=coTfMap.get(ms);
						double speed=tf.getSpeed();
						if(speed==0)
						{
							sum0++;
						}
						x0[bck-1]=speed/100+1;
					}
					if(sum0==0)
					{
						xList.add(x0);
						yList.add(cuSpeed);
						if(xList.size()>num)
						{
							break flg;
						}
					}
				}
			}
			int len=xList.size();
			double[][] x=new double[len][StaticValue.back];
			double[] y=new double[len];
			for(int k=0;k<len;k++)
			{
				x[k]=xList.get(k);
				y[k]=yList.get(k);
			}
			xList.clear();
			yList.clear();
			System.out.println("向量长度："+len);
			System.out.println("开始训练向量！");
			CW_SVM simplifiedSMO = new CW_SVM();
			SVMModel model = simplifiedSMO.train(x,y);
			//路段id——fromMs——
			System.out.println("开始读取测试数据！");
			Map<Integer,Map<Long,Traffic>> trafficMap=getTrafficMap(i,StaticValue.runStartTime,StaticValue.runEndTime);
			
			Set<Integer> roadIdSet=new HashSet<Integer>(useRoadMap.keySet());
			System.out.println("预测开始！");
			int iidx=0;
			for(int roadId:roadIdSet)
			{
				System.out.println("已预测："+(iidx++)+"/"+num);
				if(iidx>num)
				{
					break;
				}
				Map<Long,Traffic> cuTfMap=trafficMap.get(roadId);
				List<Long> fromMsList=useRoadMap.get(roadId);
				lflag:
				for(long fromMs:fromMsList)
				{
					if(fromMs<minFromMs+StaticValue.back*h)
					{
						continue;
					}
					double speed=cuTfMap.get(fromMs).getSpeed();
//					System.out.println("real:-----------"+speed);
					if(speed==0)
					{
						continue;
					}
					double[] x0=new double[StaticValue.back];
					for(int bck=1;bck<=StaticValue.back;bck++)
					{
						long ms=fromMs-bck*h;
						Traffic tf=cuTfMap.get(ms);
						if(tf==null)
						{
							continue lflag;
						}
						double speed0=tf.getSpeed();
						x0[bck-1]=speed0/100+1;
					}
					double foreSpeed=simplifiedSMO.predict(model,x0);				
//					System.out.println("predicted:-----------"+foreSpeed);
					
					outSum++;
					double diff=Math.abs(foreSpeed-speed);
					int idx=getNum(diff);
					if(idx!=-1)
					{
						vList[idx]+=1;
					}
					MAPE+=diff/speed;
					RMSE+=diff*diff;
					int tmp = StaticMethod.getHourIdx(fromMs);
					if (tmp<12&&tmp>=0) {
						listArr.get(tmp).add(foreSpeed-speed);
					}
					
				}
			}
			RMSE/=outSum;
			for(int j=0;j<vList.length;j++)
			{
				vList[j]/=outSum;
			}
			MAPE/=outSum;
			RMSE=Math.sqrt(RMSE);
			writeToFile(i);
//			StaticMethod.writeToFile2(writeUrl2,i,listArr);
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
	
	
	private Map<Integer, Map<Long, Traffic>> getTrafficMap(int i,String startTime,String endTime) {
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
	
	public static void main(String[] args) {
		new R_CWSVM();
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

}
