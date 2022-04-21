//Wu Wei (wuw4)
import java.util.Random;
import java.io.*;

public class RsaKeyGen {
	static final LargeInteger ONE = new LargeInteger(new byte[] {(byte)1});
	static final LargeInteger TWO = new LargeInteger(new byte[] {(byte)2});
	static final LargeInteger THREE = new LargeInteger(new byte[] {(byte)3});
	static final String PUB = "pubkey.rsa";
	static final String PRI = "privkey.rsa";
	
	public static void main(String[] args) {
		
		Random rn = new Random();
		LargeInteger ONE = new LargeInteger(new byte[] {(byte)1});
		LargeInteger TWO = new LargeInteger(new byte[] {(byte)2});
		LargeInteger THREE = new LargeInteger(new byte[] {(byte)3});
		LargeInteger p;
		LargeInteger q;
		LargeInteger n;
		LargeInteger e;
		LargeInteger d;
		LargeInteger phi_n;
		do {
			p = new LargeInteger(256, rn);
			q = new LargeInteger(256, rn);
			n = p.multiply(q);
			phi_n = p.subtract(ONE).multiply(q.subtract(ONE));
		}while(phi_n.subtract(THREE).isNegative());
		
		e = new LargeInteger(new byte[] {(byte)1});
		LargeInteger[] gcds;
		do {
			e = e.add(TWO);
			gcds = phi_n.XGCD(e);
		}while(!gcds[0].subtract(ONE).isZero());
		d = gcds[2];
		if(d.isNegative()) d= d.add(phi_n);

		createfile(e, n, d);
	}

	
	public static void createfile(LargeInteger e, LargeInteger n, LargeInteger d) {
		try {
			FileOutputStream pubout = new FileOutputStream(PUB);
			ObjectOutputStream pub = new ObjectOutputStream(pubout);
			pub.writeObject(e);
			pub.writeObject(n);
			pub.close();
		}catch(Exception ee) {
			System.out.println("Fail to create file 'pubkey.rsa'");
		}
		
		try {
			FileOutputStream privout = new FileOutputStream(PRI);
			ObjectOutputStream pri = new ObjectOutputStream(privout);
			pri.writeObject(d);
			pri.writeObject(n);
			pri.close();
		}catch(Exception ee) {
			System.out.println("Fail to create file 'privkey.rsa'");
		}
	}
}
