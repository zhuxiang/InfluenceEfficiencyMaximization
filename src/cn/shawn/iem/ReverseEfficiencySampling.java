/**
 * 
 */
package cn.shawn.iem;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

/**
 * @author zhuxiang
 *
 */
public class ReverseEfficiencySampling {
	private HashMap<String, Double> generateReverseReachableMap(DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> g, String v, Random randomFlip, String model){
		HashMap<String, Double> rrMap = new HashMap<>();
		HashSet<String> activedVertexSet = new HashSet<>();
		// initialize reverse reachable dequeue
		ArrayDeque<String> sDeque = new ArrayDeque<String>();
		sDeque.addLast(v);
		activedVertexSet.add(v);
		rrMap.put(v, 1.0);
		switch (model) {
		case "wic":
			while (sDeque.size() > 0) {
				String vertex = sDeque.removeFirst();
				Set<DefaultWeightedEdge> incomingEdges = g.incomingEdgesOf(vertex);
				int inDegree = g.inDegreeOf(vertex);
				for (DefaultWeightedEdge incomingEdge : incomingEdges) {
					String sourceVertex = g.getEdgeSource(incomingEdge);
					if (!activedVertexSet.contains(sourceVertex)) {
						double flip = randomFlip.nextFloat();
						if (flip < 1.0 / inDegree) {
							activedVertexSet.add(sourceVertex);
							sDeque.addLast(sourceVertex);
							double distance = rrMap.get(vertex) + g.getEdgeWeight(incomingEdge);
							if (!rrMap.containsKey(sourceVertex) || rrMap.containsKey(sourceVertex) && distance < rrMap.get(sourceVertex)) {
								rrMap.put(sourceVertex, distance);
							}
						}
					}
				}
			}
			break;
		case "uic":
			double probability = 0.01;
			while (sDeque.size() > 0) {
				String vertex = sDeque.removeFirst();
				Set<DefaultWeightedEdge> incomings = g.incomingEdgesOf(vertex);
				for (DefaultWeightedEdge incomingEdge : incomings) {
					String sourceVertex = g.getEdgeSource(incomingEdge);
					if (!activedVertexSet.contains(sourceVertex)) {
						double flip = randomFlip.nextFloat();
						if (flip < probability) {
							activedVertexSet.add(sourceVertex);
							double distance = rrMap.get(vertex) + g.getEdgeWeight(incomingEdge);
							if (!rrMap.containsKey(sourceVertex) || rrMap.containsKey(sourceVertex) && distance < rrMap.get(sourceVertex)) {
								rrMap.put(sourceVertex, distance);
							}
						}
					}
				}
			}
			break;
		default:
			break;
		}
		
		return rrMap;
	}
	
	public ArrayList<String> calculateSourceSet(DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> g, int k, int r, String model) {
		// initialize seed set s
		ArrayList<String> s = new ArrayList<>();
		// initialize distance map
		HashMap<Integer, Double> distanceMap = new HashMap<>();

		int n = g.vertexSet().size();
		String[] vertexArr = new String[n];
		g.vertexSet().toArray(vertexArr);
		
		long currentTime = System.currentTimeMillis();
		Random randomGenerator = new Random(currentTime);
		
		currentTime = System.currentTimeMillis();
		Random randomFlip = new Random(currentTime);
		// initialize reverse map array for the reverse maps.
		ArrayList<HashMap<String, Double>> rrMapArr = new ArrayList<>();
		for (int i = 0; i < r; i++) {
			String v = vertexArr[randomGenerator.nextInt(n)];
			rrMapArr.add(this.generateReverseReachableMap(g, v, randomFlip, model));
			distanceMap.put(i, Double.POSITIVE_INFINITY);
		}
		
		for (int i = 0; i < k; i++) {
			HashMap<String, Double> eff = new HashMap<>();
			for (int j = 0; j < r; j++) {
				HashMap<String, Double> rrMap = rrMapArr.get(j);
				for (String u : rrMap.keySet()) {				
					if (!s.contains(u)) {
						if (rrMap.get(u) < distanceMap.get(j)) {
							double marginalGain = 1.0 / rrMap.get(u) - 1.0 / distanceMap.get(j);
							if (eff.containsKey(u)) {
								eff.put(u, eff.get(u) + marginalGain);
							} else {
								eff.put(u, marginalGain);
							}
						} 
					}
				}
			}
			
			double maxEff = 0.0;
			String maxNode = new String("-1");
			for (String node : eff.keySet()) {
				if (eff.get(node) > maxEff) {
					maxEff = eff.get(node);
					maxNode = node;
				}
			}
			s.add(maxNode);	
		}
		
		return s;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		ArrayList<Integer> arr = new ArrayList<>();
//		for (int i = 0; i < 100; i++) {
//			arr.add(i);
//		}
//		System.out.println(arr.get(0));
		String fileName = args[0];
		String model = args[1];
		DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> dirWgtGph = Utility.loadGraph(fileName);
		ReverseEfficiencySampling res = new ReverseEfficiencySampling();
		int k = 50;
		int n = dirWgtGph.vertexSet().size();
		Double samplingCnt = n*Math.log(Double.parseDouble(Integer.toString(n)));
		int r = samplingCnt.intValue();
		long startTime = System.currentTimeMillis();
		ArrayList<String> s = res.calculateSourceSet(dirWgtGph, k, r, model);
		long endTime = System.currentTimeMillis();
		double runTimeSec = (endTime - startTime)/1000.0;
		System.out.println("RES algorithm: k = " + k + ", r = " + r + ", runtime = " + runTimeSec + " secs.");
		System.out.println(s);
		Integer times = new Integer(100);
		Double efficiency = Utility.simulateInfluenceEfficiency(dirWgtGph, s, model, times);
		System.out.println("efficiency = " + efficiency + ".");
	}

}
