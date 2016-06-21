/**
 * 
 */
package cn.shawn.iem;

import java.util.ArrayList;

import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

/**
 * @author zhuxiang
 *
 */
public class ScalabilityTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		String fileName = args[0];
		DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> dirWgtGph = 
				Utility.loadUndirectedGraph(fileName);
//		System.out.println(dirWgtGph.vertexSet().size());
//		System.out.println(dirWgtGph.edgeSet().size());
		int edgeNum = Integer.parseInt(args[1]);
		DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> subgraph =
				Utility.generateSubgraph(dirWgtGph, edgeNum);
//		System.out.println(subgraph.vertexSet().size());
//		System.out.println(subgraph.edgeSet().size());
		
		String model = args[2];
		int k = Integer.parseInt(args[3]);
		int n = subgraph.vertexSet().size();
		Double samplingCnt = n*Math.log(Double.parseDouble(Integer.toString(n)));
		ReverseEfficiencySampling res = new ReverseEfficiencySampling();
		int r = samplingCnt.intValue();
		double runTimeSecTotal = 0.0;
		for (int i = 0; i < 10; i++) {
			long startTime = System.currentTimeMillis();
			System.out.println("Start running res algorithm " + i);
			ArrayList<String> s = res.calculateSourceSet(subgraph, k, r, model);
			long endTime = System.currentTimeMillis();
			double runTimeSec = (endTime - startTime)/1000.0;
			runTimeSecTotal+=runTimeSec;
			System.out.println("RES algorithm: k = " + k + ", r = " + r + ", runtime = " + runTimeSec + " secs.");
			System.out.println(s);
		}
		System.out.println("Average runtime = " + runTimeSecTotal/10 + " secs.");	
	}

}
