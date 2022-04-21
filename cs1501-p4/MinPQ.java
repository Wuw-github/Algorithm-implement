//Wu Wei(wuw4)
public class MinPQ<T> {
	private int maxN;
	public int n;
	private T[] tree;
	
	public MinPQ(int n) {
		maxN = n;
		this.n = 0;
		tree = (T[]) new Object[maxN+1];
	}
	public void add(T key) {
		tree[++n] = key; 
		swim(n);
	}
	public void show() {
		for(int i=1;i<=n;i++) {
			System.out.println((Edge)tree[i]);
		}
	}
	public T pop() {
		T temp = tree[1];
		if(n == 0) return null;
		exch(1, n--);
		sink(1);
		return temp;
	}
	
	private void sink(int k) {
		while(2*k <= n) {
			int j = findsmaller(2*k, 2*k+1);
			if(smaller(k, j)) break;
			exch(k, j);
			k = j;
		}
	}
	private void swim(int k) {
		while(k>1 && smaller(k, k/2)) {
			exch(k, k/2);
			k = k/2;
		}
	}
	private int findsmaller(int left, int right) {
		if(right>n) return left;
		if(smaller(left, right)) return left;
		return right;
	}
	private void exch(int x, int y) {
		T temp = tree[x];
		tree[x] = tree[y];
		tree[y] = temp;
	}
	private boolean smaller(int x, int y) {
		if(((Edge) tree[x]).compareTo((Edge)tree[y])<0) return true;
		return false;
	}
}
