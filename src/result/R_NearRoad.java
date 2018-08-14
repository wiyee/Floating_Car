package result;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import myTools.StaticMethod;
import pojo.NearRoad;

public class R_NearRoad {
	List<NearRoad> nrList=new ArrayList<NearRoad>();
	//6个类——5个值
	double[][] arr=null;
	public R_NearRoad() {
		for(int i=5;i<=15;i+=5)
		{
			nrList=StaticMethod.getNearRoadList(i);
			arr=new double[6][5];
			for(NearRoad nr:nrList)
			{
				double v1=getV(nr.WEIGHTS_1);
				int n1=getNum(v1);
				arr[0][n1]++;
				
				double v2=getV(nr.WEIGHTS_2);
				int n2=getNum(v2);
				arr[1][n2]++;
				
				
				double v3=getV(nr.WEIGHTS_3);
				int n3=getNum(v3);
				arr[2][n3]++;
				
				double v4=getV(nr.WEIGHTS_4);
				int n4=getNum(v4);
				arr[3][n4]++;
				
				double v5=getV(nr.WEIGHTS_5);
				int n5=getNum(v5);
				arr[4][n5]++;
				
				double v6=getV(nr.WEIGHTS_6);
				int n6=getNum(v6);
				arr[5][n6]++;
			}
			double sum=nrList.size();
			for(int m=0;m<arr.length;m++)
			{
				for(int n=0;n<arr[0].length;n++)
				{
					arr[m][n]/=sum;
				}
			}
			String url="C:\\Users\\Administrator\\Desktop\\最新成果\\数据\\参数选取\\road\\score_"+i+".txt";
			writeFile(url);
		}
	}
	
	private double getV(String str)
	{
		String[] w2=str.split(",");
		double res=0;
		for(String w:w2)
		{
			res+=Double.parseDouble(w);
		}
		return res;
	}
	
	private int getNum(double v){
		if(v<0.5)
		{
			return 0;
		}
		if(v<0.6)
		{
			return 1;
		}
		if(v<0.7)
		{
			return 2;
		}
		if(v<0.8)
		{
			return 3;
		}
		return 4;
	}
	
	
	private void writeFile(String writeUrl){
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
		new R_NearRoad();
	}
}
