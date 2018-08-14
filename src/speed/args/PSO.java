package speed.args;

import pojo.ArgOne;

class PSO {
	
	private Agent[] agent;
	private int iStep; // 迭代次数
	private int n;
	private int m_iTempPos;
	
	public PSO(int n, int iter) {
		iStep=(int) Math.pow(2, iter);
		Agent.m_dBestFitness = Double.MAX_VALUE;
		this.n=n;
		agent = new Agent[n];
		for (int i = 0; i < n; i++){
			agent[i] = new Agent();
		}
	}

//	public void Initialize() {
//		for (int i = 0; i < Agent.iPOSNum; i++) {
//			agent[i].m_dBestfitness = 10000;
//			agent[i].UpdateFitness();
//		}
//	}

	public void Search() {
		int k = 0;
		while (k < iStep) {
//			System.out.print(k+",");
			m_iTempPos = -1;
			synchronized(PSO.class)
			{
				for (int i = 0; i < n; i++) {
					if (agent[i].m_dBestfitness < Agent.m_dBestFitness) {
						Agent.m_dBestFitness = agent[i].m_dBestfitness;
						m_iTempPos = i;
					}
				}
				if (m_iTempPos != -1) {
					Agent.copyArgOne(Agent.gbest, agent[m_iTempPos].dpbest);
				}
			}
			for (int i = 0; i < n; i++) {
				agent[i].UpdateFitness();
			}
//			Agent.update();
			for(int i = 0; i < n; i++) {
				agent[i].UpdatePos();
			}
//			Agent.clear();
			k++;
		}
//		System.out.println("After " + k + " steps " + "the best value is "
//				+ m_dBestFitness);
//		System.out.print("The best position is :");
//		for (int i = 0; i < Agent.iAgentDim; i++) {
//			System.out.print(Agent.gbest[i] + " ");
//		}
	}
	
	public ArgOne getX()
	{
		return Agent.gbest;
	}

}