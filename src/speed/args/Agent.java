package speed.args;

import java.util.Map;
import java.util.Random;

import myTools.StaticMethod;
import myTools.StaticValue;
import pojo.ArgOne;
import pojo.Traffic;

class Agent {
	
	static int s;  //比上一次好的粒子个数
	static double w = 0.7;
	static ArgOne gbest=new ArgOne();
	static double m_dBestFitness;
	static Map<Long,Traffic> nrTfMap;
	static Map<Long,Traffic> cuTfMap;
	
	
	public int iAgentDim;
	
	private final double delta1 = 1;
	private final double delta2 = 1;
	public ArgOne dpos ; // 粒子的位置
	public ArgOne dpbest ; // 粒子本身的最优位置
	public ArgOne dv ; // 粒子的速度
	private double m_dFitness;
	public double m_dBestfitness=Double.MAX_VALUE; // m_dBestfitness 粒子本身的最优解
	private Random random = new Random();
	
	
	public Agent() // 对粒子的位置和速度进行初始化
	{
		dpos = new ArgOne(); // 粒子的位置
    	dpbest = new ArgOne(); // 粒子本身的最优位置
    	dv = new ArgOne(); // 粒子的速度
    	
    	for(int i=0;i<StaticValue.groupNum;i++)
    	{
    		dv.aList_speed[i]=dpbest.aList_speed[i]=dpos.aList_speed[i]=random.nextDouble();
    		dv.bList_speed[i]=dpbest.bList_speed[i]=dpos.bList_speed[i]=random.nextDouble()*2;
    		dv.cList_speed[i]=dpbest.cList_speed[i]=dpos.cList_speed[i]=random.nextDouble()*2;
    		dv.dList_speed[i]=dpbest.dList_speed[i]=dpos.dList_speed[i]=random.nextDouble()*2;
    	}
	}
	

	public void UpdateFitness() {

//		double sum1 = 0;
//		double sum2 = 0;
		// 计算Ackley 函数的值
//		for (int i = 0; i < iAgentDim; i++) {
//			sum1 += dpos[i] * dpos[i];
//			sum2 += Math.cos(2 * Math.PI * dpos[i]);
//		}
		// m_dFitness 计算出的当前值
//		m_dFitness = -20 * Math.exp(-0.2 * Math.sqrt((1.0 / iAgentDim) * sum1))
//				- Math.exp((1.0 / iAgentDim) * sum2) + 20 + Math.E;
		m_dFitness=ArgsMain.calculateVal(dpos);
		if (m_dFitness < m_dBestfitness) {
			m_dBestfitness = m_dFitness;
			s+=0.5;
			if(m_dBestfitness< m_dBestFitness)
			{
				s+=0.5;
			}
			copyArgOne(dpbest, dpos);
		}
	}

	//更新粒子
	public void UpdatePos() {
		for(int i=0;i<StaticValue.groupNum;i++)
    	{
    		dv.aList_speed[i]=w * dv.aList_speed[i] +  delta1 * random.nextDouble()
    							* (dpbest.aList_speed[i] - dpos.aList_speed[i]) + delta2 * random.nextDouble()
    							* (gbest.aList_speed[i] - dpos.aList_speed[i]);
    		dpos.aList_speed[i] += dv.aList_speed[i];
    		
    		dv.bList_speed[i]=w * dv.bList_speed[i] +  delta1 * random.nextDouble()
					* (dpbest.bList_speed[i] - dpos.bList_speed[i]) + delta2 * random.nextDouble()
					* (gbest.bList_speed[i] - dpos.bList_speed[i]);
    		dpos.bList_speed[i] += dv.bList_speed[i];
    		
    		dv.cList_speed[i]=w * dv.cList_speed[i] +  delta1 * random.nextDouble()
					* (dpbest.cList_speed[i] - dpos.cList_speed[i]) + delta2 * random.nextDouble()
					* (gbest.cList_speed[i] - dpos.cList_speed[i]);
    		dpos.cList_speed[i] += dv.cList_speed[i];
    		
    		dv.dList_speed[i]=w * dv.dList_speed[i] +  delta1 * random.nextDouble()
					* (dpbest.dList_speed[i] - dpos.dList_speed[i]) + delta2 * random.nextDouble()
					* (gbest.dList_speed[i] - dpos.dList_speed[i]);
    		dpos.dList_speed[i] += dv.dList_speed[i];
    	}
	}
	
	
	public void clear()
	{
		s=0;
	}
	
	public static void copyArgOne(ArgOne ao1, ArgOne ao2)
	{
		for(int i=0;i<StaticValue.groupNum;i++)
    	{
			ao1.aList_speed[i]=ao2.aList_speed[i];
			ao1.bList_speed[i]=ao2.bList_speed[i];
			ao1.cList_speed[i]=ao2.cList_speed[i];
			ao1.dList_speed[i]=ao2.dList_speed[i];
    	}
	}
	

}