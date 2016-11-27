package moritz.closed;

import moritz.current.Utility;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;
import java.util.Stack;
import java.util.stream.IntStream;

/**
 * Created by Moritz on 11/22/2016.
 * <p></p>
 */
public class Sandbox {
    private static int instructionMask = 0b111111; //Also terminating instruction
    private static BigInteger bint = BigInteger.valueOf(0b010111011011);
    private static int[] sampleInstructions = {0b01010, 0b11101, 0b01100, 0b01010, 0b00100, 0b11101};

    public static void main(String[] args) {
        Utility.initialize();
        Stack<Integer> example1 = new Stack<>();
        Stack<Integer> example2 = new Stack<>();
//        String str1 = "idealscore playermap wpattern oppmap playermap oppmap wpattern | bitcount maxlinebits playermap oppmap wpattern 10 wpattern wpattern 10 + - @ / playermap wpattern oppmap wpattern oppmap playermap wpattern wpattern playermap oppmap oppmap playermap oppmap wpattern oppmap oppmap playermap oppmap wpattern oppmap wpattern wpattern oppmap wpattern wpattern playermap playermap wpattern oppmap oppmap playermap wpattern wpattern wpattern playermap playermap playermap oppmap oppmap playermap oppmap playermap playermap oppmap oppmap oppmap wpattern wpattern oppmap wpattern wpattern wpattern playermap oppmap wpattern oppmap playermap oppmap oppmap wpattern playermap wpattern wpattern wpattern playermap playermap playermap playermap wpattern oppmap wpattern wpattern oppmap wpattern wpattern playermap oppmap oppmap wpattern oppmap playermap playermap idealscore playermap wpattern oppmap playermap oppmap wpattern | bitcount maxlinebits playermap oppmap wpattern 10 wpattern wpattern 10 + - @ / playermap wpattern oppmap wpattern oppmap playermap wpattern wpattern playermap oppmap oppmap playermap oppmap wpattern oppmap oppmap playermap oppmap wpattern oppmap wpattern wpattern oppmap wpattern wpattern playermap playermap wpattern oppmap oppmap playermap wpattern wpattern wpattern playermap playermap playermap oppmap oppmap playermap oppmap playermap playermap oppmap oppmap oppmap wpattern wpattern oppmap wpattern wpattern wpattern playermap oppmap wpattern oppmap playermap oppmap oppmap wpattern playermap wpattern wpattern wpattern playermap playermap playermap playermap wpattern oppmap wpattern wpattern oppmap wpattern wpattern playermap oppmap oppmap wpattern oppmap playermap playermap";
        String str1 = "playermap wpattern ^ bitcount oppmap";
        String str2 = "playermap wpattern ^ bitcount oppmap";
        String[] labels1 = str1.split(" ");
        String[] labels2 = str2.split(" ");
        for(String label : labels1){
            System.out.println("Symbol: "+label);
            example1.add(Utility.labelToOpcode(label));
        }
        for(String label : labels2){
            example2.add(Utility.labelToOpcode(label));
        }

        System.out.println(labels1.equals(labels2));
    }

    public static int getLengthWithLeadingZeros(int algorithm){
        if(Integer.bitCount(algorithm) %Integer.bitCount(instructionMask) != 0){
            return Integer.bitCount(algorithm)/Integer.bitCount(instructionMask) + 1;
        }
        return Integer.bitCount(algorithm)/Integer.bitCount(instructionMask);
    }

    public static int[] popInstruction(int algorithm){
        int instruction = algorithm & instructionMask;
        algorithm = algorithm >> 6;
        return new int[]{instruction, algorithm};
    }
}
