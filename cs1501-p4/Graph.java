//Wu Wei(wuw4)
import java.text.DecimalFormat;

public class Graph {
	int V;
	int E;
	listNode[] graph;
	public Graph(int V) {
		graph = new listNode[V];
		this.V = V;
	}
	private static DecimalFormat df2 = new DecimalFormat(".###");
	
	public void addEdge(int pt1, int pt2, String type, int bandwidth, int length) {
		addEdge(new Edge(pt1, pt2, type, bandwidth, length));
	}
	public void addEdge(Edge e) {
		E++;
		add(e.pt1, e);
		add(e.pt2, e);
	}
	public void add(int i, Edge e) {
		if(graph[i] == null) graph[i] = new listNode();
		listNode pt = graph[i];
		while(pt.next!=null) pt = pt.next;
		pt.next = new listNode(e);
	}
	//this is an additional method for finding the articulation points which is not used in this program
	public boolean hasArticulationPt() {
		int[] low = new int[V];
		int[] order = new int[V];
		boolean[] visited = new boolean[V];
		artNode[] tree = new artNode[V];
		for(int i=0; i<V; i++) {
			low[i] = -1;
			order[i] = -1;
			visited[i] = false;
			tree[i] = new artNode();
		}
		return ArtPtRec(low, order, visited, tree, 0, 0);
	}
	private boolean ArtPtRec(int[] low, int[] order, boolean[] visited, artNode[] tree, int v, int n) {
		assert !visited[v];
		assert n<V;
		order[v] = n;
		visited[v] = true;
		listNode curr = graph[v].next;
		boolean hasArt = false;
		while(curr!=null) {
			int child = curr.edge.other(v);
			if(visited[child]) curr = curr.next;
			else {
				tree[v].add(child);
				hasArt= ArtPtRec(low, order, visited, tree, child, ++n);
				if(hasArt) return true;
			}
		}
		if(v==0) {
			if(tree[v].numOfChild() >1) return true;
			return false;
		}
		int lowest = order[v];
		if(tree[v].numOfChild()==0) {
			curr = graph[v].next;
			if(curr==null) return true;
			while(curr!=null) {
				if((!isChildOf(v, curr.edge.other(v), tree))&& lowest>order[curr.edge.other(v)])
					lowest = order[curr.edge.other(v)];
				curr = curr.next;
			}
			low[v] = lowest;
			if(low[v]>=order[v]) return true;
			return false;
		}
		else {
			lowest = lowestFromChild(tree[v], v, low, order);
			curr = graph[v].next;
			while(curr!=null) {
				if((!isChildOf(v, curr.edge.other(v), tree))&&(!isParentOf(v, curr.edge.other(v), tree)))
					if(lowest> order[curr.edge.other(v)]) lowest = order[curr.edge.other(v)];
				curr = curr.next;
			}
			low[v] = lowest;
			if(low[v]>=order[v]) return true;
			return false;
		}
	}
	//find the lowest return index from children
	private int lowestFromChild(artNode c, int v, int[] low, int[] order) {
		artNode temp = c.next;
		assert temp!=null;
		int lowest = order[v];
		while(temp!=null) {
			if(lowest>low[temp.v])lowest = low[temp.v];
			temp = temp.next;
		}
		return lowest;
	}
	//determine if one vertex is the child of another vertex
	private boolean isChildOf(int lower, int upper, artNode[] tree) {
		artNode temp = tree[upper].next;
		while(temp!=null) {
			if(lower == temp.v) return true;
			temp = temp.next;
		}
		return false;
	}
	//determine if one vertex is the parent of another vertex
	private boolean isParentOf(int v, int x, artNode[] tree) {
		artNode temp = tree[v].next;
		while(temp!=null) {
			if(x ==temp.v) return true;
			temp = temp.next;
		}
		return false;
	}
	//print the connection path between two vertices
	private void checkConnect(int[] via, double[] dis, int[] band, int st, int ed) {
		if(via[ed] == -1)
			System.out.println("\nVertices "+st+" and "+ed+" are not connected");
		else {
			int[] path = new int[V];
			int vertex = 0;
			int pt = ed;
			while(pt!=via[pt]) {
				path[vertex++] = pt;
				pt = via[pt];
			}
			path[vertex] = pt;
			System.out.print("The lowest latency path from "+st+" to "+ed +" is:    ");
			for(int i =vertex;i>0;i--) {
				System.out.print(path[i] +" => ");
			}
			System.out.println(path[0]);
			System.out.println("Latency(in nanosecond): "+ df2.format(dis[ed]*1000000000));
			System.out.println("Bandwidth: "+band[ed]+" Mbps");
		}
	}
	//find the shortest path between two vertices based on the latency
	public void lowestLatencyPath(int st, int ed) {
		if(st<0 || st>V-1) throw new IllegalArgumentException("vertex "+st+" is not in the graph");
		if(ed<0 || ed>V-1) throw new IllegalArgumentException("vertex "+ed+" is not in the graph");
		int visited = 0;  //number of visited vertex
		int[] via= new int[V];
		double[] dis = new double[V];
		int[] band = new int[V];
		boolean[] visit = new boolean[V];
		for(int i=0;i<V;i++) {
			via[i] = -1;
			dis[i] = -1;
			band[i] = -1;
			visit[i] = false;
		}
		dis[st] = 0;
		band[st] = 0;
		via[st] = st;
		int curr = st; //curr is the current node that visited
		
		while(visited<V) {
			//add siblings
			if(curr == -1) {
				checkConnect(via, dis, band, st, ed);
				return;
			}
			listNode node = graph[curr];
			while(node.next!=null) {
				node = node.next;
				int other = node.edge.other(curr);
				int bandwidth = node.edge.bandwidth;
				if(!visit[other]) {
					double newDis = dis[curr]+node.edge.latency;
					if(dis[other]==-1) {
						dis[other]=newDis;
						via[other] = curr;
						if(band[curr] == 0) band[other] = bandwidth;
						else if(band[curr]>bandwidth) band[other] = bandwidth;
						else band[other] = band[curr];
					}
					else {
						if(dis[other]>newDis) {
							dis[other] = newDis;
							via[other] = curr;
							if(band[curr]>bandwidth) band[other] = bandwidth;
							else band[other] = band[curr];
						}
					}
				}
			}
			//find next target
			visited++;
			visit[curr] = true;
			curr=-1;
			double least = -1;
			for(int i=0;i<V;i++) {
				if(dis[i]!=-1 && (!visit[i])) {
					if(curr==-1 || least > dis[i]) {
						least = dis[i];
						curr = i;
					}
				}
			}
		}
		//add to path
		checkConnect(via, dis, band, st, ed);
	}
	
	//determine if the graph is copper-only
	public boolean copperOnly() {
		boolean[] visit = new boolean[V];
		for(int i = 0; i<V;i++)	visit[i] = false;
		if(DFSrec(0, visit, 0)==V) return true;
		return false;
	}
	//how many vertices could be visited from one point
	private int DFSrec(int visited, boolean[] visit, int v) {
		listNode node = graph[v].next;
		visit[v] = true;
		visited++;
		while(node!=null) {
			if(!visit[node.edge.other(v)] && node.edge.type.equals("copper")) {
				visited = DFSrec(visited, visit, node.edge.other(v));
			}
			node = node.next;
		}
		return visited;
	}
	
	//determine if the graph is still connected if any two points fail
	public int[] connnectedfailtwopoints() {
		for(int i = 0; i<V-1; i++) {
			for(int j = i+1; j<V;j++) {
				int v = 0;
				while(v==i||v==j) v++;
				if(!connected(i, j, v)) {
					int[] array= {i, j};
					return array;
				}
			}
		}
		return null;
	}
	
	private boolean connected(int x, int y,int v) {
		boolean[] visited = new boolean[V];
		for(int i=0;i<V;i++)
			visited[i] = false;
		if(connectedfailrec(x, y, 0, v, visited)==(V-2)) return true;
		return false;
	}
	
	private int connectedfailrec(int x, int y, int visited, int v, boolean[] visit) {
		listNode node = graph[v].next;
		visit[v] = true;
		visited++;
		while(node!=null) {
			if(!(node.edge.other(v)==x || node.edge.other(v)==y) && (!visit[node.edge.other(v)]))
				visited = connectedfailrec(x, y, visited, node.edge.other(v), visit);
			node = node.next;
		}
		return visited;
	}
	
	//find the MST for the lowest average latency
	public void minLatencyTree() {
		int n = 0; int visit = 0;
		Edge[] tree = new Edge[E];
		MinPQ<Edge> pq = new MinPQ(E);
		boolean[] visited = new boolean[V];
		for(int i = 0; i< V;i++) visited[i] = false;
		int vertex = 0; 
		listNode curr = graph[0].next;
		while(visit<V-1) {
			visit++;
			visited[vertex] = true;
			while(curr!=null) {
				if(!visited[curr.edge.other(vertex)]) {
					pq.add(curr.edge);
				}
				curr = curr.next;
			}
			Edge temp = pq.pop();
			while(shouldKeep(temp, visited)) {
				temp = pq.pop();
				if(temp == null) {
					System.out.println("\nGraph is not connected, no Minimum Latency Path");
					return;
				}
			}
				
			tree[n] = temp;
			if(!visited[tree[n].either()]) vertex = tree[n].either();
			else vertex = tree[n].other(tree[n].either());
			n++;
			curr = graph[vertex].next;
		}
		double latency =0;
		for(int i=0; i<n;i++) {
			latency+=tree[i].latency;
			System.out.println("("+tree[i].either()+", "+tree[i].other(tree[i].either())+")");
		}
		System.out.println("The average latency is "+df2.format(latency/n*1000000000)+" in nanoseconds");
	}
	private boolean isConnected() {
		return true;
	}
	//method to determine if two points both been added
	private boolean shouldKeep(Edge key, boolean[] array) {
		if(key == null) return true;
		return array[key.either()]&&array[key.other(key.either())];
	}
	
	//build the adjacency list
	private static class listNode{
		private listNode next;
		private Edge edge;
		public listNode() {}
		public listNode(Edge e) {
			edge = e;
		}
	}
	
	//used for finding the articulation point, not used in this program
	private static class artNode{
		int v;
		artNode next;
		int n; //num of child
		public artNode() {
			
		}
		public artNode(int v) {
			this.v = v;
		}
		public void add(int v) {
			next = add(next, v);
			n++;
		}
		private artNode add(artNode x, int v) {
			if(x==null) return new artNode(v);
			x.next = add(x.next, v);
			return x;
		}
		public int numOfChild() {
			return n;
		}
	}
}
