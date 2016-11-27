package moritz.closed;

import java.math.BigInteger;

/**
 * Created by Moritz on 11/22/2016.
 * <p></p>
 */
public class Sandbox {
    private static int instructionMask = 0b111111; //Also terminating instruction
    private static BigInteger bint = BigInteger.valueOf(0b010111011011);
    private static int[] sampleInstructions = {0b01010, 0b11101, 0b01100, 0b01010, 0b00100, 0b11101};

    public static void main(String[] args){

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
