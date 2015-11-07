import java.io.*;
import java.util.*;
// import java.Math.*;

public class BetweennessCentrality{
	
	public static void main(String[] args){
		if (args.length < 1){
			System.out.println("Please give the data file URL as second argument");
			System.exit(0);
		}
		try{
			
			Scanner scanner = new Scanner(new File(args[0]));
			int noOfVertices = 0;
			while (scanner.hasNext()) noOfVertices = Math.max(noOfVertices,scanner.nextInt());

			ArrayList<ArrayList<Integer>> adjacencyList = new ArrayList<ArrayList<Integer>>();
			for (int i=0;i<noOfVertices;i++) adjacencyList.add(new ArrayList<Integer>());

			scanner = new Scanner(new File(args[0]));

			while ( scanner.hasNext() ){
				int v1 = scanner.nextInt()-1;
				int v2 = scanner.nextInt()-1;
				adjacencyList.get(v1).add(v2);
				adjacencyList.get(v2).add(v1);
			}

			solve(adjacencyList, noOfVertices);
		}
		catch (Exception e){
			e.printStackTrace();
			System.out.println("Data file not found");
		}
	}

	private static void solve(ArrayList<ArrayList<Integer>> adjacencyList, int noOfVertices){
		System.out.println("No of vertices : "  + noOfVertices);
		double[] cBindex = new double[noOfVertices];
		for (int i=0;i<noOfVertices;i++) cBindex[i]=0;

		for (int i=0;i<noOfVertices;i++){
			int s = i;
			Stack<Integer> stack = new Stack<Integer>();
			
			ArrayList<ArrayList<Integer>> predecessors = new ArrayList<ArrayList<Integer>>();
			for (int j=0;j<noOfVertices;j++) predecessors.add(new ArrayList<Integer>());

			int[] countPaths = new int[noOfVertices];
			for (int j=0;j<noOfVertices;j++) countPaths[j] = 0;
			countPaths[s] = 1;

			int[] distance = new int[noOfVertices];
			for (int j=0;j<noOfVertices;j++) distance[j] = -1;
			distance[s] = 0;

			Queue<Integer> queue = new ArrayDeque<Integer>();
			queue.add(s);

			while (queue.peek() != null){
				int v = queue.remove();
				stack.add(v);

				for (int j=0;j<adjacencyList.get(v).size();j++){
					int w = adjacencyList.get(v).get(j);

					if (distance[w]<0){
						queue.add(w);
						distance[w] = distance[v] + 1;
					}

					if (distance[w] == distance[v] + 1){
						countPaths[w] += countPaths[v];
						predecessors.get(w).add(v);
					}

				}

			}

			double[] dependency = new double[noOfVertices];
			for (int j=0;j<noOfVertices;j++) dependency[j] = 0;

			while (!stack.empty()){
				int w = stack.pop();
				for (int j=0;j<predecessors.get(w).size();j++){
					int v = predecessors.get(w).get(j);
					dependency[v] += ( (double)countPaths[v] ) /countPaths[w]*(1+dependency[w]);
				}
				if (w != s) cBindex[w] += dependency[w];
			}
		}

		for (int i=0;i<noOfVertices;i++) cBindex[i] /= 2;
		for (int i=0;i<noOfVertices;i++){
			System.out.println("CentralBetweenness index of vertex " + (i+1)+ " :" +cBindex[i]);
		}
	}
}