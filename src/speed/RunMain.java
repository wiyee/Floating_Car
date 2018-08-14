package speed;

import speed.args.ArgsMain;
import speed.args.UsedRoadMain;
import speed.group.Group_Main;
import speed.nearRoad.Road_Main;
import speed.nearState.RoadSet_Main;
import speed.nearState.State_Main;
import speed.traffic.ExtractSqlData;
import speed.traffic.Traffic_Main;


public class RunMain {

	public static void main(String[] args) {
		
		/**
		 * 运行Trafic_Main 计算各个路段，每个时间间隔内的4个交通属性值
		 */
		long start_time = System.currentTimeMillis();
		new Traffic_Main();
		long traffic_time = System.currentTimeMillis();
		System.out.println("Traffic_Main time:" + (traffic_time-start_time)/1000f/60f + "min");
		
		/**
		 * 运行Road_Main 计算每条路段的top-k相关路段
		 */
		new Road_Main();
		long nearroad_time = System.currentTimeMillis();
		System.out.println("Road_Main time:"+(nearroad_time-traffic_time)/1000f/60f+"min");
		
		/**
		 * 将traffic_main 产生的结果中的4条交通属性值分别保存在4个表中
		 */
		ExtractSqlData.extractData("TRAFFIC_5", "SPEED_5", "SPEED", 0);
		ExtractSqlData.extractData("TRAFFIC_5", "GCL_5", "GCL", 1);
		ExtractSqlData.extractData("TRAFFIC_5", "INDATAS_5", "INDATAS", 0);
		ExtractSqlData.extractData("TRAFFIC_5", "OUTDATAS_5", "OUTDATAS", 0);
		
		ExtractSqlData.extractData("TRAFFIC_10", "SPEED_10", "SPEED", 0);
		ExtractSqlData.extractData("TRAFFIC_10", "GCL_10", "GCL", 1);
		ExtractSqlData.extractData("TRAFFIC_10", "INDATAS_10", "INDATAS", 0);
		ExtractSqlData.extractData("TRAFFIC_10", "OUTDATAS_10", "OUTDATAS", 0);
		
		ExtractSqlData.extractData("TRAFFIC_15", "SPEED_15", "SPEED", 0);
		ExtractSqlData.extractData("TRAFFIC_15", "GCL_15", "GCL", 1);
		ExtractSqlData.extractData("TRAFFIC_15", "INDATAS_15", "INDATAS", 0);
		ExtractSqlData.extractData("TRAFFIC_15", "OUTDATAS_15", "OUTDATAS", 0);
		
		long extract_time = System.currentTimeMillis();
		System.out.println("extract sql data time:" + (extract_time-nearroad_time)/1000f/60f + "min");
		
		/**
		 * near traffic attribute 计算每条道路的top-M个相关交通属性
		 */
		new RoadSet_Main();
		new State_Main();
		long state_time = System.currentTimeMillis();
		System.out.println("near traffic attribute time:" + (state_time-extract_time) + "min");
		
		/**
		 * Group_Main 计算jenks聚类的聚类点
		 */
		new Group_Main();
		long group_time = System.currentTimeMillis();
		System.out.println("group_main time:"+(group_time-extract_time)/1000f/60f+"min");
		
		/**
		 * argsMain 利用PSO算法求解jenks聚类中的各参数值
		 */
		new UsedRoadMain();
		new ArgsMain();
		long args_time = System.currentTimeMillis();
		System.out.println("argsMain time:" + (args_time-group_time)/1000f/60f+"min");
		
	}

}
