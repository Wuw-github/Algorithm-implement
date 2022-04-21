//Wu Wei (wuw4)
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.io.*;

public class RsaSign {
	static final String PUB = "pubkey.rsa";
	static final String PRI = "privkey.rsa";
	
	public static void sign(String filename) {
		LargeInteger Hash = Hash(filename);
		LargeInteger d = null;
		LargeInteger n = null;
		try {
			FileInputStream prifile = new FileInputStream(PRI);
			ObjectInputStream pri = new ObjectInputStream(prifile);
			
			d = (LargeInteger) pri.readObject();
			n = (LargeInteger) pri.readObject();
			pri.close();	
		}catch(FileNotFoundException ex) {
			System.out.println("Cannot find the file 'privkey.rsa'");
			System.exit(0);
		}catch(Exception ee) {
			System.out.println("Cannot open the file 'privkey.rsa' normally");
			System.exit(0);
		}
		if(d == null || n == null) {
			System.out.println("Something wrong with privkey.rsa");
			System.exit(0);
		}
		LargeInteger res = Hash.modularExp(d, n);
		
		String sigfile = filename+".sig";
		try {
			FileOutputStream fi = new FileOutputStream(sigfile);
			ObjectOutputStream f = new ObjectOutputStream(fi);
			f.writeObject(res);
			f.close();
		}catch(Exception e) {
			System.out.println("Cannot generate '.sig' file");
		}
		
	}
	
	public static LargeInteger readSigfile(String filename) {
		LargeInteger verify = null;
		try {
			FileInputStream fi = new FileInputStream(filename);
			ObjectInputStream f = new ObjectInputStream(fi);
			verify = (LargeInteger) f.readObject();
			f.close();
		}catch(FileNotFoundException fe) {
			System.out.println("Cannot find file '"+filename+"'.");
			System.exit(0);
		}catch(Exception x) {
			System.out.println("Fail to read '"+filename+"'.");
			System.exit(0);
		}
		if(verify == null) {
			System.out.println("Something wrong with the file '"+filename+"'.");
			System.exit(0);
		}
		return verify;
	}
	
	public static LargeInteger[] readPubRsa() {
		LargeInteger e = null, n = null;
		try {
			FileInputStream fi = new FileInputStream(PUB);
			ObjectInputStream f = new ObjectInputStream(fi);
			e = (LargeInteger) f.readObject();
			n = (LargeInteger) f.readObject();
			f.close();
		}catch(FileNotFoundException x) {
			System.out.println("Cannot find file 'pubkey.rsa'");
			System.exit(0);
		}catch(Exception y) {
			System.out.println("Cannot open the file 'pubkey.rsa' normally.");
			System.exit(0);
		}
		if(e == null || n == null) {
			System.out.println("Something wrong with 'pubkey.rsa'");
			System.exit(0);
		}
		
		return new LargeInteger[] {e, n};
	}
	
	
	public static void verify(String filename) {
		String sigfile = filename+".sig";
		
		LargeInteger code = readSigfile(sigfile);
		LargeInteger Hash = Hash(filename);
		LargeInteger[] pubs = readPubRsa();
		LargeInteger e = pubs[0];
		LargeInteger n = pubs[1];
		
		LargeInteger calc = code.modularExp(e, n);

		if(calc.subtract(Hash).isZero())
			System.out.println("\nThe signature is valid.");
		else
			System.out.println("\nThe signature is NOT valid.");
	}
	
	
	
	
	public static LargeInteger Hash(String file) {
		// lazily catch all exceptions...
		try {
			// read in the file to hash
			Path path = Paths.get(file);
			byte[] data = Files.readAllBytes(path);

			// create class instance to create SHA-256 hash
			MessageDigest md = MessageDigest.getInstance("SHA-256");

			// process the file
			md.update(data);
			// generate a hash of the file
			byte[] digest = md.digest();

			// print each byte in hex
			/*for (byte b : digest) {
				System.out.print(String.format("%02x", b));
			}
			System.out.println();*/
			return new LargeInteger(digest);
			
		} catch(Exception e) {
			System.out.println("Cannot open the file '"+file+"'.");
			System.exit(0);
		}
		return null;
	}
	
	public static void main(String[] args) {
		if(args.length<2) {
			System.out.println("Invalid Input.");
			return;
		}
		
		if(args[0].equals("s")) 
			sign(args[1]);
		else if(args[0].equals("v"))
			verify(args[1]);
		else
			System.out.println("Invalid Input.");
	}

}
