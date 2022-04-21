
public class VINdlb {
	private int n; //how many objects total
	private int[] gap; // gap is the hole after remove
	private int rn; //track the index of the hole
	private Node root;
	
	public VINdlb() {
		n = 0;
		root = null;
		gap = new int[20];
	}
	
	public int insert(String VIN) {
		root = put(root, VIN, 0);
		Node c = containsp(VIN);
		return c.index;
	}
	public int contains(String VIN) {
		Node c = containsp(VIN);
		if(c == null) return -1;
		return c.index;
	}
	private Node containsp(String VIN) {
		Node pointer = root;
		int index = 0;
		while(pointer!=null) {
			if(index == VIN.length()) return pointer;
			if(VIN.charAt(index)==pointer.c) {
				pointer = pointer.down;
				index++;
			}
			else
				pointer = pointer.next;
		}
		return null;
	}
	
	public int remove(String VIN) {
		Node c = containsp(VIN);
		if(c==null) return -1;
		int r = c.index;
		gap[rn++] = r;
		n--;
		c.index =-1;
		c.c = 0;
		return r;
	}
	
	private Node put(Node x, String VIN, int index) {
		if(x == null) {
			x = new Node();
			if(index == VIN.length()) {
				x.c = '^';
				if(rn>0) x.index = gap[--rn];
				else x.index = n;
				n++;
				return x;
			}
			x.c = VIN.charAt(index);
		}
		if(index == VIN.length()) {
			Node temp = new Node('^', x, null);
			if(rn>0) temp.index = gap[--rn];
			else temp.index = n;
			n++;
			return temp;
		}
		if(VIN.charAt(index) == x.c) {
			x.down = put(x.down, VIN, index+1);
		}
		else {
			x.next = put(x.next, VIN, index);
		}
		return x;
	}
	private static class Node{
		private char c;
		private int index;
		private Node next;
		private Node down;
		private Node() {
		}
		private Node(char c, Node next, Node down) {
			this.c = c;
			this.next = next;
			this.down = down;
		}
	}
}
