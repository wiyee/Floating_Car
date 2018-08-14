package speed;

import myTools.StaticMethod;
import myTools.TestTime;


public abstract class Super_Main  
{
	public String fromTime;
    public long toTime;
    public int segment;  //回滚的时间
    public int forward;  //向前更新时间
    
    public TestTime tt;
    public String startTime;
    public String endTime;
    
	public Super_Main() 
	{
		this.tt = new TestTime();
		run();
	}
	
	public abstract void run();
	
	public void updateTime()
	{
	    this.startTime = StaticMethod.nextTime(this.startTime, forward);
	    this.endTime = StaticMethod.nextTime(this.endTime, forward); 
	}
	
	
}
