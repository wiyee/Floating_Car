package dbTable.pojo;

import java.util.ArrayList;
import java.util.List;

public class NearState_POJO{
	int id;
	public int linkId;
	//speed:0  gcl:1  width:2  length:3  indata:4  outdata:5
	//交通状态——路段——diff1,diff2,...
	public List<List<List<Double>>> diff=new ArrayList<List<List<Double>>>();
	//负相关
	public List<List<List<Double>>> neDiff=new ArrayList<List<List<Double>>>();
	public List<Double> weightList=new ArrayList<Double>();
	public List<Integer> roadIdList=new ArrayList<Integer>();
	List<Double> rList=new ArrayList<Double>();
	List<Integer> symbolList=new ArrayList<Integer>();
	
	
	public String getWeightState()
	{
		StringBuffer res=new StringBuffer();
		for(double r:rList)
		{
			res.append(String.format("%.4f", r));
			res.append(",");
		}
		return res.substring(0,res.length()-1);
	}
	
	public String getSymbol()
	{
		StringBuffer res=new StringBuffer();
		for(int s:symbolList)
		{
			res.append(s);
			res.append(",");
		}
		return res.substring(0,res.length()-1);
	}
	
	public void finish()
	{
		double sumR=0;
		for(int i=0;i<diff.size();i++)
		{
			double r=0;
			double neR=0;
			List<List<Double>> outList=diff.get(i);
			List<List<Double>> neOutList=neDiff.get(i);
			for(int j=0;j<outList.size();j++)
			{
				double w=weightList.get(j);
				List<Double> inList=outList.get(j);
				List<Double> neInList=neOutList.get(j);
				double sum=0;
				double neSum=0;
				for(int k=0;k<inList.size();k++)
				{
					sum+=inList.get(k);
					neSum+=neInList.get(k);
				}
				r+=w*sum;
				neR+=w*neSum;
			}
			if(r>neR)
			{
				symbolList.add(1);
				rList.add(r);
				sumR+=r;
			}else
			{
				symbolList.add(-1);
				rList.add(neR);
				sumR+=neR;
			}
		}
		List<Double> resList=new ArrayList<Double>();
		for(int i=0;i<rList.size();i++)
		{
			resList.add(rList.get(i)/sumR);
		}
		rList=resList;
	}
	
	
	public NearState_POJO(int id, int linkId, String roads_5, String weights_5) {
		super();
		this.id = id;
		this.linkId = linkId;
		String[] wStrs=weights_5.split(",");
		for(String wStr:wStrs)
		{
			weightList.add(Double.parseDouble(wStr));
		}
		for(int i=0;i<4;i++)
		{
			List<List<Double>> outList=new ArrayList<List<Double>>();
			diff.add(outList);
			List<List<Double>> neOutList=new ArrayList<List<Double>>();
			neDiff.add(neOutList);
			for(int j=0;j<weightList.size();j++)
			{
				List<Double> inList=new ArrayList<Double>();
				List<Double> neInList=new ArrayList<Double>();
				outList.add(inList);
				neOutList.add(neInList);
			}
		}
		String[] rStrs=roads_5.split(",");
		for(String rStr:rStrs)
		{
			roadIdList.add(Integer.parseInt(rStr));
		}
	}
}
