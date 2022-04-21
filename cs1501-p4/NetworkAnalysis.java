//Wu Wei(wuw4)
import java.io.*;
import java.util.*;
public class NetworkAnalysis {
	static Scanner sc = new Scanner(System.in);
	public static void main(String [] args) {
		if(args.length==0) {
			System.out.println("No input file.");
			return;
		}
		Scanner fsc;
		try {
			File fileIn = new File(args[0]);
			fsc = new Scanner(fileIn);
		}catch (FileNotFoundException e) {
			System.out.println("Cannot open the file '"+args[0]+"'.");
			return;
		}
		Graph graph = new Graph(Integer.parseInt(fsc.nextLine()));
		while(fsc.hasNext()) {
			String[] edgeinfo = fsc.nextLine().split(" ");
			graph.addEdge(Integer.parseInt(edgeinfo[0]), Integer.parseInt(edgeinfo[1]), edgeinfo[2], Integer.parseInt(edgeinfo[3]), Integer.parseInt(edgeinfo[4]));
		}
		fsc.close();
		String input;
		
		do {
			System.out.println("\n1) Find the lowest latency path between two points");
			System.out.println("2) Determine if the graph is copper-only connected");
			System.out.println("3) Find the minimum average latency spanning tree");
			System.out.println("4) Determine if the graph still connected when any two vertices fail");
			System.out.println("5) Quit");
			System.out.print("Please select a number(1-5): ");
			input = sc.nextLine();
			if(input.equals("1"))
				findLowestLatency(graph);
			if(input.equals("2"))
				copperOnly(graph);
			if(input.equals("3"))
				MST(graph);
			if(input.equals("4"))
				Articulation(graph);
		}while(!input.equals("5"));

	}
	public static void findLowestLatency(Graph graph) {
		System.out.print("Please enter the first vertex: ");
		int first = Integer.parseInt(sc.nextLine());
		System.out.print("Please enter the second vertex: ");
		int second = Integer.parseInt(sc.nextLine());
		graph.lowestLatencyPath(first, second);
	}
	public static void copperOnly(Graph graph) {
		if(graph.copperOnly())
			System.out.println("\nThe graph IS copper-only connected");
		else
			System.out.println("\nThe graph IS NOT copper-only connected");
	}
	public static void MST(Graph graph) {
		System.out.println("\nThe Lowest Average Latency Spanning Tree:");
		graph.minLatencyTree();
	}
	public static void Articulation(Graph graph) {
		int[] arr = graph.connnectedfailtwopoints();
		if(arr==null)
			System.out.println("\nThe graph IS connected when any two points fail");
		else {
			System.out.println("\nThe graph IS NOT connected when some two points fail");
			System.out.println("Ex: Vertex "+arr[0]+" and "+arr[1]);
		}
	}
}
