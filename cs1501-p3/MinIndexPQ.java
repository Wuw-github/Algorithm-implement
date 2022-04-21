
public class MinIndexPQ<T>{
	private int maxN;
	public int n;
	private T[] tree;
	private int[] pq;
	private int[] qp; //qp is the direct index in the array
	private String mode;
	
	public MinIndexPQ(String mode) {
		this.mode = mode;
		maxN = 256;
		tree = (T[]) new Object[maxN+1];
		n=0;
		pq = new int[maxN+1];
		qp = new int[maxN+1];
	}
	private void resize(int newSize){
			T[] temp = (T[])new Object[newSize];
			int[] tempI = new int[newSize];
			int[] tempII = new int[newSize];
			maxN = newSize;
			for(int i=0;i<tree.length;i++) {
				temp[i] = tree[i];
				tempI[i] = pq[i];
				tempII[i] = qp[i];
			}
			tree = temp;
			pq = tempI;
			qp = tempII;
	}
	
	private boolean isEmpty() {
		return n == 0;
	}
	
	public void insert(int i, T key) {
		//i is from the has table from the main
		assert maxN >= n;
		assert qp[i] == 0;
		if(i<0 ||i>=maxN) throw new IllegalArgumentException("i is "+ i);
		n++;
        qp[i] = n;
        pq[n] = i;
        tree[n] = key;
        swim(n);
		if(n==maxN)
			resize(n*2);
	}
	public void remove(int i) {
		assert n>0;
		int index = qp[i];
		assert index <= n;
		exch(index, n--);
		swim(index);
		sink(index);
		tree[n+1] = null;
		qp[i] =-1;
	}
	
	public T show(int i) {
		if(qp[i] == -1 )return null;
		return tree[qp[i]];
	}
	
	public T First() {
		return tree[1];
	}
	
	public void reSort(int i) {
		int index = qp[i];
		swim(index);
		sink(index);
	}
	//index is the logical index in the tree(index in qp[])
	private void swim(int k) {
		while(k > 1 && smaller(k, k/2)) {
			exch(k, k/2);
			k = k/2;
		}
	}
	
	private void sink(int k) {
		while(k*2 <= n) {
			int j = findSmaller(k*2, k*2+1);
			if(!smaller(j, k)) break;
			exch(k, j);
			k = j;
		}
	}
	private void exch(int i, int j) {
		if(i == j) return;
		int temp = pq[i];
		pq[i] = pq[j];
		pq[j] = temp;
		qp[pq[i]] = i; 
		qp[pq[j]] = j;
		T tempN = tree[i];
		tree[i] = tree[j];
		tree[j] = tempN;
	}
	private boolean smaller(int child, int parent) {
		if(((Car) tree[child]).compareTo((Car)tree[parent], mode) < 0)
			return true;
		return false;
	}
	private int findSmaller(int left, int right) {
		if(right > n) return left;
		if(smaller(left, right)) return left;
		return right;
	}
	public void showAll() {
		for(int i = 1; i<= n; i++) {
			System.out.println(tree[i]);
		}
	}
}
