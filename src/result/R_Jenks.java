package result;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pojo.Class_Number;
import myTools.Jenks;
import myTools.StaticMethod;
import myTools.StaticValue;

public class R_Jenks {
	
//	static int[] arr={100,250,500,750,1000,2500,5000,7500,9976};
	static int[] arr={100};
	static int[] clssNumArr={2,3,4,5,6,7,8,9,10};
	
	static double[][] our_ResArr=new double[arr.length][clssNumArr.length];
	static double[][] our_TimeArr=new double[arr.length][clssNumArr.length];
	
	static double[][] origin_ResArr=new double[arr.length][clssNumArr.length];
	static double[][] origin_TimeArr=new double[arr.length][clssNumArr.length];
	
	static double[][] mojo_ResArr=new double[arr.length][clssNumArr.length];
	static double[][] mojo_TimeArr=new double[arr.length][clssNumArr.length];
	
	static String path="C:\\Users\\wiyee\\Desktop\\最新成果\\Jenks\\";
	static String ourUrl=path+"Our\\";
	static String originUrl=path+"Origin\\";
	static String mojoUrl=path+"Mojo\\";
	
	static final int time=1000;
	
	public static void main(String[] args) {
//		Class_Number speeds=getSpeed5();
//		List<Double> dataList=speeds.getDataList();
//		List<Integer> valueList=speeds.getValueList();
		for(int i=0;i<arr.length;i++)
		{
			int num=arr[i];
			List<Double> datas=new ArrayList<Double>();
			List<Integer> values=new ArrayList<Integer>();
			for(int k=0;k<num;k++)
			{
				datas.add(k+0.0);
				values.add(1);
//				datas.add(dataList.get(k));
//				values.add(valueList.get(k));
			}
			Jenks jenk=new Jenks(datas, values);
			for(int j=0;j<clssNumArr.length;j++)
			{
				System.out.println();
				int clssNum=clssNumArr[j];
				System.out.println("num="+num+",clssNum="+clssNum+"------------");
				long sMs=System.currentTimeMillis();
//				double origin=jenk.getV_Origin(clssNum);
//				double originTime=(System.currentTimeMillis()-sMs)/StaticValue.Second;
//				System.out.println("origin值："+origin+",origin时间(s)："+originTime);
//				sMs=System.currentTimeMillis();
				double our=0;
				for(int t=0;t<time;t++)
				{
					our+=jenk.getV_Our(clssNum)/time;
				}
				double ourTime=(System.currentTimeMillis()-sMs);
				System.out.println("our值："+(our)+",our时间(ms)："+ourTime);
				sMs=System.currentTimeMillis();
				double mojo=0;
				for(int t=0;t<time;t++)
				{
					mojo+=jenk.getV_MOJO(clssNum)/time;
				}
				double mojoTime=(System.currentTimeMillis()-sMs);
				System.out.println("mojo值："+(mojo)+",mojo时间(ms)："+mojoTime);
				
//				origin_ResArr[i][j]=origin/origin;
//				origin_TimeArr[i][j]=originTime;
				our_ResArr[i][j]=our;
				our_TimeArr[i][j]=ourTime;
				mojo_ResArr[i][j]=mojo;
				mojo_TimeArr[i][j]=mojoTime;
			}
		}
		writeToFile(our_ResArr, ourUrl+"res.txt");
		writeToFile(our_TimeArr, ourUrl+"time.txt");
//		writeToFile(origin_ResArr, originUrl+"res.txt");
//		writeToFile(origin_TimeArr, originUrl+"time.txt");
		writeToFile(mojo_ResArr, mojoUrl+"res.txt");
		writeToFile(mojo_TimeArr, mojoUrl+"time.txt");
	}
	
	private static void writeToFile(double[][] res,String url) {
		File file1=new File(url);
		OutputStreamWriter write = null;
		try{
			write = new OutputStreamWriter(new FileOutputStream(file1),"UTF-8");//考虑到编码格式
			BufferedWriter bufferedWriter = new BufferedWriter(write);
			 if (!file1.exists()) {
				    file1.createNewFile();
				   }
			 for(double[] arr:res)
			 {
				 for(double d:arr)
				 {
					 bufferedWriter.write(d+",");
				 }
				 bufferedWriter.newLine();
			 }
			 bufferedWriter.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private static Class_Number getSpeed5() {
		String sql="select TRUNC(value,2),count(TRUNC(value,2)) from SPEED_5 where VALUE!=0 group by TRUNC(value,2) order by TRUNC(value,2)";
		return StaticMethod.getListFromDB(sql);
	}
	
}
