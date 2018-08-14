package myTools;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

import pojo.SVMModel;

public class CW_SVM {
	
	private HashSet<Integer> boundAlpha = new HashSet<Integer>();
	private Random random = new Random();
	private double x[][];
	
	double a[];
	double y[];
	double b = 0.0;
	
	public SVMModel train(double[][] x,double[] y) {
		this.x=x;
		this.y=y;
		
		int maxPasses = 5; 
		
		double a[] = new double[x.length];
		this.a = a;
		
		for (int i = 0; i < x.length; i++) {
			a[i] = 0;
		}
		int passes = 0;
		
		
		while (passes < maxPasses) {
			System.out.println("passes="+passes);
			int num_changed_alphas = 0;
			for (int i = 0; i < x.length; i++) {
				double Ei = getE(i);
				if ((y[i] * Ei < -StaticValue.tol && a[i] < StaticValue.C) ||
					(y[i] * Ei > StaticValue.tol && a[i] > 0)) 
				{
					int j;
					if (this.boundAlpha.size() > 0) {
						j = findMax(Ei, this.boundAlpha);
					} else 
						j = RandomSelect(i);
					
					double Ej = getE(j);
					double oldAi = a[i];
					double oldAj = a[j];
					
					double L, H;
					if (y[i] != y[j]) {
						L = Math.max(0, a[j] - a[i]);
						H = Math.min(StaticValue.C, StaticValue.C - a[i] + a[j]);
					} else {
						L = Math.max(0, a[i] + a[j] - StaticValue.C);
						H = Math.min(0, a[i] + a[j]);
					}
					
					
					double eta = 2 * k(x[i], x[j]) - k(x[i], x[i]) - k(x[j], x[j]);//��ʽ(3)
					
					if (eta >= 0)
						continue;
					
					a[j] = a[j] - y[j] * (Ei - Ej)/ eta;//��ʽ(2)
					if (0 < a[j] && a[j] < StaticValue.C)
						this.boundAlpha.add(j);
					
					if (a[j] < L) 
						a[j] = L;
					else if (a[j] > H) 
						a[j] = H;
					
					if (Math.abs(a[j] - oldAj) < 1e-5)
						continue;
					a[i] = a[i] + y[i] * y[j] * (oldAj - a[j]);
					if (0 < a[i] && a[i] < StaticValue.C)
						this.boundAlpha.add(i);
					
					double b1 = b - Ei - y[i] * (a[i] - oldAi) * k(x[i], x[i]) - y[j] * (a[j] - oldAj) * k(x[i], x[j]);
					double b2 = b - Ej - y[i] * (a[i] - oldAi) * k(x[i], x[j]) - y[j] * (a[j] - oldAj) * k(x[j], x[j]);
					System.out.println("----------------------------");
					System.out.println("Ei:" + Ei);
					System.out.println("Ej:" + Ej);
					System.out.println("k:"+k(x[i], x[j]));
					System.out.println("b1:" + b1);
					System.out.println("b2:" + b2);
					System.out.println("----------------------------");
					if (!Double.isInfinite(b1) && !Double.isInfinite(b2)){
						if (0 < a[i] && a[i] < StaticValue.C)
							b = b1;
						else if (0 < a[j] && a[j] < StaticValue.C)
							b = b2;
						else
							b = (b1 + b2) / 2;
					}
					num_changed_alphas = num_changed_alphas + 1;
				}
			}
			if (num_changed_alphas == 0) {
				passes++;
			} else 
				passes = 0;
		}
		
		return new SVMModel(a, y, 0.0);
	}
	
	private int findMax(double Ei, HashSet<Integer> boundAlpha2) {
		double max = 0;
		int maxIndex = -1;
		for (Iterator<Integer> iterator = boundAlpha2.iterator(); iterator.hasNext();) {
			Integer j = (Integer) iterator.next();
			double Ej = getE(j);
			if (Math.abs(Ei - Ej) > max) {
				max = Math.abs(Ei - Ej);
				maxIndex = j;
			}
		}
		return maxIndex;
	}

//	private double[] predict(SVMModel model, double x[][]) {
//		int total = x.length;
//		double[] res=new double[total];
//		for (int i = 0; i < total; i++) {
//			int len = y.length;
//			double sum = 0;
//			for (int j = 0; j < len; j++) {
//				sum += y[j] * a[j] * k(x[j], x[i]);
//			}
//			sum += model.b;
//			res[i]=sum;
//		}
//		return res;
//	}
	
	public double predict(SVMModel model, double x[]) {
		int len = y.length;
		double sum = 0;
		for (int j = 0; j < len; j++) {
			sum += y[j] * a[j] * k(this.x[j], x);
		}
		sum += model.b;
		return sum;
	} 
	
	
	private double k(double[] x1, double[] x2) {
//		double sum=0.0;
		double sum = 1.0;
		double tmp=(2*StaticValue.kA*StaticValue.kA);
		for (int t = 0; t < x1.length; t++) {
//			sum+=x1[t]*x2[t];
			double cut=Math.abs(x1[t]-x2[t]);
			sum *= (1-Math.pow(cut,2)/StaticValue.kA)*Math.exp(-cut/tmp);
		}
		return sum;
	}


	private int RandomSelect(int i) {
		int j;
		do {
			j = random.nextInt(x.length);
		} while(i == j);
		return j;
	}



	private double f(int j) {
		double sum = 0;
		for (int i = 0; i < x.length; i++) {
			sum += a[i] * y[i] * k(x[i],x[j]); 
		}
		
		return sum + this.b;
	}

	private double getE(int i) {
		return f(i) - y[i];
	}

}
