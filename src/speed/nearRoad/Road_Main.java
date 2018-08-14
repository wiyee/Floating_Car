package speed.nearRoad;

import java.sql.Timestamp;

import speed.Super_Main;
import speed.nearRoad.Road_Run;
import jdbc.Input_Car;
import myTools.StaticMethod;
import myTools.TestTime;


public class Road_Main extends Super_Main
{
	
	@Override
	public void run() {
		for(int i=5;i<=5;i+=5)  
		{
			segment=i*60*1000;
			forward=i*60*1000;
			Road_Run.setInterval(i*60*1000);
			for(int d=1;d<3;d++)    //3天
			{
				fromTime="2015-02-0"+d+" 00:00:00";
				toTime=Timestamp.valueOf("2015-02-0"+(d+1)+" 00:00:00").getTime();
				startTime = fromTime;
				endTime = StaticMethod.nextTime(fromTime, segment);
				Input_Car.TABLENAME="TMP_GPS"+d;
				while(toTime-Timestamp.valueOf(endTime).getTime()>=0){
			//		this.tt.showTime("*************等待时间");
					System.out.println();
					System.out.println("*************时间间隔："+i);
					System.out.println("*************第"+d+"天");
					System.out.println("*************当前时间："+endTime);
					Road_Run sCoins=new Road_Run(startTime, endTime);
					System.out.println("近邻的比例："+sCoins.getFindRate());
					sCoins.run();
					updateTime(); 
					this.tt.showTime("计算速度花费的时间");
			    }
			}
			Road_Run.end(i);
		}
	}
	
	public static void main(String[] args) {
		Long start = System.currentTimeMillis();
		new Road_Main();
		Long end = System.currentTimeMillis();
		System.out.println("totalTime: "+(end-start)/1000f/60f/60f+" h");
		
	}
}
