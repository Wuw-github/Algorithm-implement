/*************************************************************************
 *  Compilation:  javac LZW.java
 *  Execution:    java LZW - < input.txt   (compress)
 *  Execution:    java LZW + < input.txt   (expand)
 *  Dependencies: BinaryIn.java BinaryOut.java
 *
 *  Compress or expand binary input from standard input using LZW.
 *
 *  WARNING: STARTING WITH ORACLE JAVA 6, UPDATE 7 the SUBSTRING
 *  METHOD TAKES TIME AND SPACE LINEAR IN THE SIZE OF THE EXTRACTED
 *  SUBSTRING (INSTEAD OF CONSTANT SPACE AND TIME AS IN EARLIER
 *  IMPLEMENTATIONS).
 *
 *  See <a href = "http://java-performance.info/changes-to-string-java-1-7-0_06/">this article</a>
 *  for more details.
 *
 *************************************************************************/

public class MyLZW {
    private static final int R = 256;        // number of input chars
    private static int L = 512;       // number of codewords = 2^W
    private static int W = 9;         // codeword width
    private static int data_processed = 0;
    private static int data_generate = 0;
    private static double old_ratio = 0;
    private static double new_ratio = 0;

    public static void compress(String mode) { 
        String input = BinaryStdIn.readString();
        char [] in = input.toCharArray();
        TST<Integer> st = setNewBook();
        int code = R+1;  // R is codeword for EOF
        int currIndex=0, wholeIndex=input.length();
        if(wholeIndex>0){
            if(mode.equals("n"))  BinaryStdOut.write(0, W);
            if(mode.equals("r"))  BinaryStdOut.write(1, W);
            if(mode.equals("m"))  BinaryStdOut.write(2, W);
        }

        while (wholeIndex-currIndex > 0) {
            String s = st.longestPrefixOf(new String(in, currIndex, wholeIndex-currIndex));;  // Find max prefix match s.
            BinaryStdOut.write(st.get(s), W);      // Print s's encoding.
            data_generate += W; data_processed += 8*s.length();
            if(!(code < L) && W == 16){
                if(mode.equals("m")){
                    new_ratio = CompressionRatio(data_processed, data_generate, "compress");
                    if (old_ratio == 0) old_ratio = new_ratio;
                }
                if(mode.equals("r")||mode.equals("m")&& ifReset(old_ratio, new_ratio)){
                    old_ratio = 0;
                    st = setNewBook(); code = R+1;
                    currIndex += s.length();
                    s = st.longestPrefixOf(new String(in, currIndex, wholeIndex-currIndex));
                    BinaryStdOut.write(st.get(s), W); data_generate = W; data_processed = 8*s.length();
                }
            }
            if(!(code < L) && W < 16) L = change_code_length(++W); 
            int t = s.length();
            if (t < (wholeIndex-currIndex) && code < L)    // Add s to symbol table.
                st.put(new String(in, currIndex, t+1), code++);
            currIndex += t;
        }
        BinaryStdOut.write(R, W);
        BinaryStdOut.close();
    } 

    private static boolean ifReset(double oldRatio, double newRatio){
        if(newRatio == 0) return false;
        if(oldRatio/newRatio >= 1.1){
            return true;
        }
        return false;
    }
    private static double CompressionRatio(int processed, int generate, String mode){
        if(mode.equals("compress")){
            return (double)processed/generate;
        } 
        return (double)generate/processed;
    }

    private static TST<Integer> setNewBook(){
        TST<Integer> st = new TST<Integer>();
        for (int i = 0; i < R; i++)
            st.put("" + (char) i, i);
        L = 512;
        W = 9;
        return st;
    }

    public static void expand() {
        String[] st = new String[65536];
        int i;
        for (i = 0; i < R; i++)
            st[i] = "" + (char) i;
        st[i++] = "";

        int mode = BinaryStdIn.readInt(W);
        if (mode == R) return;           // expanded message is empty string

        int codeword = BinaryStdIn.readInt(W);
        String val = st[codeword];

        while (true) {
            BinaryStdOut.write(val);
            if(mode == 2) data_generate += 8*val.length();
            if(!(i < L) && W < 16) L = change_code_length(++W);
            if(!(i < L) && W == 16){
                if (mode ==2){
                    new_ratio = CompressionRatio(data_processed, data_generate, "expand");
                    if (old_ratio == 0) old_ratio = new_ratio;
                }
                if(mode==1||mode==2&&ifReset(old_ratio, new_ratio)){
                    old_ratio = 0;
                    i = R+1; L = 512; W = 9;
                    codeword = BinaryStdIn.readInt(W);
                    val = st[codeword];
                    BinaryStdOut.write(val);
                    if(mode == 2) {data_generate = 8*val.length(); data_processed = W; }
                }
            }
            codeword = BinaryStdIn.readInt(W);
            if(mode == 2)  data_processed += W;
            if (codeword == R) break;
            String s = st[codeword];
            if (i == codeword) s = val + val.charAt(0);   // special case hack
            if (i < L) st[i++] = val + s.charAt(0);
            val = s;
        }
        BinaryStdOut.close();
    }

    private static int change_code_length(int W){
        return change_code_length(2, W);
    }

    private static int change_code_length(int base, int exp){
        if(exp==0) return 1;
        return base*(change_code_length(base, exp-1));
    }

    public static void main(String[] args){
        if(args[0].equals("-")) {
            try{
                compress(args[1]);
            }catch(ArrayIndexOutOfBoundsException e){
                System.err.println("Invalid input");
            }
        }
        else if (args[0].equals("+")) expand();
        else throw new IllegalArgumentException("Illegal command line argument");
    }
}