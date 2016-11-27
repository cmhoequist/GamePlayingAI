package moritz.experimental;

import moritz.core.ParseRPN;
import moritz.core.StringGenerator;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by Moritz on 11/22/2016.
 * <p></p>
 */
public class Sandbox {
    private static int instructionMask = 0b111111; //Also terminating instruction
    private static BigInteger bint = BigInteger.valueOf(0b010111011011);
    private static int[] sampleInstructions = {0b01010, 0b11101, 0b01100, 0b01010, 0b00100, 0b11101};

    public static void main(String[] args){
        BigInteger bint = BigInteger.valueOf(0b01010111011011001010100001000001);

//        GeneticHeuristic gh = new GeneticHeuristic();

        StringGenerator test = new StringGenerator();
        List<Stack<Integer>> algorithms = new ArrayList<>();
        for(int i = 0; i < 10; i++){
            Stack<Integer> alg = test.getRPNInstructions(20);
            algorithms.add(alg);
            System.out.print(i+": ");
            alg.stream().forEach(e -> System.out.print(test.getLabel(e)+" "));
            System.out.println();
        }

        ParseRPN parseTest = new ParseRPN();
        for(int i = 0; i < 10; i++){
            int outcome = parseTest.score(algorithms.get(i));
            System.out.println(i+": "+outcome);
        }


//        Queue<Integer> algorithm = new LinkedList<>();
//        algorithm.add(instructionMask);
//        for(int sample : sampleInstructions){
//            algorithm.add(sample);
//        }
//
//        gh.parseInstructions(algorithm);


//        System.out.println("Algorithm string: " + bint.toString(2));
//        System.out.println("Length: " + bint.bitLength());
//        System.out.println("Length: " + bint.bitCount());

//        IntStream.range(0, getLengthWithLeadingZeros(algorithm)).forEach(i -> {
//            int[] outcome = popInstruction(algorithm);
//            algorithm = outcome[1];
//            System.out.println("Instruction "+i+": "+Integer.toBinaryString(outcome[0]));
//            System.out.println("Remaining algorithm: "+Integer.toBinaryString(algorithm));
//        });
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
