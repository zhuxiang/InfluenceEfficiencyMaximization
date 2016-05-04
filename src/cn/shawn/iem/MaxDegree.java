/**
 * 
 */
package cn.shawn.iem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

/**
 * @author zhuxiang
 *
 */
public class MaxDegree {

	private <K, V extends Comparable<? super V>> Map<K, V> sortByValue( Map<K, V> map ) {
		List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
			public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});
		Map<K, V> result = new LinkedHashMap<K, V>();
		for (Map.Entry<K, V> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}
	public ArrayList<String> calculateByDegree(DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> g, int k) {
		ArrayList<String> s = new ArrayList<>();
		HashMap<String, Integer> outDegreeMap = new HashMap<>();
		for (String vertex : g.vertexSet()) {
			outDegreeMap.put(vertex, g.outDegreeOf(vertex));
		}
		Map<String, Integer> sortedOutDegreeMap = this.sortByValue(outDegreeMap);
		Iterator<Map.Entry<String, Integer>> entries = sortedOutDegreeMap.entrySet().iterator();
		int i = 0;
		while (i < k && entries.hasNext()) {
			Map.Entry<String, Integer> entry = entries.next();
			s.add(entry.getKey());
			i++;
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
		MaxDegree maxDegree = new MaxDegree();
		int k = Integer.parseInt(args[2]);
		long startTime = System.currentTimeMillis();
		ArrayList<String> s = maxDegree.calculateByDegree(dirWgtGph, k);
		long endTime = System.currentTimeMillis();
		double runTimeSec = (endTime - startTime)/1000.0;
		System.out.println("Greedy: k = " + k + ", runtime = " + runTimeSec + " secs.");
		System.out.println(s);
		Integer times = new Integer(100);
		Double efficiency = Utility.simulateInfluenceEfficiency(dirWgtGph, s, model, times);
		System.out.println("efficiency = " + efficiency + ".");
	}

}
