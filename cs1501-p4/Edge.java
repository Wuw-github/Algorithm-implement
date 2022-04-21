//Wu Wei(wuw4)
public class Edge {
	private final int COPPER_SPEED = 230000000;
	private final int FIBER_SPEED = 200000000;
	int pt1;
	int pt2;
	String type;
	int bandwidth;
	int length;
	double latency;
	public Edge(int pt1, int pt2, String type, int bandwidth, int length) {
		this.pt1 = pt1;
		this.pt2 = pt2;
		this.type = type;
		this.bandwidth = bandwidth;
		this.length = length;
		this.latency = latency();
	}
	private double latency() {
		if(type.equals("copper")) return (double)length/COPPER_SPEED;
		if(type.equals("optical")) return (double)length/FIBER_SPEED;
		else throw new IllegalArgumentException("type does not match");
	}
	public boolean checkSame(Edge ed) {
		if(ed.pt1 == this.pt1||ed.pt1 == this.pt2) {
			if(ed.pt2 == this.pt1 || ed.pt2 == this.pt2)
				return true;
		}
		return false;
	}
	public int other(int v) {
		if(v == pt1) return pt2;
		if(v == pt2) return pt1;
		else throw new IllegalArgumentException("The point is not in this edge");
	}
	public int either() {
		return pt1;
	}
	public int compareTo(Edge e) {
		if(this.latency>e.latency) return 1;
		if(this.latency== e.latency) return 0;
		return -1;
	}
	public String toString() {
		return new String("("+pt1+", "+pt2+")");
	}
}
