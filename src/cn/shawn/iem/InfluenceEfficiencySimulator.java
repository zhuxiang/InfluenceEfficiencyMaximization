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
public class InfluenceEfficiencySimulator {

	public Double simulateInfluenceEfficiency(DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> g, ArrayList<String> s, String model, Integer times) {	
		Double efficiencySum = 0.0;
		Double probability = 0.01;
		for (int i = 0; i < times; i++) {
			HashMap<String, Double> distanceFromSourceSet = new HashMap<>();
			HashSet<String> activedVertexSet = new HashSet<>();
			ArrayDeque<String> sDeque = new ArrayDeque<String>();
			for (String sVertex : s) {
				sDeque.add(sVertex);
				activedVertexSet.add(sVertex);
				distanceFromSourceSet.put(sVertex, 1.0);
			}
			long currentTime = System.currentTimeMillis();
			Random randomFlip = new Random(currentTime);
			switch (model) {
			case "wic":
				while (sDeque.size() > 0) {
					String vertex = sDeque.removeFirst();
					Set<DefaultWeightedEdge> outgoingEdges = g.outgoingEdgesOf(vertex);
					for (DefaultWeightedEdge outgoingEdge : outgoingEdges) {
						String targetVertex = g.getEdgeTarget(outgoingEdge);
						if (!activedVertexSet.contains(targetVertex)) {
							double flip = randomFlip.nextDouble();
							if (flip < 1.0 / g.inDegreeOf(targetVertex)) {
								activedVertexSet.add(targetVertex);
								sDeque.addLast(targetVertex);
								double distance = distanceFromSourceSet.get(vertex) + g.getEdgeWeight(outgoingEdge);
								if (!distanceFromSourceSet.containsKey(targetVertex)
										|| distanceFromSourceSet.containsKey(targetVertex)
												&& distance < distanceFromSourceSet.get(targetVertex)) {
									distanceFromSourceSet.put(targetVertex, distance);
								}
							}
						}
					}
				}
				break;

			case "uic":
				while (sDeque.size() > 0) {
					String vertex = sDeque.removeFirst();
					Set<DefaultWeightedEdge> outgoingEdges = g.outgoingEdgesOf(vertex);
					for (DefaultWeightedEdge outgoingEdge : outgoingEdges) {
						String targetVertex = g.getEdgeTarget(outgoingEdge);
						if (!activedVertexSet.contains(targetVertex)) {
							double flip = randomFlip.nextDouble();
							if (flip < probability) {
								activedVertexSet.add(targetVertex);
								sDeque.addLast(targetVertex);
 								double distance = distanceFromSourceSet.get(vertex) + g.getEdgeWeight(outgoingEdge);
 								if (!distanceFromSourceSet.containsKey(targetVertex)
										|| distanceFromSourceSet.containsKey(targetVertex)
												&& distance < distanceFromSourceSet.get(targetVertex)) {
									distanceFromSourceSet.put(targetVertex, distance);
								}
							}
						}
					}
				}
				break;

			default:
				break;
			}
			Double efficiency = 0.0;
			for (String vertex : distanceFromSourceSet.keySet()) {
				efficiency += 1.0 / distanceFromSourceSet.get(vertex);
			}
			efficiencySum += efficiency;
		}
		return efficiencySum / times;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
