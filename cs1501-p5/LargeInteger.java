//Wu Wei (wuw4)
import java.util.Random;
import java.math.BigInteger;
import java.io.Serializable;

public class LargeInteger implements Serializable {
	
	private final byte[] ONE = {(byte) 1};
	private final byte[] ZERO = {(byte) 0};

	private byte[] val;

	/**
	 * Construct the LargeInteger from a given byte array
	 * @param b the byte array that this LargeInteger should represent
	 */
	public LargeInteger(byte[] b) {
		val = b;
	}

	/**
	 * Construct the LargeInteger by generatin a random n-bit number that is
	 * probably prime (2^-100 chance of being composite).
	 * @param n the bitlength of the requested integer
	 * @param rnd instance of F to use in prime generation
	 */
	public LargeInteger(int n, Random rnd) {
		val = BigInteger.probablePrime(n, rnd).toByteArray();
	}
	
	/**
	 * Return this LargeInteger's val
	 * @return val
	 */
	public byte[] getVal() {
		return val;
	}

	/**
	 * Return the number of bytes in val
	 * @return length of the val byte array
	 */
	public int length() {
		return val.length;
	}

	/** 
	 * Add a new byte as the most significant in this
	 * @param extension the byte to place as most significant
	 */
	public void extend(byte extension) {
		byte[] newv = new byte[val.length + 1];
		newv[0] = extension;
		for (int i = 0; i < val.length; i++) {
			newv[i + 1] = val[i];
		}
		val = newv;
	}

	/**
	 * If this is negative, most significant bit will be 1 meaning most 
	 * significant byte will be a negative signed number
	 * @return true if this is negative, false if positive
	 */
	public boolean isNegative() {
		return (val[0] < 0);
	}

	/**
	 * Computes the sum of this and other
	 * @param other the other LargeInteger to sum with this
	 */
	public LargeInteger add(LargeInteger other) {
		byte[] a, b;
		// If operands are of different sizes, put larger first ...
		if (val.length < other.length()) {
			a = other.getVal();
			b = val;
		}
		else {
			a = val;
			b = other.getVal();
		}

		// ... and normalize size for convenience
		if (b.length < a.length) {
			int diff = a.length - b.length;

			byte pad = (byte) 0;
			if (b[0] < 0) {
				pad = (byte) 0xFF;
			}

			byte[] newb = new byte[a.length];
			for (int i = 0; i < diff; i++) {
				newb[i] = pad;
			}

			for (int i = 0; i < b.length; i++) {
				newb[i + diff] = b[i];
			}

			b = newb;
		}

		// Actually compute the add
		int carry = 0;
		byte[] res = new byte[a.length];
		for (int i = a.length - 1; i >= 0; i--) {
			// Be sure to bitmask so that cast of negative bytes does not
			//  introduce spurious 1 bits into result of cast
			carry = ((int) a[i] & 0xFF) + ((int) b[i] & 0xFF) + carry;

			// Assign to next byte
			res[i] = (byte) (carry & 0xFF);

			// Carry remainder over to next byte (always want to shift in 0s)
			carry = carry >>> 8;
		}

		LargeInteger res_li = new LargeInteger(res);
	
		// If both operands are positive, magnitude could increase as a result
		//  of addition
		if (!this.isNegative() && !other.isNegative()) {
			// If we have either a leftover carry value or we used the last
			//  bit in the most significant byte, we need to extend the result
			if (res_li.isNegative()) {
				res_li.extend((byte) carry);
			}
		}
		// Magnitude could also increase if both operands are negative
		else if (this.isNegative() && other.isNegative()) {
			if (!res_li.isNegative()) {
				res_li.extend((byte) 0xFF);
			}
		}

		// Note that result will always be the same size as biggest input
		//  (e.g., -127 + 128 will use 2 bytes to store the result value 1)
		return res_li;
	}

	/**
	 * Negate val using two's complement representation
	 * @return negation of this
	 */
	public LargeInteger negate() {
		byte[] neg = new byte[val.length];
		int offset = 0;

		// Check to ensure we can represent negation in same length
		//  (e.g., -128 can be represented in 8 bits using two's 
		//  complement, +128 requires 9)
		if (val[0] == (byte) 0x80) { // 0x80 is 10000000
			boolean needs_ex = true;
			for (int i = 1; i < val.length; i++) {
				if (val[i] != (byte) 0) {
					needs_ex = false;
					break;
				}
			}
			// if first byte is 0x80 and all others are 0, must extend
			if (needs_ex) {
				neg = new byte[val.length + 1];
				neg[0] = (byte) 0;
				offset = 1;
			}
		}

		// flip all bits
		for (int i  = 0; i < val.length; i++) {
			neg[i + offset] = (byte) ~val[i];
		}

		LargeInteger neg_li = new LargeInteger(neg);
	
		// add 1 to complete two's complement negation
		return neg_li.add(new LargeInteger(ONE));
	}

	/**
	 * Implement subtraction as simply negation and addition
	 * @param other LargeInteger to subtract from this
	 * @return difference of this and other
	 */
	public LargeInteger subtract(LargeInteger other) {
		return this.add(other.negate());
	}

	
	
	/**
	 * Compute the product of this and other
	 * @param other LargeInteger to multiply by this
	 * @return product of this and other
	 */
	public LargeInteger multiply(LargeInteger other) {
		LargeInteger a, b;
		boolean is_neg = (this.isNegative() || other.isNegative()) && !(this.isNegative() && other.isNegative());
		if(this.isNegative()) a = this.negate();
		else a = this;
		if(other.isNegative()) b = other.negate();
		else b = other;
		
		
		a = extendcopy(a, b.getVal().length);
		byte[] multer = b.getVal();
		LargeInteger res = new LargeInteger(new byte[] {0x00});
		//calculate using gradeschool method
		for(int i = multer.length-1; i>=0; i--) {
			byte curr = multer[i];
			for(int j=0; j<8; j++) {
				if(((curr>>j)&1) == 1) {
					res = res.add(a);
				}
				a = shiftLeft(a, 1);
			}
		}
		
		if(is_neg)
			res = res.negate();
		res.getOffAddition();
		return res;
	}
	
	/**
	 * Run the extended Euclidean algorithm on this and other
	 * @param other another LargeInteger
	 * @return an array structured as follows:
	 *   0:  the GCD of this and other
	 *   1:  a valid x value
	 *   2:  a valid y value
	 * such that this * x + other * y == GCD in index 0
	 */
	 public LargeInteger[] XGCD(LargeInteger other) {
		if(other.isZero()) 
			return(new LargeInteger[] {this, new LargeInteger(ONE), new LargeInteger(ZERO)});
		
		LargeInteger[] vals = other.XGCD(this.mod(other));
		LargeInteger gcd = vals[0];
		LargeInteger x = vals[2];
		LargeInteger temp = this.divide(other)[0].multiply(vals[2]);
		LargeInteger y = vals[1].subtract(temp);
		gcd.getOffAddition();
		x.getOffAddition();
		y.getOffAddition();
		return new LargeInteger[] {gcd, x, y};
	 }
	 
	 public LargeInteger mod(LargeInteger other) {
		 if(other.isNegative()) other = other.negate();
		 return this.divide(other)[1];
	 }

	 /**
	  * calculate this/other
	  * [0]: quotient
	  * [1]: remainder
	  */
	 public LargeInteger[] divide(LargeInteger other) {
		LargeInteger a, b;
		boolean is_neg = (this.isNegative() || other.isNegative()) && !(this.isNegative() && other.isNegative());
		boolean rem_is_neg = this.isNegative();
		
		if(this.isNegative()) a = this.negate();
		else a = this;
		if(other.isNegative()) b = other.negate();
		else b = other;
		
		LargeInteger[] component = forDivide(a.getVal(), b.getVal());
		LargeInteger remainder = component[0];
		LargeInteger divisor = component[1];
		
		int length = divisor.getVal().length/2;
		int loop = length*8+1;
		
		LargeInteger quotient = new LargeInteger(getQuotientArray(length));
		
		int i=0;
		
		while(i<loop) {
			if(remainder.isBiger(divisor)) {
				remainder = remainder.subtract(divisor);
				quotient = shiftLeft(quotient, 1);
				quotient = quotient.add(new LargeInteger(ONE));
			}
			else {
				quotient = shiftLeft(quotient, 1);
			}
			divisor = shiftRight(divisor, 1);
			i++;
		}
		if(is_neg) quotient = quotient.negate();
		if(rem_is_neg) remainder = remainder.negate();
		
		quotient.getOffAddition();
		remainder.getOffAddition();
		
		return new LargeInteger[]{quotient, remainder};
	 }
	 private byte[] getQuotientArray(int length) {
		 byte[] newarray = new byte[length];
		 for(int i=0; i<length; i++)
			 newarray[i] = 0x00;
		 return newarray;
	 }
	 
	 /**
	  * Compute the result of raising this to the power of y mod n
	  * @param y exponent to raise this to
	  * @param n modulus value to use
	  * @return this^y mod n
	  * based on the mathematic relationship: (x*y) mod n = (x mod n)*(y mod n) mod n
	  * shorten the actual exponential length
	  */
	 
	 public LargeInteger modularExp(LargeInteger y, LargeInteger n) {
		 if(y.isNegative()) y = y.negate();
		 return modularExpR(y, n);
	 }

	 public LargeInteger modularExpR(LargeInteger exp, LargeInteger n) {

		 if(exp.isZero())
			 return new LargeInteger(ONE);
		 if(exp.subtract(new LargeInteger(ONE)).isZero())
			 return this.mod(n);
		 byte[] array = exp.getVal();
		 LargeInteger newExp = shiftRight(exp, 1);
		 if((array[array.length-1] & 0x01) == 1) {
			 //odd number
			 LargeInteger half = modularExpR(newExp, n);
			 return half.multiply(half).multiply(this).mod(n);
		 }
		 else {
			 //even number
			 LargeInteger half = modularExpR(newExp, n);
			 return half.multiply(half).mod(n);
		 }
	 }
	 
	 
	 //Other useful methods
	 
	 
	 //compare if this is biger than LargeInteger other
	 private boolean isBiger(LargeInteger other) {
		 return !this.subtract(other).isNegative();
	 }
	 
	 //return an array of LargeInteger to make sure dividend and divisor has the same length
	 private LargeInteger[] forDivide(byte[] dividend, byte[] divisor) {
		 byte[] newdividend, newdivisor;
		 if(dividend.length>divisor.length) {
			 int length = dividend.length;
			 int diff = length - divisor.length;
			 newdividend = new byte[length*2];
			 newdivisor = new byte[length*2];
			 for(int i=0; i<length; i++) {
				 newdividend[i]=0x00;
				 newdividend[i+length] = dividend[i];
			 }
			 for(int i=0; i<diff; i++)
				 newdivisor[i] = 0x00;
			 for(int i=0; i<divisor.length; i++)
				 newdivisor[i+diff] = divisor[i];
			 for(int i=0; i<length; i++)
				 newdivisor[i+length] = 0x00;
		 }
		 else {
			 int length = divisor.length;
			 int diff = length - dividend.length;
			 newdividend = new byte[length*2];
			 newdivisor = new byte[length*2];
			 for(int i=0; i<length; i++) {
				 newdivisor[i] = divisor[i];
				 newdivisor[i+length] = 0x00;
			 }
			 for(int i=0; i< length+diff; i++)
				 newdividend[i] = 0x00;
			 for(int i=0; i< dividend.length; i++)
				 newdividend[i+length+diff] = dividend[i];
		 }
		 LargeInteger Divisor = new LargeInteger(newdivisor);
		 LargeInteger Dividend = new LargeInteger(newdividend);
		 return new LargeInteger[] {Dividend, Divisor};
	 }
	 

	 private LargeInteger extendcopy(LargeInteger old, int extend) {
			byte[] oldarray = old.getVal();
			byte[] newarray = new byte[oldarray.length+extend];
			byte front = (byte) 0;
			if(old.isNegative()) front = (byte) 0xFF;
			for(int i = 0; i<extend; i++) {
				newarray[i] = front;
			}
			for(int i = 0; i<oldarray.length; i++) {
				newarray[i+extend] = oldarray[i];
			}
			return new LargeInteger(newarray);
		}
		
	//funtion for getting rid of the additional byte
		private void getOffAddition() {
			byte[] oldarray = this.getVal();
			if(oldarray.length ==1) return;
			int num = 0;
			int index = 0;
			if(this.isNegative()) {
				while(true) {
					if(index<oldarray.length-1 && oldarray[index] == -1 && oldarray[index+1] < 0) {
						num++;
						index++;
					}
					else break;
				}
			}
			else {
				while(true) {
					if(index<oldarray.length-1 && oldarray[index] == 0 && oldarray[index+1] >= 0) {
						num++;
						index++;
					}
					else break;
				}
			}
			byte[] newarray = new byte[oldarray.length-num];
			for(int i=0; i<newarray.length; i++) {
				newarray[i] = oldarray[i+num];
			}
			this.val = newarray;
		}

		//shift lefth the ob by amount bits
		public LargeInteger shiftLeft(LargeInteger ob, int amount) {
			if(amount>4) {
				System.out.println("Do not support shift larger than 4 bits.");
				return null;
			}
			byte[] oldarray = ob.getVal();
			byte[] newarray = new byte[oldarray.length];
			byte[] shiftcarry = {(byte)0x00, (byte)0x80, (byte)0xC0, (byte)0xE0, (byte)0xF0};
			for(int i=0; i<oldarray.length;i++) {
				newarray[i] = (byte) (oldarray[i] << amount);
				if(i!=oldarray.length-1) {
					int carry = (oldarray[i+1] & shiftcarry[amount])<<24;
					carry >>>= (32-amount);
					newarray[i] |= carry;
				}
			}
			return new LargeInteger(newarray);
		}

		//shift right
		public LargeInteger shiftRight(LargeInteger ob, int amount) {
			if(amount>4) {
				System.out.println("Do not support shift larger than 4 bits.");
				return null;
			}
			byte[] oldarray = ob.getVal();
			byte[] newarray = new byte[oldarray.length];
			byte[] shiftcarry = {(byte)0x00, 0x01, 0x03, 0x07, 0x0F};
			for(int i = oldarray.length-1; i>=0; i--) {
				int val = (oldarray[i]<< 24) >>> (24 + amount);
				newarray[i] = (byte)val;
				if(i!=0) {
					int carry = (oldarray[i-1] & shiftcarry[amount]);
					carry <<= (8-amount);
					newarray[i] |= carry;
				}
			}
			return new LargeInteger(newarray);
		}
		
	//if this is equal to Zero
	public boolean isZero() {
		return !this.isNegative() && this.subtract(new LargeInteger(ONE)).isNegative();
	 }
		 
	 public String toString() {
		 StringBuilder st = new StringBuilder();
		 for(int i = 0; i<val.length; i++) {
			 st.append(val[i]+" ");
		 }
		 return st.toString();
	 }
}
