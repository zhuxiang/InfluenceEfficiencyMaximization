/**
 * Utilities for graphs
 */
package cn.shawn.iem;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jgrapht.graph.ClassBasedEdgeFactory;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

/**
 * @author zhuxiang
 *
 */
public class Utility {
	private static int nThreads = 10;	
	private static ExecutorService threadPool = Executors.newFixedThreadPool(nThreads);
	
	/**
	 * generate subgraph by BFS.
	 * @param dirWgtGph
	 * @param edgeNum
	 * @return
	 */
	public static DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> generateSubgraph(
			DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> dirWgtGph,
			Integer edgeNum) {
		DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> subGraph =
				new DefaultDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		// the vertex which has been added to subgraph
		ArrayList<String> searchedVertexArr = new ArrayList<>();
		// the number of edges has been added to subgraph
		int m = 0;
		
		int n = dirWgtGph.vertexSet().size();
		String[] vertexArr = new String[n];
		dirWgtGph.vertexSet().toArray(vertexArr);
		
		long currentTime = System.currentTimeMillis();
		Random randomGenerator = new Random(currentTime);
		
		ArrayDeque<String> sDeque = new ArrayDeque<>();
		while (m < edgeNum) {
			String v = vertexArr[randomGenerator.nextInt(n)];
			if (searchedVertexArr.contains(v)) {
				continue;
			}
			sDeque.clear();
			sDeque.add(v);
			while (sDeque.size() > 0 && m < edgeNum) {
				String sourceVertex = sDeque.removeFirst();
				searchedVertexArr.add(sourceVertex);
				Set<DefaultWeightedEdge> outgoingEdges = dirWgtGph.outgoingEdgesOf(sourceVertex);
				for (DefaultWeightedEdge outgoingEdge : outgoingEdges) {
					String targetVertex = dirWgtGph.getEdgeTarget(outgoingEdge);
					subGraph.addVertex(sourceVertex);
					subGraph.addVertex(targetVertex);
					subGraph.addEdge(sourceVertex, targetVertex, outgoingEdge);
					m++;
					if (!searchedVertexArr.contains(targetVertex) && !sDeque.contains(targetVertex)) {
						sDeque.addLast(targetVertex);
						
					}			
//					System.out.println(subGraph.edgeSet().size());
					if (m >= edgeNum) {
						break;
					}
				}
			}
		}
//		System.out.println(m);
//		System.out.println(subGraph.edgeSet().size());
		return subGraph;
	}
	
	public static ExecutorService getThreadPool() {
		return threadPool;
	}
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
	 * 
	 * @param fileName
	 * @return
	 */
	public static DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> loadUndirectedGraph(String fileName) {
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
				DefaultWeightedEdge e1 = ef.createEdge(vertexs[0], vertexs[1]);
				DefaultWeightedEdge e2 = ef.createEdge(vertexs[1], vertexs[0]);
				dirWgtGph.addEdge(vertexs[0], vertexs[1], e1);
				dirWgtGph.addEdge(vertexs[1], vertexs[0], e2);
				if (vertexs.length == 2) {
					dirWgtGph.setEdgeWeight(e1, 1.0);
					dirWgtGph.setEdgeWeight(e2, 1.0);
				}
				if (vertexs.length == 3) {
					dirWgtGph.setEdgeWeight(e1, Double.parseDouble(vertexs[2]));
					dirWgtGph.setEdgeWeight(e2, Double.parseDouble(vertexs[2]));
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
	 * 
	 * @param g
	 * @param s
	 * @param model
	 * @param times
	 * @return
	 */
	public static Double simulateInfluenceEfficiency(DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> g, ArrayList<String> s, String model, Integer times) {	
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
	
	public static Double calcInfluenceEfficiencyMCMultiThreadIC(
			final DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> g,
			final ArrayList<String> s, final String model, final Integer times) {
		CompletionService<Double> cs = new ExecutorCompletionService<>(threadPool);
		int nTasks = 10;
		final int iterations = times / nTasks;
		for (int i = 0; i < nTasks; i++) {
			cs.submit(new Callable<Double>() {
				
				@Override
				public Double call() throws Exception {
					Double efficiencySum = 0.0;
					Double probability = 0.01;
					HashMap<String, Double> distanceFromSourceSet = new HashMap<>();
					HashSet<String> activedVertexSet = new HashSet<>();
					ArrayDeque<String> sDeque = new ArrayDeque<String>();
					for (int iter = 0; iter < iterations; iter++) {
						distanceFromSourceSet.clear();
						for (String vertex : s) {
							distanceFromSourceSet.put(vertex, 1.0);
						}
						activedVertexSet.clear();
						activedVertexSet.addAll(s);
						sDeque.clear();
						sDeque.addAll(s);
						
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
											double distance = distanceFromSourceSet.get(vertex) + 
													g.getEdgeWeight(outgoingEdge);
											if (! distanceFromSourceSet.containsKey(targetVertex)
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
											double distance = distanceFromSourceSet.get(vertex) + 
													g.getEdgeWeight(outgoingEdge);
											if (! distanceFromSourceSet.containsKey(targetVertex)
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
					return efficiencySum;
				}
			});
		}
		Double efficiencySumSum = 0.0;
		for (int i = 0; i < nTasks; i++) {
			try {
				efficiencySumSum += cs.take().get();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return efficiencySumSum / times;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String fileName = args[0];
		DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> dirWgtGph = Utility.loadGraph(fileName);
		System.out.println(dirWgtGph.vertexSet().size());
		System.out.println(dirWgtGph.edgeSet().size());
		HashMap<String, Integer> indegreeMap = new HashMap<>();
		for (String vertex : dirWgtGph.vertexSet()) {
			indegreeMap.put(vertex, dirWgtGph.inDegreeOf(vertex));
		}
		long startTime = System.currentTimeMillis();		
		for (int i = 0; i < 1000; i++) {
			for (String vertex : dirWgtGph.vertexSet()) {
				dirWgtGph.inDegreeOf(vertex);
			} 
		}
		long endTime = System.currentTimeMillis();
		double runTimeSec = (endTime - startTime)/1000.0;
		System.out.println("runtime = " + runTimeSec + " secs.");
		startTime = System.currentTimeMillis();
		for (int i = 0; i < 1000; i++) {
			for (String vertex : dirWgtGph.vertexSet()) {
				indegreeMap.get(vertex);
			} 
		}
		endTime = System.currentTimeMillis();
		runTimeSec = (endTime - startTime)/1000.0;
		System.out.println("runtime = " + runTimeSec + " secs.");
	}

}
