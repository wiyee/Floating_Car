package pojo;

public class NearState {
	public int linkid;
	public double[] weightState;
	public int[] symbol;
	public NearState(int linkid, String weightState, String symbol) {
		super();
		this.linkid = linkid;
		String[] wArr=weightState.split(",");
		this.weightState=new double[wArr.length];
		for(int i=0;i<wArr.length;i++)
		{
			this.weightState[i]=Double.parseDouble(wArr[i]);
		}
		
		String[] sArr=symbol.split(",");
		this.symbol=new int[sArr.length];
		for(int i=0;i<sArr.length;i++)
		{
			this.symbol[i]=Integer.parseInt(sArr[i]);
		}
	}
	
}
