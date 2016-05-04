/**
 * 
 */
package cn.shawn.iem;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

/**
 * @author zhuxiang
 *
 */
public class ReverseInfluenceSampling {

	private HashSet<String> generateReverseReachableSet(DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> g, String v, Random randomFlip, String model) {
		HashSet<String> rrSet = new HashSet<>();
		ArrayDeque<String> sDeque = new ArrayDeque<>();
		rrSet.add(v);
		sDeque.push(v);
		switch (model) {
		case "wic":
			while (sDeque.size() > 0) {
				String vertex = sDeque.removeFirst();
				Set<DefaultWeightedEdge> incomingEdges = g.incomingEdgesOf(vertex);
				int inDegree = g.inDegreeOf(vertex);
				for (DefaultWeightedEdge incomingEdge : incomingEdges) {
					String sourceVertex = g.getEdgeSource(incomingEdge);
					if (!rrSet.contains(sourceVertex)) {
						double flip = randomFlip.nextFloat();
						if (flip < 1.0 / inDegree) {
							rrSet.add(sourceVertex);
							sDeque.addLast(sourceVertex);
						}
					}
				}
			}
			break;
			
		case "uic":
			Double probability = 0.01;
			while (sDeque.size() > 0) {
				String vertex = sDeque.removeFirst();
				Set<DefaultWeightedEdge> incomingEdges = g.incomingEdgesOf(vertex);
				for (DefaultWeightedEdge incomingEdge : incomingEdges) {
					String sourceVertex = g.getEdgeSource(incomingEdge);
					if (!rrSet.contains(sourceVertex)) {
						double flip = randomFlip.nextFloat();
						if (flip < probability) {
							rrSet.add(sourceVertex);
							sDeque.addLast(sourceVertex);
						}
					}
				}
			}
			break;
			
		default:
			break;
		}
		
		return rrSet;
	}
	
	public ArrayList<String> calculateSourceSet(DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> g, int k, int r, String model) {
		ArrayList<String> s = new ArrayList<>();
		HashSet<HashSet<String>> rrSets = new HashSet<>();
		
		int n = g.vertexSet().size();
		String[] vertexArr = new String[n];
		g.vertexSet().toArray(vertexArr);
		
		long currentTime = System.currentTimeMillis();
		Random randomGenerator = new Random(currentTime);
		
		currentTime = System.currentTimeMillis();
		Random randomFilp = new Random(currentTime);
		
		for (int i = 0; i < r; i++) {
			String v = vertexArr[randomGenerator.nextInt(n)];
			rrSets.add(this.generateReverseReachableSet(g, v, randomFilp, model));
		}
		
		for (int i = 0; i < k; i++) {
			HashMap<String, Integer> marginalGain = new HashMap<>();
			for (HashSet<String> rrSet : rrSets) {
				for (String vertex : rrSet) {
					if (marginalGain.containsKey(vertex)) {
						marginalGain.put(vertex, marginalGain.get(vertex) + 1);
					} else {
						marginalGain.put(vertex, 1);
					}
				}
			}
			
			int maxInf = 0;
			String maxNode = new String("-1");
			for (String vertex : marginalGain.keySet()) {
				if (marginalGain.get(vertex) > maxInf) {
					maxNode = vertex;
					maxInf = marginalGain.get(vertex);
				}
			}
			
			Iterator<HashSet<String>> hashItr = rrSets.iterator();
			while (hashItr.hasNext()) {
				HashSet<String> rrSetCheck = hashItr.next();
				if (rrSetCheck.contains(maxNode)) {
					hashItr.remove();
				}
				
			}
//			System.out.println(rrSets.size());
			s.add(maxNode);
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
		ReverseInfluenceSampling ris = new ReverseInfluenceSampling();
		int k = Integer.parseInt(args[2]);
		int n = dirWgtGph.vertexSet().size();
		Double samplingCnt = n*Math.log(Double.parseDouble(Integer.toString(n)));
		int r = samplingCnt.intValue();
		long startTime = System.currentTimeMillis();
		ArrayList<String> s = ris.calculateSourceSet(dirWgtGph, k, r, model);
		long endTime = System.currentTimeMillis();
		double runTimeSec = (endTime - startTime)/1000.0;
		System.out.println("RIS algorithm: k = " + k + ", r = " + r + ", runtime = " + runTimeSec + " secs.");
		System.out.println(s);
	}

}
