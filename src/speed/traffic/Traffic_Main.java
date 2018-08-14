package speed.traffic;

import java.sql.Timestamp;

import dbTable.TRAFFIC;
import speed.Super_Main;
import speed.traffic.Traffic_Run;
import jdbc.Input_Car;
import myTools.StaticMethod;


public class Traffic_Main extends Super_Main
{
	
	@Override
	public void run() {
		
		for(int i=5;i<=15;i+=5)   //预测间隔分钟
		{
			segment=i*60*1000;
			forward=i*60*1000;
			for(int d=1;d<=3;d++)    //3天
			{
				fromTime="2015-02-0"+d+" 00:00:00";
				toTime=Timestamp.valueOf("2015-02-0"+(d+1)+" 00:00:00").getTime();
				startTime = fromTime;
				endTime = StaticMethod.nextTime(fromTime, segment);
				Input_Car.TABLENAME="TMP_GPS"+d;
				
//				System.out.println("fromTime:"+fromTime);//2015-02-01 00:00:00
//				System.out.println("toTime"+toTime);//1422806400000  2015-02-02 00:00:00
//				System.out.println("endTime"+endTime);//2015-02-01 00:05:00
				
				while(toTime-Timestamp.valueOf(endTime).getTime()>=0)
			    {
			//		this.tt.showTime("*************等待时间");
					System.out.println();
					System.out.println("*************时间间隔："+i);
					System.out.println("*************第"+d+"天");
					System.out.println("*************当前时间："+endTime);
					Traffic_Run sCoins=new Traffic_Run(startTime, endTime, segment);
					System.out.println("近邻的比例："+sCoins.getFindRate());
					sCoins.start(startTime,endTime,i);
					updateTime(); //时间更新为下一个时间间隔
					this.tt.showTime("计算速度花费的时间");
			    }
			}
		}
	}
	
	public static void main(String[] args) {
		long sTime = System.currentTimeMillis();
		new Traffic_Main();
		long eTime = System.currentTimeMillis();
		System.out.println("Time:" + (eTime-sTime)/1000f/60f + "min");
	}
}
