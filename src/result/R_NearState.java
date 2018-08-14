package result;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import myTools.StaticMethod;
import pojo.NearState;

public class R_NearState {

	List<NearState> nsLIst=new ArrayList<NearState>();
	
	//speed————平均相似度、最具影响的比例、正相关比例、负相关比例
	//gcl————...
	//indata————...
	//outdata————...
	double[][] arr=null;
	
	public R_NearState() {
		for(int i=5;i<=15;i+=5)
		{
			nsLIst=StaticMethod.getNearStateList(i);
			arr=new double[4][4];
			double len=nsLIst.size();
			for(NearState ns:nsLIst)
			{
				double[] wsArr=ns.weightState;
				int[] syArr=ns.symbol;
				int maxValIdx=maxValIdx(wsArr);
				
				arr[maxValIdx][1]+=1/len;
				
				arr[0][0]+=wsArr[0]/len;
				if(syArr[0]==1)
				{
					arr[0][2]++;
				}else
				{
					arr[0][3]++;
				}
				
				arr[1][0]+=wsArr[1]/len;
				if(syArr[1]==1)
				{
					arr[1][2]++;
				}else
				{
					arr[1][3]++;
				}
				
				arr[2][0]+=wsArr[2]/len;
				if(syArr[2]==1)
				{
					arr[2][2]++;
				}else
				{
					arr[2][3]++;
				}
				
				arr[3][0]+=wsArr[3]/len;
				if(syArr[3]==1)
				{
					arr[3][2]++;
				}else
				{
					arr[3][3]++;
				}
			}
			
			for(int m=0;m<arr.length;m++)
			{
				double sum=arr[m][2]+arr[m][3];
				arr[m][2]/=sum;
				arr[m][3]/=sum;
			}
			String url="C:\\Users\\Administrator\\Desktop\\最新成果\\数据\\参数选取\\state\\state_"+i+".txt";
			writeFile(url);
		}
	}
	
	private int maxValIdx(double[] wsArr)
	{
		double maxV=-Double.MAX_VALUE;
		int res=-1;
		for(int i=0;i<wsArr.length;i++)
		{
			if(wsArr[i]>maxV)
			{
				maxV=wsArr[i];
				res=i;
			}
		}
		return res;
	}
	
	
	private void writeFile(String writeUrl){                                                                                                     StaticMethod.changeArr(arr);     
		File file1=new File(writeUrl);                                                                                                             
		OutputStreamWriter write = null;                                                                                                           
		try{
			write = new OutputStreamWriter(new FileOutputStream(file1),"UTF-8");//考虑到编码格式        
			BufferedWriter bufferedWriter = new BufferedWriter(write);                                                                             
			 if (!file1.exists()) {
				    file1.createNewFile();
				   }                                                                                                
			 for(int i=0;i<arr.length;i++)                                                          											   
			 {
				 StringBuffer sb=new StringBuffer();
				 for(double ar:arr[i])
				 {
					 sb.append(String.format("%.1f",ar*100)+"%");
					 sb.append(",");
				 }
				 bufferedWriter.write(sb.substring(0,sb.length()-1));
				 bufferedWriter.newLine();
			 }
			 bufferedWriter.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		new R_NearState();
	}
}
