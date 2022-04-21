
public class CarManager {
	public MinIndexPQ<Car> allPrice;
	private MinIndexPQ<Car> allMileage;
	private CarMakeModel makemodelprice;
	private CarMakeModel makemodelmileage;
	private VINdlb VIN;
	
	public CarManager() {
		initialize();
	}
	public CarManager(Car[] list) {
		initialize();
		addCar(list);
	}
	private void initialize() {
		allPrice  = new MinIndexPQ<Car>("price");
		allMileage = new MinIndexPQ<Car>("mileage");
		makemodelprice = new CarMakeModel("price");
		makemodelmileage = new CarMakeModel("mileage");
		VIN = new VINdlb();
	}
	
	public void addCar(Car[] list) {
		for(int i =0; i<list.length;i++) {
			addCar(list[i]);
		}
	}
	public boolean addCar(Car car) {
		if(VIN.contains(car.VIN())==-1) {
			int index = VIN.insert(car.VIN());
			car.updateIndex(index);
			allPrice.insert(index, car);
			allMileage.insert(index, car);
			makemodelprice.addCar(car);
			makemodelmileage.addCar(car);
			return true;
		}
		else {
			System.out.println("\nVIN "+car.VIN()+" already exists.");
			return false;
		}
	}
	public boolean removeCar(String VIN) {
		int position = this.VIN.remove(VIN);
		if(position==-1) return false;
		Car car = allPrice.show(position);
		allPrice.remove(position);
		allMileage.remove(position);
		makemodelprice.remove(car.make(), car.model(), position);
		makemodelmileage.remove(car.make(), car.model(), position);
		return true;
	}
	public void showAll(String make, String model) {
		System.out.println("allPrint: ");
		allPrice.showAll();
		System.out.println("\n\nallMileage: ");
		allMileage.showAll();
		System.out.println("\n\nmakemodelPrice: ");
		makemodelprice.showAll(make, model);
		System.out.println("\n\nmakemodelMileage: ");
		makemodelmileage.showAll(make, model);
	}
	
	public boolean updateColor(String VIN, String color) {
		int position = this.VIN.contains(VIN);
		if(position == -1) return false;
		Car car = allPrice.show(position);
		car.updateColor(color);
		return true;
	}
	public boolean updateMileage(String VIN, int newM) {
		int position = this.VIN.contains(VIN);
		if(position == -1) return false;
		Car car = allMileage.show(position);
		car.updateMileage(newM);
		allMileage.reSort(position);
		makemodelmileage.reSort(car.make(), car.model(), position);
		return true;
	}
	public boolean updatePrice(String VIN, int newM) {
		int position = this.VIN.contains(VIN);
		if(position == -1) return false;
		Car car = allPrice.show(position);
		car.updatePrice(newM);
		allPrice.reSort(position);
		makemodelprice.reSort(car.make(), car.model(), position);
		return true;
	}
	
	public Car findMin(String mode, String make, String model){
		if(make == null) {
			if(mode.equals("price"))
				return allPrice.First();
			return allMileage.First();
		}
		else
			if(mode.equals("price"))
				return makemodelprice.first(make, model);
			return makemodelmileage.first(make, model);
				
	}
	public Car find(String VIN) {
		int index = this.VIN.contains(VIN);
		if(index==-1) return null;
		return allPrice.show(index);
	}
}
