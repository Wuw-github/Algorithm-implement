import java.io.*;
import java.util.*;
public class CarTracker {
	static CarManager cars;
	static Scanner sc;
	public static void main(String[] args) {
		
		cars = new CarManager();
		sc = new Scanner(System.in);
		Car[] list = readfile();
		String input = "";
		cars.addCar(list);
		
		System.out.println("Welcome to the Car Market!!!");
		do {
			System.out.println("\n1) Add a Car");
			System.out.println("2) Update a Car");
			System.out.println("3) Remove a Car");
			System.out.println("4) Get the lowest Price Car");
			System.out.println("5) Get the lowest Mileage Car");
			System.out.println("6) Get the lowest Price Car by specific make and model");
			System.out.println("7) Get the lowest Mileage Car by specific make and model");
			System.out.println("8) Quit");
			System.out.print("Please select a number(1-8): ");
			input = sc.nextLine();
			
			if(input.equals("1"))
				add();
			else if(input.equals("2"))
				update();
			else if(input.equals("3"))
				remove();
			else if(input.equals("4"))
				lowestP();
			else if(input.equals("5"))
				lowestM();
			else if(input.equals("6"))
				lowestPmm();
			else if(input.equals("7"))
				lowestMmm();
			else if(input.equals("8")) {}
			else
				System.out.println("\nInvalid Input!");
			
		}while(!input.equals("8"));
		System.out.println("\nThank you for using! See you next time!");
	}
	public static Car[] readfile() {
		Scanner fsc;
		String[] Carinfo;
		Car[] cars = new Car[10];
		int n = 0;
		try {
			File fileIn = new File("cars.txt");
			fsc = new Scanner(fileIn);
		}catch (FileNotFoundException e) {
			System.out.println("Cannot open the file 'cars.txt'.");
			return null;
		}
		fsc.nextLine();
		while(fsc.hasNext()) {
			Carinfo = fsc.nextLine().split(":");
			if(n>=cars.length) cars = resize(cars);
			cars[n++] = new Car(Carinfo[0], Carinfo[1], Carinfo[2], Carinfo[5], Integer.parseInt(Carinfo[3]), Integer.parseInt(Carinfo[4]));
		}
		cars = adjust(cars, n);
		fsc.close();
		return cars;
	}
	private static Car[] resize(Car[] old) {
		Car[] newA = new Car[old.length*2];
		for(int i=0; i< old.length;i++)
			newA[i] = old[i];
		return newA;
	}
	private static Car[] adjust(Car[] old, int n) {
		if(n == old.length) return old;
		assert n < old.length;
		Car[] newA = new Car[n];
		for(int i = 0; i< n;i++)
			newA[i] = old[i];
		return newA;
	}
	public static void add() {
		String VIN = askVIN();
		if(VIN.equals("-1")) return;
		String make = askMake();
		if(make.equals("-1")) return;
		String model = askModel();
		if(model.equals("-1")) return;
		String color = askColor();
		if(color.equals("-1")) return;
		int price = askPrice();
		if(price == -1) return;
		int mileage = askMileage();
		if(mileage == -1) return;
		boolean isAdd=cars.addCar(new Car(VIN, make, model, color, price, mileage));
		if(isAdd)
			System.out.println("\nSuccessfully add Car: "+ VIN);
		}
	
	public static void update() {
		System.out.println("	Which property would you want to change: ");
		System.out.println("		1) Price");
		System.out.println("		2) Mileage");
		System.out.println("		3) Color");
		System.out.println("		Any other number to Main Menu");
		System.out.print("Please select a choice: ");
		String input = sc.nextLine();
		if(input.equals("1"))
			updatePrice();
		else if(input.equals("2"))
			updateMileage();
		else if(input.equals("3"))
			updateColor();
	}
	public static void updatePrice() {
		String VIN = askVIN();
		if(VIN.equals("-1")) return;
		int price = askPrice();
		if(price == -1) return;
		if(cars.updatePrice(VIN, price))
			System.out.println("\nUpdate complete.");
		else
			System.out.println("\nCar does not exist.");
	}
	public static void updateMileage() {
		String VIN = askVIN();
		if(VIN.equals("-1")) return;
		int mileage = askMileage();
		if(mileage == -1) return;
		if(cars.updateMileage(VIN, mileage))
			System.out.println("\nUpdate complete.");
		else
			System.out.println("\nCar does not exist.");
	}
	public static void updateColor() {
		String VIN = askVIN();
		if(VIN.equals("-1")) return;
		String color = askColor();
		if(color.equals("-1")) return;
		if(cars.updateColor(VIN, color))
			System.out.println("\nUpdate complete.");
		else
			System.out.println("\nCar does not exist.");
	}
	public static void remove() {
		String VIN = askVIN();
		if(VIN.equals("-1")) return;
		if(cars.removeCar(VIN))
			System.out.println("\nCar "+VIN+" has been removed.");
		else
			System.out.println("\nCar does not exist.");
	}
	
	public static void lowestP() {
		Car car = cars.findMin("price", null, null);
		if(car == null) {
			System.out.println("\nNo car avaliable.");
			return;
		}
		System.out.println("\nHere is the car with the lowest price:\n"+ car);
	}
	public static void lowestM() {
		Car car = cars.findMin("mileage", null, null);
		if(car == null) {
			System.out.println("\nNo car avaliable.");
			return;
		}
		System.out.println("\nHere is the car with the lowest mileage:\n"+ car);
	}
	
	public static void lowestPmm() {
		String make = askMake();
		if(make.equals("-1")) return;
		String model = askModel();
		if(model.equals("-1")) return;
		Car car = cars.findMin("price", make, model);
		if(car!=null) {
			System.out.println("\nHere is the "+make+" "+model+" with lowest price:\n"+ car);
			return;
		}
		System.out.println("\nNo "+make+" "+model+" avaliable.");
	}
	
	public static void lowestMmm() {
		String make = askMake();
		if(make.equals("-1")) return;
		String model = askModel();
		if(model.equals("-1")) return;
		Car car = cars.findMin("mileage", make, model);
		if(car!=null) {
			System.out.println("\nHere is the "+make+" "+model+" with lowest mileage:\n"+ car);
			return;
		}
		System.out.println("\nNo "+make+" "+model+" avaliable.");
	}
	public static int askPrice() {
		System.out.print("Please enter the Price (Any Negative number or String to quit): ");
		String input = sc.nextLine();
		try {
			return Integer.parseInt(input);
		}catch(Exception e) {
			System.out.println("- Quit.");
			return -1;
		}
	}
	
	public static int askMileage() {
		System.out.print("Please enter the Mileage (Any Negative number or String to quit): ");
		String input = sc.nextLine();
		try {
			return Integer.parseInt(input);
		}catch(Exception e) {
			System.out.println("- Quit.");
			return -1;
		}
	}
	
	public static String askColor() {
		System.out.print("Please enter the Color (0 to quit): ");
		String input = sc.nextLine();
		try {
			Integer.parseInt(input);
			System.out.println("- Quit.");
			return "-1";
		}catch(Exception e) {
			return input;
		}
	}
	public static String askVIN() {
			System.out.print("Please enter the 17-digit VIN number(0 to quit): ");
			String input = sc.nextLine();
			if(input.equals("0")) return "-1";
			return input;
	}
	public static String askMake() {
		System.out.print("Please enter the make (0 to quit): ");
		String input = sc.nextLine();
		if(input.equals("0")) return "-1";
		return input;
	}
	public static String askModel() {
		System.out.print("Please enter the model (0 to quit): ");
		String input = sc.nextLine();
		if(input.equals("0")) return "-1";
		return input;
	}
}
