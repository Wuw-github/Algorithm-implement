
public class Car {
	private String VIN;
	private String make;
	private String model;
	private String color;
	public int price;
	private int mileage;
	private int index;
	
	public Car(){
	}
	
	public Car(String VIN, String make, String model, String color, int price, int mileage) {
		this.VIN = VIN;
		this.make = make;
		this.model = model;
		this.color = color;
		this.price = price;
		this.mileage = mileage;
	}
	
	public int compareTo(Car ob, String c) {
		if(ob == null) throw new IllegalArgumentException("wrong argument");
		if(c.equals("mileage")) {
			return (this.mileage-ob.mileage);
		}
		else if(c.equals("price")) {
			return (this.price - ob.price);
		}
		else throw new IllegalArgumentException("wrong argument");
	}
	
	public void updatePrice(int newPrice) {
		this.price = newPrice;
	}
	
	public void updateMileage(int newmileage) {
		this.mileage = newmileage;
	}
	
	public void updateColor(String newColor) {
		this.color = newColor;
	}
	public String make() {
		return make;
	}
	public String color() {
		return color;
	}
	public int mileage() {
		return mileage;
	}
	public int price() {
		return price;
	}
	public String model() {
		return model;
	}
	public int index() {
		return index;
	}
	public String VIN() {
		return VIN;
	}
	public void updateIndex(int index) {
		this.index = index;
	}
	public String toString() {
		StringBuilder returnString = new StringBuilder();
		returnString.append("VIN:		" + this.VIN+"\n");
		returnString.append("Make:		" + this.make+"\n");
		returnString.append("Model:		" + this.model+"\n");
		returnString.append("color:		" + this.color+"\n");
		returnString.append("Price:		" + this.price+"\n");
		returnString.append("Mileage:	" + this.mileage+" miles\n");
		returnString.append("Index:	" + this.index);
		return returnString.toString();
	}
}
