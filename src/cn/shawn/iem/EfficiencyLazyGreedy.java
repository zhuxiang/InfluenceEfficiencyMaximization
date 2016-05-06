/**
 * 
 */
package cn.shawn.iem;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

/**
 * @author zhuxiang
 * An algorithm based on lazy greedy to solve influence efficiency maximization problem.
 */
public class EfficiencyLazyGreedy {

	/**
	 * @author zhuxiang
	 * A class to record the marginal gain and iteration of a node.
	 */
	public class Node {
		private String name;
		private Double marginalGain;
		private Integer iteration;
		
		public Node(String name, Double mg, Integer i) {
			this.name = name;
			this.marginalGain = mg;
			this.iteration = i;
		}
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public Double getMarginalGain() {
			return marginalGain;
		}
		public void setMarginalGain(Double marginalGain) {
			this.marginalGain = marginalGain;
		}
		public Integer getIteration() {
			return iteration;
		}
		public void setIteration(Integer iteration) {
			this.iteration = iteration;
		}
		
	}
	
	public ArrayList<String> lazyGreedy(DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> g, int k, int r, String model) {
		ArrayList<String> s = new ArrayList<>();
		PriorityQueue<Node> pQueue = new PriorityQueue<Node>(new Comparator<Node>() {
			@Override
			public int compare(Node n1, Node n2) {
				return (n2.getMarginalGain()).compareTo(n1.getMarginalGain());
			}
		});
		
		Integer iteration = 1;
		for (String vertex : g.vertexSet()) {
			ArrayList<String> tmp = new ArrayList<>();
			tmp.add(vertex);
			Double eff = Utility.calcInfluenceEfficiencyMCMultiThreadIC(g, tmp, model, r);
			Node node = new Node(vertex, eff, 1);
			pQueue.add(node);
		}
		
		while (iteration <= k && !pQueue.isEmpty()) {
			Node u = pQueue.remove();
			if (u.getIteration() == iteration) {
				s.add(u.getName());
				iteration++;
			}
			else {
				ArrayList<String> tmpS = (ArrayList<String>)s.clone();
				tmpS.add(u.getName());
				Double eff = Utility.calcInfluenceEfficiencyMCMultiThreadIC(g, tmpS, model, r);
				Node updateU = new Node(u.getName(), eff, iteration);
				pQueue.add(updateU);
			}
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
		EfficiencyLazyGreedy effLazyGreedy = new EfficiencyLazyGreedy();
		int k = Integer.parseInt(args[2]);
		int r = 20000;
		long startTime = System.currentTimeMillis();
		ArrayList<String> s = effLazyGreedy.lazyGreedy(dirWgtGph, k, r, model);
		long endTime = System.currentTimeMillis();
		double runTimeSec = (endTime - startTime)/1000.0;
		System.out.println("LazyGreedy: k = " + k + ", r = " + r + ", runtime = " + runTimeSec + " secs.");
		System.out.println(s);
		Integer times = new Integer(200);
		Double efficiency = Utility.calcInfluenceEfficiencyMCMultiThreadIC(dirWgtGph, s, model, times);
		System.out.println("efficiency = " + efficiency + ".");
		Utility.getThreadPool().shutdown();
	}

}
