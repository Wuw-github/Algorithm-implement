//It is a DLB structure for the make and model
public class CarMakeModel {
	private  String mode;
	private CarNode root;
	
	public CarMakeModel(String mode) {
		this.mode = mode;
	}
	public void addCar(Car key) {
		String makemodel = key.make()+"*"+key.model();
		root = put(root, makemodel, 0, key);
	}
	public void addCar(Car[] list) {
		for (int i=0; i<list.length; i++) {
			addCar(list[i]);
		}
	}
	public Car first(String make, String model) {
		String makemodel = make+"*"+model;
		MinIndexPQ<Car> pq= find(root, makemodel, 0);
		if(pq == null) return null;
		return pq.First();
	}
	public void showAll(String make, String model) {
		String makemodel = make+"*"+model;
		MinIndexPQ<Car> pq= find(root, makemodel, 0);
		if(pq == null) return;
		pq.showAll();
	}
	public void remove(String make, String model, int index) {
		String makemodel = make+"*"+model;
		MinIndexPQ<Car> pq= find(root, makemodel, 0);
		assert pq!=null;
		pq.remove(index);
	}
	public void reSort(String make, String model, int index) {
		String makemodel = make+"*"+model;
		MinIndexPQ<Car> pq= find(root, makemodel, 0);
		assert pq!=null;
		pq.reSort(index);
	}
	private CarNode put(CarNode curr, String string, int n, Car key) {
		if(curr == null) {
			curr = new CarNode();
			if(n==string.length()) {
				curr.c = '^';
				curr.mmpq = new MinIndexPQ<Car>(mode);
				curr.mmpq.insert(key.index(), key);
				return curr;
			}
			curr.c = string.charAt(n);
		}
		assert n < string.length();
		if(n == string.length()) {
			curr.c = '^';
			if(curr.mmpq == null) curr.mmpq = new MinIndexPQ<Car>(mode);
			curr.mmpq.insert(key.index(), key);
		}
		else if(string.charAt(n) == curr.c) {
			curr.down = put(curr.down, string, n+1, key);
		}
		else {
			curr.next = put(curr.next, string, n, key);
		}
		return curr;
	}
	private MinIndexPQ<Car> find(CarNode x, String str, int i) {
		if(x == null) return null;
		if(i == str.length())
			if(x.c == '^') return x.mmpq;
		if(x.c == str.charAt(i)) return find(x.down, str, i+1);
		return find(x.next, str, i);
	}
	
	private static class CarNode{
		private MinIndexPQ<Car> mmpq;
		private CarNode next;
		private CarNode down;
		private char c;
		
		public CarNode() {
			
		}
	}
	
}
