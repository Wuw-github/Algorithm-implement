//Wu Wei (wuw4)
import java.io.*;
import java.text.DecimalFormat;
import java.util.*;
public class ac_test {
	public static void main(String[] args) throws IOException{
		DLBTrie t = new DLBTrie();
		Scanner	sc=null,input=null;
		try{
			File fileIn =new File("dictionary.txt");
			sc = new Scanner(fileIn);
			input = new Scanner(System.in);
		}catch(FileNotFoundException e){
			System.out.println("File does not exist.");
			System.exit(0);
		}
		
		StringBuilder word = new StringBuilder("");
		StringBuilder[] history = new StringBuilder[10];

		readFile(t, sc);
		sc.close();
		//create new file
		File newFile = new File("user_history.txt");
		if (!newFile.exists())
			newFile.createNewFile();
		else{
			Scanner historyRead = new Scanner(newFile);
			history = readFilefromhistory(history, historyRead);
			historyRead.close();
		}

		
		StringBuilder[] arrayFromDLB=null;
		StringBuilder[] arrayFromHis=null;
		StringBuilder[] prediction=null;
		double[] timeArray = new double[10];
		while(true){
			if(word.toString().equals(""))
				System.out.print("Enter your first character: ");
			else
				System.out.print("Enter your next character: ");
			String character = input.nextLine();

			while(!character.equals("!")&&!character.equals("$")&&!isInteger(character)){
				try{
					if(character.length()>1){
						System.out.println("Please input one character each time!");
						break;
					}
					word.append(character);
					double nano = System.nanoTime();
					arrayFromDLB = t.showPrediction(word.toString());
					arrayFromHis = checkPrefix(history, word);
					prediction = checkContains(arrayFromHis, arrayFromDLB);
					double nanoTime = (System.nanoTime()-nano)/1000000000;
					timeArray = addTime(timeArray, nanoTime);
					System.out.println("\n("+new DecimalFormat("#.######").format(nanoTime)+" s)");
					if(prediction==null||prediction[0]==null)
						System.out.println("Cannot find any pridictions.\n");
					else{
						System.out.println("Predictions:");
						for(int i=0;i<5;i++) {
							if(prediction[i]!=null)
								System.out.print("   ("+(i+1)+") "+ prediction[i]);
						}
						System.out.println("\n");
					}
						
					System.out.print("Enter your next character: ");
					character = input.nextLine();
				}catch(Exception e){
					System.out.println("Invalid Input!");
					break;
				}	
			}
			if(character.equals("$")){
				System.out.println("   WORD COMPLETED:  "+ word+"\n");
				history = addToArray(history, new StringBuilder(word));
				word = new StringBuilder("");
			}
			if(isInteger(character)){
				try{
					String show= prediction[Integer.parseInt(character)-1].toString();
					System.out.println("   WORD COMPLETED:  "+show +"\n");
					word = new StringBuilder("");
					history= addToArray(history, new StringBuilder(prediction[Integer.parseInt(character)-1].toString()));
				}catch(ArrayIndexOutOfBoundsException e){
					System.out.println("\nInvalid selection! Procedure continue...\n");
				}catch(NullPointerException e){
					System.out.println("\nPlease reenter the character\n");
				}
				
			}
			if(character.equals("!")){
				int n=numberOfObject(history);
				PrintWriter fileOut = new PrintWriter(newFile);
				for(int i=0;i<n;i++){
					fileOut.println(history[i].toString());
				}
				fileOut.close();
				System.out.println("\nAverage time: "+new DecimalFormat("#.######").format(calculateAverage(timeArray))+" s");
				System.out.println("Bye!");
				System.exit(0);
			}
		}
	}

	static double[] addTime(double[] array, double number){
		int n = numberOfObject(array);
		if(n == array.length)
			array = doubleArray(array);
		array[n] = number;
		return array;
	}

	static int numberOfObject(double[] array){
		int i=0;
		for(double item: array){
			if(item!=0)
				i++;
		}
		return i;
	}

	static double calculateAverage(double[] array){
		int n = numberOfObject(array);
		double sum =0;
		for(int i=0;i<n;i++){
			sum+=array[i];
		}
		return sum/n;
	}

	static StringBuilder[] checkPrefix(StringBuilder[] array, StringBuilder prefix){
		StringBuilder[] returnArray = new StringBuilder[5];
		int n=0;
		for(int i = numberOfObject(array)-1;i>=0;i--){
			if(n<5&&contains(array[i], prefix)){
				returnArray[n]=array[i];
				n++;
			}
		}
		return returnArray;
	}

	private static boolean contains(StringBuilder str, StringBuilder prefix){
		if(prefix.length()>str.length())
			return false;
		int i=0;
		while(i<prefix.length()){
			if(str.charAt(i)!=prefix.charAt(i))
				return false;
			i++;
		}
		return true;
	}

	static StringBuilder[] addToArray(StringBuilder[] array, StringBuilder str){
		if(checkContains(array, str))
			return array;
		int i=numberOfObject(array);
		if(i==array.length)
			array =doubleArray(array);
		array[i]=str;
		return array;
	}
	static int numberOfObject(Object[] array){
		int i=0;
		while(array[i]!=null&&i<array.length){
			i++;
		}
		return i;
	} 
	

	static void readFile(DLBTrie t, Scanner sc) {
		while(sc.hasNext()) {
			t.put(sc.nextLine());
		}
	}
	static StringBuilder[] readFilefromhistory(StringBuilder[] array, Scanner sc){
		int i =0;
		while(sc.hasNext()){
			if(i==array.length) array = doubleArray(array);
			array[i]=new StringBuilder(sc.nextLine());
			i++;
		}
		return array;
	}

	static StringBuilder[] checkContains(StringBuilder[] arrayFirst, StringBuilder[] arraySecond){

		StringBuilder[] returnArray = new StringBuilder[5];
		int n=0;
		for (int i=0;i<5;i++){
			if(arrayFirst[i]!=null){
				returnArray[i]=arrayFirst[i];
				n++;
			}
		}
		if(arraySecond==null) return returnArray;
		if(n==5)
			return returnArray;
		int i=0;
		while(n<5&&i<5&&arraySecond[i]!=null){
			if(checkContains(returnArray, arraySecond[i]))
				i++;
			else{
				returnArray[n]=arraySecond[i];
				n++;i++;}
		}
		if(n==0)
			return null;
		return returnArray;
	}
	static boolean checkContains(StringBuilder[] array, StringBuilder str){
		for(int i=0;i<numberOfObject(array);i++){
			if(array[i].toString().equals(str.toString()))
				return true;
		}
		return false;
	}

	static StringBuilder[] doubleArray(StringBuilder[] array){
		StringBuilder[] newArray = new StringBuilder[array.length*2];
		for(int i=0; i<array.length;i++){
			newArray[i]=array[i];
		}
		return newArray;
	}

	static double[] doubleArray(double[] array){
		double[] newArray = new double[array.length*2];
		for(int i=0; i<array.length;i++){
			newArray[i]=array[i];
		}
		return newArray;
	}

	static boolean isInteger(String s) {
	    try { 
	        Integer.parseInt(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    } catch(NullPointerException e) {
	        return false;
	    }
	    // only got here if we didn't return false
	    return true;
	}
}
