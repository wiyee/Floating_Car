package myTools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Jenks {
	
	List<Double> dataList=null;
	List<Integer> valueList=null;
	int nbClass;
	int dataLen;
	double[][] mat1;
	double[][] mat2;
	double norm=0;
	
	public Jenks(List<Double> dataList,List<Integer> valueList, int nbClass) {
		this.dataList=dataList;
		this.valueList=valueList;
		this.dataLen=dataList.size();
//		System.out.println(dataList);
//		System.out.println(valueList);
//		System.out.println(valueList.size());
		our_Init(nbClass);
//		Collections.sort(this.dataList);
	}
	
	public Jenks(List<Double> dataList,List<Integer> valueList) {
		this.dataList=dataList;
		this.valueList=valueList;
		this.dataLen=dataList.size();
	}
	
	public double getV_Our(int clssNum) 
	{
		our_Init(clssNum);
		norm=1;
		return (double) getJenks_Our(clssNum)[1];
	}
	
	public double getV_MOJO(int clssNum)
	{
		nbClass=clssNum;
		return getV(getList(getJenks_MOJO()));
	}
	
	public double getV_Origin(int clssNum)
	{
		return getJenks_Origin(clssNum-1,0,dataLen);
	}
	
	public Object[] getJenks_Our(int clssNum)
	{
		List<Double> kList=new ArrayList<Double>();
		// fill the kclass (classification) array with zeros:
		double[] kclass = new double[clssNum+1];

		

		// this is the last number in the array:
//		System.out.println(dataList);
//		System.out.println(dataLen);
		kclass[clssNum] = dataList.get(dataLen - 1);
		// this is the first number - can set to zero, but want to set to lowest
		// to use for legend:
		kclass[0] = dataList.get(0);
		int countNum = clssNum;
		int k=dataLen;
		while (countNum >= 2) {
			int id = (int)mat1[k][countNum] - 2;
			kList.add(0, dataList.get(id));
			kclass[countNum - 1] = dataList.get(id);
			k = (int)mat1[k][countNum] - 1;
			// spits out the rank and value of the break values:
			// console.log("id="+id,"rank = " + String(mat1[k][countNum]),"val =
			// " + String(dataList[id]))
			// count down:
			countNum -= 1;
		}
		// check to see if the 0 and 1 in the array are the same - if so, set 0
		// to 0:
		if (kclass[0] == kclass[1]) {
			kclass[0] = 0;
		}

		return new Object[]{kList, mat2[dataLen][clssNum]/norm}; //array of breaks
		
	}
	
	private void our_Init(int nbClass)
	{
		this.nbClass=nbClass;
		mat1=new double[dataLen+1][nbClass+1];//BP
		mat2=new double[dataLen+1][nbClass+1];//SV
		
		for(int y=1;y<nbClass+1;y++)
		{
			mat1[0][y]=1;
			mat2[0][y]=0;
			for (int t = 1; t < dataLen+1; t++) {
				mat2[t][y] = Double.MAX_VALUE;
			}
		}
		
		double v=0;
		
		for ( int l = 2; l < dataLen+1; l++) {
			double s1 = 0.0;   //均值
			double s2 = 0.0;   //平方和
			double w = 0.0;    //个数
			for ( int m = 1; m < l+1; m++) {
				int i3 = l - m + 1;
				double val = dataList.get(i3 - 1);
				int num=valueList.get(i3-1);
				s2 += val * val *num;
				s1 += val*num;
				w += num;
				v = s2 - (s1 * s1) / w;   //方差
				int i4 = i3 - 1;
				if (i4 != 0) {
					for ( int p = 2; p < nbClass+1; p++) {
						if (mat2[l][p] >= (v + mat2[i4][p - 1])) {
							mat1[l][p] = i3;
							mat2[l][p] = v + mat2[i4][p - 1];
						}
					}
				}
			}
			mat1[l][1] = 1;
			mat2[l][1] = v;
//			System.out.println("BP:"+mat1[3][2]);
//			System.out.println("SV"+mat2[3][2]);
//			System.out.println(v);
		}
		norm=mat2[dataLen][2]/100;
	}
	
	private double getJenks_Origin(int num,int start,int end)
	{
		if(num==0)
		{
			List<Object[]> list_data=new ArrayList<Object[]>();
			List<Object[]> list_value=new ArrayList<Object[]>();
			Object[] objs1=new Object[end-start];
			Object[] objs2=new Object[end-start];
			for(int i=start;i<end;i++)
			{
				objs1[i-start]=dataList.get(i);
				objs2[i-start]=valueList.get(i);
			}
			list_data.add(objs1);
			list_value.add(objs2);
			double res=getV(new Clazz(list_data, list_value));
			return res;
		}
		double res=Double.MAX_VALUE;
		for(int i=0;i<end-start-num;i++)
		{
			res=Math.min(res, getJenks_Origin(0,start,i+start+1)+getJenks_Origin(num-1,i+start+1,end));
		}
		return res;
	}
	
	private double[] getJenks_MOJO()
	{
		List<Double> cut=new ArrayList<Double>();
		List<Double> orderCut=new ArrayList<Double>();
		
		for(int i=1;i<dataLen;i++)
		{
			double val=dataList.get(i)-dataList.get(i-1);
			cut.add(val);
			orderCut.add(val);
		}
		
		Collections.sort(orderCut);
		List<Integer> indexList=new ArrayList<Integer>();
		for(int i=1;i<nbClass;i++)
		{
			indexList.add(cut.indexOf(orderCut.get(orderCut.size()-i)));
		}
		Collections.sort(indexList);
		
		// fill the kclass (classification) array with zeros:
		double[] kclass = new double[nbClass+1];

		// this is the last number in the array:
		kclass[nbClass] = dataList.get(dataLen - 1);
		// this is the first number - can set to zero, but want to set to lowest
		// to use for legend:
		kclass[0] = dataList.get(0);

		for(int i=0;i<indexList.size();i++)
		{
			kclass[i+1]=dataList.get(indexList.get(i));
		}
		
		return kclass; //array of breaks
	}
	
	
	
	private Clazz getList(double[] kclass)
	{
		List<Object[]> res=new ArrayList<Object[]>();
		List<Object[]> res2=new ArrayList<Object[]>();
		List<Double> list=new ArrayList<Double>();
		List<Integer> list2=new ArrayList<Integer>();
		int idx=0;
		for(int i=1;i<kclass.length;i++)
		{
			while(idx<dataLen)
			{
				Double data=dataList.get(idx);
				Integer v=valueList.get(idx);
				idx++;
				if(data<=kclass[i])
				{
					list.add(data);
					list2.add(v);
				}else
				{
					res.add(list.toArray());
					list=new ArrayList<Double>();
					list.add(data);
					
					res2.add(list2.toArray());
					list2=new ArrayList<Integer>();
					list2.add(v);
					break;
				}
			}
		}
		res.add(list.toArray());
		res2.add(list2.toArray());
		return new Clazz(res,res2);
	}
	
	private double getV(Clazz kArr)
	{
		double res=0;
		List<Object[]> objsList1=kArr.list_data;
		List<Object[]> objsList2=kArr.list_value;
		for(int i=0;i<objsList1.size();i++)
		{
			Object[] objs1=objsList1.get(i);
			Object[] objs2=objsList2.get(i);
			double s1 = 0.0;   //均值
			double s2 = 0.0;   //平方和
			double w = 0.0;    //个数
			for(int j=0;j<objs1.length;j++)
			{
				Double d=(Double)objs1[j];
				Integer n=(Integer)objs2[j];
				s2 += d * d*n;
				s1 += d*n;
				w += n;
			}
			double v = s2 - (s1 * s1) / w;   //方差
			res+=v;
		}
		return res;
	}
	
//	public static void main(String[] args) {
//		Double[] dataArr=new Double[]{1.0,3.0,5.0,7.0,9.0};
//		List<Double> dataList=Arrays.asList(dataArr);
//		Integer[] dat1 = new Integer[]{2,2,2,2,2};
//		List<Integer> dataL = Arrays.asList(dat1);
//		Jenks jenks = new Jenks(dataList, dataL);
//		jenks.getV_Our(3);
//		System.out.println(1);
		
//		Jenks m=new Jenks(dataList,4);
//		double[] kclass=m.getClassJenks();
//		List<Object[]> kArr=m.getList(kclass);
//		double v=m.getV(kArr);
//		
//		
//		double[] kclass2=m.getClassJenks2();
//		List<Object[]> kArr2=m.getList(kclass2);
//		double v2=m.getV(kArr2);
//		System.out.println();
//	}
}

class Clazz{
	List<Object[]> list_data;
	List<Object[]> list_value;
	public Clazz(List<Object[]> list_data, List<Object[]> list_value) {
		super();
		this.list_data = list_data;
		this.list_value = list_value;
	}
}
