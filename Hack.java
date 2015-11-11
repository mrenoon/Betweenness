import java.io.*;
import java.util.*;

// import java.Math.*;

public class Hack{
	
	public static void main(String[] args){
		if (args.length < 2){
			System.out.println("Wrong syntax. Correct syntax: Hack [dataFile] [number of nodes to hack]");
			System.exit(0);
		}
		try{
			
			Scanner scanner = new Scanner(new File(args[0]));
			int noOfNodes = Integer.parseInt(args[1]);
			System.out.println("no of nodes to hack: " + noOfNodes);

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

			solve(adjacencyList,noOfNodes);


		}
		catch (Exception e){
			e.printStackTrace();
			System.out.println("Data file not found");
		}
	}

	private static class doubleArrayWrapper {
		private double[] array;
		public void setArray(double[] newArray){
			array = newArray;
		}
		public double[] getArray(){
			return array;
		}
		public doubleArrayWrapper(double[] newArray){
			array = newArray;
		}
		public doubleArrayWrapper(){
		}

	}

	private static double solveBetnessness(ArrayList<ArrayList<Integer>> adjacencyList, boolean isGroup,ArrayList<Integer> group,doubleArrayWrapper cB){
		int noOfVertices = adjacencyList.size();

		// System.out.println("Solving for group " + group.toString()  + " , size of " + group.size());
		// System.out.println("No of vertices = " + noOfVertices);

		boolean[] isInGroup = new boolean[noOfVertices];
		for (int i=0;i<noOfVertices;i++) isInGroup[i] = false;
		for (int i=0;i<group.size();i++) isInGroup[group.get(i)] = true;


		double[] cBindex = new double[noOfVertices];	
		for (int i=0;i<noOfVertices;i++) cBindex[i]=0;
		double groupCBindex = 0;
		

		for (int i=0;i<noOfVertices;i++){
			if (isInGroup[i]) continue;
			// System.out.println();
			// System.out.println();
			// System.out.println("Running from node " + i + "______");

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

					if (distance[w]<0){ //first time reaching this node w
						queue.add(w);
						distance[w] = distance[v] + 1;
					}

					if (distance[w] == distance[v] + 1){
						countPaths[w] += countPaths[v];
						predecessors.get(w).add(v);
					}

				}
			}

			// System.out.println("after bfs, distances = " + Arrays.toString(distance));
			// System.out.println("countPaths = " + Arrays.toString(countPaths));
			// System.out.println("predecessors = " + predecessors.toString());
			// System.out.println("Stack = " + stack.toString());

			double[] dependency = new double[noOfVertices];
			for (int j=0;j<noOfVertices;j++) dependency[j] = 0;

			while (!stack.empty()){
				int w = stack.pop();
				// System.out.println("Popped " + w + " ***********************************");

				for (int j=0;j<predecessors.get(w).size();j++){

					int v = predecessors.get(w).get(j);
					//System.out.println("processing predecessor " + v);

					if (isGroup && isInGroup[w]) {
						//System.out.println("w is in group, dependency[v] = " + dependency[v] + ", dependency[w]="+dependency[w]);
						
						groupCBindex += dependency[w];
						dependency[w]=0;
						//System.out.println("set dependency[v]=0, new groupCBindex = " + groupCBindex);
					} else {
						//System.out.println("v is NOTTTTTTTTTT in group, dependency[v] = " + dependency[v] + ", dependency[w]="+dependency[w]);
						dependency[v] += ( (double)countPaths[v] ) /countPaths[w]*(1+dependency[w]);
						//System.out.println("After update, dependency[v] = " +  dependency[v]);
					}
					//System.out.println();
				}
				if (w != s) cBindex[w] += dependency[w];
			}
		}

		for (int i=0;i<noOfVertices;i++) cBindex[i] /= (noOfVertices-1)*(noOfVertices-2);
		

		if (isGroup){
			groupCBindex /= (noOfVertices - group.size() ) * (noOfVertices - group.size() - 1 );
			return groupCBindex;
		}

		cB.setArray(cBindex);
		//just return a dummy cBindex, not important;
		return cBindex[0]; 
	}

	private static void solve(ArrayList<ArrayList<Integer>> adjacencyList,int noOfNodes){
		
		int noOfVertices = adjacencyList.size();


		doubleArrayWrapper cBindexArray = new doubleArrayWrapper();
		// find the central betweenness of all the vertices in the graph first
		solveBetnessness(adjacencyList,false,new ArrayList<Integer>(),cBindexArray);
		final double[] cBindex = cBindexArray.getArray();

		//then sort the vertices in order of their cBindex;
		Integer[] sortedVertices = new Integer[noOfVertices];
		for (int i=0;i<noOfVertices;i++) sortedVertices[i] = i;

		class vertexComparator implements Comparator<Integer>{
			@Override
		    public int compare(Integer o1, Integer o2) {
		        //sort in descending order of Centrality betweenness
		        return ((Double)cBindex[o2]).compareTo(cBindex[o1]);
		    }
		}

		Arrays.sort(sortedVertices,new vertexComparator());
		
		// selects the top vertices in the sorted list;
		ArrayList<Integer> selected = new ArrayList<Integer>();
		for (int i=0;i<noOfNodes+1;i++) selected.add(sortedVertices[i]);

		System.out.println((noOfNodes+1) + " first nodes in decreasing order of centrality betweenness:");
		System.out.println(selected.toString());

		String selectedNodes = "";
		double maxCB = 0;
		for (int i=0;i<noOfNodes+1;i++){
			selected.remove(0);
			double groupCB = solveBetnessness(adjacencyList,true,selected,new doubleArrayWrapper());
			System.out.println("If we select " + selected.toString()+ " ,centrality betweenness = " + groupCB);
			if (groupCB > maxCB){
				maxCB = groupCB;
				selectedNodes = selected.toString();
			}
			selected.add(sortedVertices[i]);
		}
		System.out.println("------------------------------------------------");
		System.out.println("Conclusion: we should hack " + selectedNodes + ", with centrality betweenness = " + maxCB);

		
		// for (int i=0;i<noOfVertices;i++){
		// 	System.out.println("CentralBetweenness index of vertex " + (i+1)+ " :" +cBindex[i]);
		// }
	}

}