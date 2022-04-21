//Wu Wei (wuw4)
import java.util.*;
public class DLBTrie<T> {
	private Node root;
	private Node pointer;
	//private int n;
	public DLBTrie() {
		
	}
	
	public void put(String key) {
		if(key ==null) throw new IllegalArgumentException("String is null");
		root = put(root, key, 0);
		pointer=root;
	}
	public Node put(Node x, String key, int n) {
		if(x == null) {
			x = new Node();
			if(n == key.length()) {
				x.val='^';
				return x;
			}
			x.val = key.charAt(n);
		}
		if(n == key.length()) {
			Node temp = new Node('^',x, null);
			return temp;
		}
		if(x.val == (Object)key.charAt(n)) {
			x.down = put(x.down, key, n+1);
			return x;
		}
		else {
			x.next = put(x.next, key, n);
			return x;
		}
	}
	
	public StringBuilder[] showPrediction(String prefix) {
		StringBuilder[] array = new StringBuilder[5];
		Node tempPointer = setPointer(root, prefix, 0);
		if(tempPointer==null) return null;
		for(int i=0;i<5;i++)
			array[i]=new StringBuilder(prefix);
		int m = showPrediction(array, 0, tempPointer.down);
		for(int i=m+1;i<5;i++)
			array[i]=null;
		return array;
	}

	public int showPrediction(StringBuilder[] array, int n, Node p){
		if(n==5)
			return 5;
		n = continueAdd(array, n, p);
		if(n==5)
			return 5;
		Node temp = p;
		while(n<5) {
			if(temp.val!=(Object)'^') {
				if(temp.next!=null) {
					n= showPrediction(array, n, temp.next);
				}
				else {
					array[n].append(temp.val);
					temp = temp.down;
				}
			}
			else if(temp.next!=null)
				temp = temp.next;
			else
				break;
			}
		return n;
	}
	
	private int continueAdd(StringBuilder[] array, int n, Node p) {
		if(n>=5)
			return 5;
		while(p.val!=(Object)'^') {
			array[n].append(p.val);
			p = p.down;
		}
		if(p.next!=null&&n<4) {
			array[n+1]=new StringBuilder(array[n].toString());
			return continueAdd(array, n+1, p.next);
		}
		return n+1;
	}

	public Node setPointer(Node p, String str,int x){
		if(p ==null)
			return null;
		if(p.val==(Object)str.charAt(x)) {
			if(x+1==str.length())
				return p;
			return setPointer(p.down, str,x+1);}
		else
			return setPointer(p.next, str, x);
	}
	
	private static class Node{
		private Object val;
		private Node next;
		private Node down;
		
		public Node() {
			
		}
		public Node(Object val, Node next, Node down) {
			this.val = val;
			this.next = next;
			this.down = down;
		}
	}
}
