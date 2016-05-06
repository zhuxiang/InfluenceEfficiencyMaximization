/**
 * 
 */
package cn.shawn.iem;

import java.util.ArrayList;
import java.util.Set;

import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

/**
 * @author zhuxiang
 *
 */
public class EfficiencyGreedy {

	/**
	 * Greedy algorithm to solve the problem.
	 * @param g
	 * @param k
	 * @param r
	 * @return
	 */
	public ArrayList<String> greedy(DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> g, int k, int r, String model) {
		ArrayList<String> s = new ArrayList<>();
		Set<String> vertexSet = g.vertexSet();
		for (int i = 0; i < k; i++) {
			Double maxEff = 0.0;
			String maxVertex = new String("-1");
			for (String vertex : vertexSet) {
				if (!s.contains(vertex)) {
					Double eff = 0.0;
					ArrayList<String> tmpS = (ArrayList<String>)s.clone();
					tmpS.add(vertex);
					eff = Utility.calcInfluenceEfficiencyMCMultiThreadIC(g, tmpS, model, r);
					if (eff > maxEff) {
						maxEff = eff;
						maxVertex = vertex;
					}
				}
			}
			s.add(maxVertex);
		}
		return s;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fileName = args[0];
		String model = args[1];
		DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> dirWgtGph = Utility.loadGraph(fileName);
		EfficiencyGreedy effGreedy = new EfficiencyGreedy();
		int k = Integer.parseInt(args[2]);
		int r = 20000;
		long startTime = System.currentTimeMillis();
		ArrayList<String> s = effGreedy.greedy(dirWgtGph, k, r, model);
		long endTime = System.currentTimeMillis();
		double runTimeSec = (endTime - startTime)/1000.0;
		System.out.println("Greedy: k = " + k + ", r = " + r + ", runtime = " + runTimeSec + " secs.");
		System.out.println(s);
		Integer times = new Integer(100);
		Double efficiency = Utility.calcInfluenceEfficiencyMCMultiThreadIC(dirWgtGph, s, model, times);
		System.out.println("efficiency = " + efficiency + ".");
		Utility.getThreadPool().shutdown();
	}

}
