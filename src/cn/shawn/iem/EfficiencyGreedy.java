/**
 * 
 */
package cn.shawn.iem;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

/**
 * @author zhuxiang
 *
 */
public class EfficiencyGreedy {
	
	private DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> generateSubgraph(DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> g, String model) {
		DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> subGraph = (DefaultDirectedWeightedGraph<String, DefaultWeightedEdge>)g.clone();
		long currentTime = System.currentTimeMillis();
		Random randomFlip = new Random(currentTime);
		Set<DefaultWeightedEdge> edgeSet = g.edgeSet();
		switch (model) {
		case "wic":
			for (DefaultWeightedEdge edge : edgeSet) {
				Double flip = randomFlip.nextDouble();
				int inDegree = g.inDegreeOf(g.getEdgeTarget(edge));
				if (flip > 1.0 / inDegree) {
					subGraph.removeEdge(edge);
				}
			}
			break;
		
		case "uic":
			for (DefaultWeightedEdge edge : edgeSet) {
				Double flip = randomFlip.nextDouble();
				if (flip > 0.01) {
					subGraph.removeEdge(edge);
				}
			}
			break;
			
		default:
			break;
		}
		
		return subGraph;
	}
	/**
	 * Greedy algorithm to solve the problem.
	 * @param g
	 * @param k
	 * @param r
	 * @return
	 */
	public ArrayList<String> greedy(DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> g, int k, int r, String model) {
		ArrayList<String> s = new ArrayList<>();
		ArrayList<DefaultDirectedWeightedGraph<String, DefaultWeightedEdge>> subgraphArray = new ArrayList<>();
		for (int i = 0; i < r; i++) {
			subgraphArray.add(this.generateSubgraph(g, model));
		}
		
		
		int n = g.vertexSet().size();
		String[] vertexArr = new String[n];
		g.vertexSet().toArray(vertexArr);
		for (int i = 0; i < k; i++) {
			Double maxEff = 0.0;
			String maxVertex = new String("-1");
			for (String vertex : vertexArr) {
				if (!s.contains(vertex)) {
					Double effSum = 0.0;
					ArrayList<String> tmpS = (ArrayList<String>)s.clone();
					tmpS.add(vertex);
					for (DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> subGraph : subgraphArray) {
						effSum += Utility.simulateInfluenceEfficiency(subGraph, tmpS, model, 1);
					}
					if (effSum > maxEff) {
						maxEff = effSum;
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
		int k = 50;
//		int n = dirWgtGph.vertexSet().size();
//		Double samplingCnt = n*Math.log(Double.parseDouble(Integer.toString(n)));
		int r = 20000;
		long startTime = System.currentTimeMillis();
		ArrayList<String> s = effGreedy.greedy(dirWgtGph, k, r, model);
		long endTime = System.currentTimeMillis();
		double runTimeSec = (endTime - startTime)/1000.0;
		System.out.println("Greedy: k = " + k + ", r = " + r + ", runtime = " + runTimeSec + " secs.");
		System.out.println(s);
		Integer times = new Integer(100);
		Double efficiency = Utility.simulateInfluenceEfficiency(dirWgtGph, s, model, times);
		System.out.println("efficiency = " + efficiency + ".");
	}

}
