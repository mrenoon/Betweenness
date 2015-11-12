import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
import java.util.Vector;

public class Main {
	private HashMap<Integer, Vector<Integer>> nodes = new HashMap<>();
	
	public static void main(String[] args){
		Main solver = new Main();
		try {
			solver.parseData();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
//		solver.printGraph();
		
		solver.analyzeGraph();
	}
	
	@SuppressWarnings("resource")
	private void parseData() throws FileNotFoundException {
		//get all data file in data folder
		String dataFolder = "." + File.separator + "data";
		File folder = new File(dataFolder);
		File[] listOfFiles = folder.listFiles();
		
		for (int i = 0; i < listOfFiles.length; i++) {
			String path =listOfFiles[i].getPath(); 
			String ext = path.substring(path.lastIndexOf('.'));
			if(!ext.equals(".edges")) {
				continue;
			}
			Scanner scanner = new Scanner(listOfFiles[i]);
			while ( scanner.hasNext() ){
				int v1 = scanner.nextInt();
				int v2 = scanner.nextInt();
				
				Vector<Integer> v1Edges = nodes.get(v1);
				if(v1Edges == null) {
					v1Edges = new Vector<>();
					nodes.put(v1, v1Edges);
				}
				Vector<Integer> v2Edges = nodes.get(v2);
				if(v2Edges == null) {
					v2Edges = new Vector<>();
					nodes.put(v2, v2Edges);
				}
				v1Edges.add(v2);
				v2Edges.add(v1);
			}
		}
	}
	
	private void analyzeGraph() {
		int diameter = getDiameter();
		System.out.println("DIAMETER: " + diameter);
		
		int averageNodeDegree = getAverageNodeDegree();
		System.out.println("AVERAGE NODE DEGREE: " + averageNodeDegree);
	}
	
	private int getDiameter() {
		
		int nodeSize = -1;
		Set<Integer> nodes = this.nodes.keySet();
		for (int u: nodes) {
			nodeSize = Math.max(nodeSize, u + 1);
		}
		int[][] distances = new int[nodeSize][nodeSize];
		
		//init as (infinity)
		for (int i = 0; i < nodeSize; i++) {
			for (int j = 0; j < nodeSize; j++) {
				distances[i][j] = 100000;
			}
		}
		
		System.out.println("Prepare for Calculation");
		//dist[v][v] ← 0
		for (int node : nodes) {
			distances[node][node] = 0;
		}
		//dist[u][v] ← 1
		for (int u: nodes) {
			for (int v : this.nodes.get(u)) {
				distances[u][v] = 1;
			}
		}
		
		System.out.println("Finding All Shortest Path");
		// find all shortest paths
		int progress = 0;
//		for (int k = 0; k < distances.length; k++) {
//			System.out.println("Calculating..." + progress++ + "/" + nodeSize);
//			for (int i = 0; i < distances.length; i++) {
//				for (int j = 0; j < distances.length; j++) {
//					if(distances[i][j] > distances[i][k] + distances[k][j])
//						distances[i][j] = distances[i][k] + distances[k][j];
//				}
//			}
//		}
		for (int k : nodes) {
			System.out.println("Calculating..." + progress++ + "/" + nodeSize);
			for (int i : nodes) {
				for (int j : nodes) {
					if(distances[i][j] > distances[i][k] + distances[k][j])
						distances[i][j] = distances[i][k] + distances[k][j];
				}
			}
		}
		
		System.out.println("Finding Diameter");
		//return the longest shortest path
		int diameter = -1;
		for (int i : nodes) {
			for (int j : nodes) {
				diameter = Math.max(diameter, distances[i][j]);
			}
		}
		
		return diameter;
	}

	private int getAverageNodeDegree() {
		int totalDegree = 0;
		for(int node : this.nodes.keySet()) {
			Vector<Integer> edges = this.nodes.get(node);
			totalDegree += edges.size();
		}
		int averageNodeDegree = totalDegree / this.nodes.keySet().size();
		return averageNodeDegree;
	}

	private void printGraph() {
		Integer[] keys = this.nodes.keySet().toArray(new Integer[0]);
		for (int i = 0; i < keys.length; i++) {
			int key = keys[i];
			System.out.println(nodes.get(key));
		}
	}
}
