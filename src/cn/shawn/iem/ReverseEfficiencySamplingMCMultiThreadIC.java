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
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

/**
 * @author zhuxiang
 *
 */
public class ReverseEfficiencySamplingMCMultiThreadIC {
	
	public static int nThreads = 10;
	public static int nTasks = 10;
	private static ExecutorService threadPool = 
			Executors.newFixedThreadPool(nThreads);
	
	public static ExecutorService getThreadPool() {
		return threadPool;
	}
	
	private ArrayList<HashMap<String, Double>> generateReverseReachableMap(
			final DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> g, 
			final int r, final String model
			) {
		
		CompletionService<ArrayList<HashMap<String, Double>>> cs = 
				new ExecutorCompletionService<>(threadPool);
		int nTasks = ReverseEfficiencySamplingMCMultiThreadIC.nTasks;
		final int iterations = r / nTasks;
		
		int n = g.vertexSet().size();
		String[] vertexArr = new String[n];
		g.vertexSet().toArray(vertexArr);
		
		long currentTime = System.currentTimeMillis();
		Random randomGenerator = new Random(currentTime);
		
		currentTime = System.currentTimeMillis();
		Random randomFlip = new Random(currentTime);
		
		for (int i = 0; i < nTasks; i++) {
			
			cs.submit(new Callable<ArrayList<HashMap<String,Double>>>() {
							
				@Override
				public ArrayList<HashMap<String, Double>> call() throws Exception {
					
					ArrayList<HashMap<String, Double>> rrMapArrThread = 
							new ArrayList<>();
					
					HashSet<String> activedVertexSet = new HashSet<>();
					ArrayDeque<String> sDeque = new ArrayDeque<String>();
					
					for (int j = 0; j < iterations; j++) {
						HashMap<String, Double> rrMap = new HashMap<>();
						activedVertexSet.clear();
						sDeque.clear();
						
						String v = vertexArr[randomGenerator.nextInt(n)];
						
						// initialize reverse reachable dequeue
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
											if (!rrMap.containsKey(sourceVertex) || rrMap.containsKey(sourceVertex)
													&& distance < rrMap.get(sourceVertex)) {
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
											if (!rrMap.containsKey(sourceVertex) || rrMap.containsKey(sourceVertex)
													&& distance < rrMap.get(sourceVertex)) {
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
						
						rrMapArrThread.add(rrMap);
//						HashMap<String, Double> testTmp = new HashMap<>();
//						testTmp.put(v, (double)rrMap.size());
//						tmp.add(testTmp);
					}
					return rrMapArrThread;
				}
			});
		}
		
		ArrayList<HashMap<String, Double>> rrMapArrSum = new ArrayList<>();
		for (int i = 0; i < nTasks; i++) {
			try {
				rrMapArrSum.addAll(cs.take().get());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return rrMapArrSum;
	}
	
	public ArrayList<String> calculateSourceSet(
			final DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> g, 
			final int k, final int r, final String model) {
		// Initialize seed set s
		ArrayList<String> s = new ArrayList<>();
		// Initialize distance map
		HashMap<Integer, Double> distanceMap = new HashMap<>();
		// The whole reverse reachable hash maps.
		ArrayList<HashMap<String, Double>> rrMapArr = 
				this.generateReverseReachableMap(g, r, model);
		// Initialize the distance map.
		for (int i = 0; i < r; i++) {
			distanceMap.put(i, Double.POSITIVE_INFINITY);
		}
		
		/* Now we want to separate the hash map list, calculate the 
		 * efficiency independently, then combine all the efficiency 
		 * map. 
		*/
		
//		CompletionService<HashMap<String, Double>> cs = 
//				new ExecutorCompletionService<>(threadPool);
//		int nTasks = ReverseEfficiencySamplingMCMultiThreadIC.nTasks;
//		final int iterations = r / nTasks;
//		
//		for (int i = 0; i < nTasks; i++) {
//			
//			cs.submit(new Callable<HashMap<String,Double>>() {
//				
//				@Override
//				public HashMap<String, Double> call() throws Exception {
//					
//					return null;
//				}
//			});
//		}
		
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
			if (!maxNode.equals("-1")) {
				s.add(maxNode);
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
		int k = Integer.parseInt(args[2]);
		DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> dirWgtGph = 
				Utility.loadGraph(fileName);
		ReverseEfficiencySamplingMCMultiThreadIC resMulThread = 
				new ReverseEfficiencySamplingMCMultiThreadIC();
		int n = dirWgtGph.vertexSet().size();
		Double samplingCnt = n*Math.log(Double.parseDouble(Integer.toString(n)));
		int r = samplingCnt.intValue();
//		System.out.println(r);
		r = r/ReverseEfficiencySamplingMCMultiThreadIC.nTasks * 
				ReverseEfficiencySamplingMCMultiThreadIC.nTasks;
		long startTime = System.currentTimeMillis();
		ArrayList<String> s = 
				resMulThread.calculateSourceSet(dirWgtGph, k, r, model);
		long endTime = System.currentTimeMillis();
		double runTimeSec = (endTime - startTime)/1000.0;
		System.out.println("RES multi-thread algorithm: k = " + k + ", r = " + r + ", runtime = " + runTimeSec + " secs.");
		System.out.println(s);
		ReverseEfficiencySamplingMCMultiThreadIC.getThreadPool().shutdown();
//		Integer times = new Integer(100);
//		Double efficiency = Utility.simulateInfluenceEfficiency(dirWgtGph, s, model, times);
//		System.out.println("efficiency = " + efficiency + ".");
		
	}

}
