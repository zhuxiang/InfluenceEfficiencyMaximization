/**
 * 
 */
package cn.shawn.iem;

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
				Utility.loadGraph(fileName);
		System.out.println(dirWgtGph.vertexSet().size());
		System.out.println(dirWgtGph.edgeSet().size());
		int edgeNum = Integer.parseInt(args[1]);
		DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> subgraph =
				Utility.generateSubgraph(dirWgtGph, edgeNum);
		System.out.println(subgraph.vertexSet().size());
		System.out.println(subgraph.edgeSet().size());
		
//		DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> test =
//				new DefaultDirectedWeightedGraph<>(DefaultWeightedEdge.class);
//		test.addVertex("1");
//		test.addVertex("1");
//		System.out.println(test.vertexSet().size());
	}

}
