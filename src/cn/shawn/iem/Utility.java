/**
 * Utilities for graphs
 */
package cn.shawn.iem;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.jgrapht.graph.ClassBasedEdgeFactory;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

/**
 * @author zhuxiang
 *
 */
public class Utility {
	
	/**
	 * 
	 * @param fileName
	 * @return dirWgtGph
	 */
	public static DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> loadGraph(String fileName) {
		DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> dirWgtGph = new DefaultDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(fileName));
			String line = br.readLine();
			ClassBasedEdgeFactory<String, DefaultWeightedEdge> ef = new ClassBasedEdgeFactory<>(DefaultWeightedEdge.class);
			while (line != null) {
				String[] vertexs = line.trim().split("[\t ]");
				dirWgtGph.addVertex(vertexs[0]);
				dirWgtGph.addVertex(vertexs[1]);
				DefaultWeightedEdge e = ef.createEdge(vertexs[0], vertexs[1]);
				dirWgtGph.addEdge(vertexs[0], vertexs[1], e);
				if (vertexs.length == 2) {
					dirWgtGph.setEdgeWeight(e, 1.0);
				}
				if (vertexs.length == 3) {
					dirWgtGph.setEdgeWeight(e, Double.parseDouble(vertexs[2]));
				}
				line = br.readLine();
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dirWgtGph;
	}
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String fileName = args[0];
		DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> dirWgtGph = Utility.loadGraph(fileName);
		System.out.println(dirWgtGph.vertexSet().size());
		System.out.println(dirWgtGph.edgeSet().size());
		
	}

}
