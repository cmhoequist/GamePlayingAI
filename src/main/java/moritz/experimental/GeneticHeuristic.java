package moritz.experimental;

import moritz.core.BinopMethod;

import java.util.*;

/**
 * Created by Moritz on 11/26/2016.
 * <p></p>
 */
public class GeneticHeuristic {
    private Set<Integer> winningPatterns = new HashSet<>();
    private int totalScore = 0;
    private int instructionSize = 5;
    private int instructionMask = 0b11111;
    //terminateInstruction ensures a canonical length (some multiple of the instruction size) but should be ignored, not interpreted
    private int terminateInstruction = 0b11111;

    private Map<String, BinopMethod> methodMap = new HashMap<>();
    private Map<Integer, String> bitMap = new HashMap<>();
    private String[] instructionStrings = { "terminate",
                                            "0","1","2","3","4","5","6","7","8","9",
                                            "&","^","|","bitcount","abs","+","-","*","/","total+","compare","skip",
                                            "maxlinebits","idealscore"};

    public GeneticHeuristic(){
        int binLabel = terminateInstruction;
        for(String label : instructionStrings){
            bitMap.put(binLabel, label);
            System.out.println(label+": "+Integer.toBinaryString(binLabel));
            binLabel -= 1;
        }

        methodMap.put("&", (playerBits, refBits) -> refBits & playerBits);
        methodMap.put("^", (playerBits, refBits) -> refBits ^ playerBits);
        methodMap.put("|", (playerBits, refBits) -> refBits | playerBits);
        methodMap.put("bitcount", (playerBits, refBits) -> Integer.bitCount(playerBits));
        methodMap.put("abs", (playerBits, refBits) -> Math.abs(playerBits));
        methodMap.put("+", (playerBits, refBits) -> playerBits + refBits);
        methodMap.put("-", (playerBits, refBits) -> playerBits - refBits);
        methodMap.put("*", (playerBits, refBits) -> playerBits * refBits);
        methodMap.put("@", (playerBits, refBits) -> new Double(Math.pow(playerBits, refBits)).intValue());
        methodMap.put("/", (playerBits, refBits) -> playerBits / refBits);
        methodMap.put("total+", (playerBits, refBits) -> {
            totalScore += playerBits;
            return totalScore;
        });
        methodMap.put("compare", (playerBits, refBits) -> Integer.compare(playerBits, refBits));
        methodMap.put("skip", (playerBits, refBits) -> playerBits);
        methodMap.put("terminate",(playerBits, refBits) -> playerBits);
        methodMap.put("maxlinebits",(playerBits, refBits) -> playerBits);
        methodMap.put("idealscore",(playerBits, refBits) -> playerBits);
    }

    /**
     * Returns the number of instructions contained in the instruction string (accounts for implicit leading zeros).
     * @param algorithm the instruction string
     * @return the number of instructions in an instruction string
     */
    public int getLengthWithLeadingZeros(int algorithm){
        if(Integer.bitCount(algorithm) %Integer.bitCount(instructionMask) != 0){
            return Integer.bitCount(algorithm)/Integer.bitCount(instructionMask) + 1;
        }
        return Integer.bitCount(algorithm)/Integer.bitCount(instructionMask);
    }

    /**
     * Consumes the LSB instruction from an instruction string and returns the [instruction, remaining] tuple.
     * @param algorithm the current bitstring representing a set of instructions
     * @return a tuple containing the next instruction to execute and the remainder of the input bitstring
     */
    public int popInstruction(Queue<Integer> algorithm){
        int instruction = algorithm.poll() & instructionMask;
        return instruction;
    }

    public void parseInstructions(Queue<Integer> algorithm){
        System.out.println("Algorithm: "+algorithm);
        while(!algorithm.isEmpty()){
            int instruction = popInstruction(algorithm);
            String method = bitMap.get(instruction);
            System.out.println("Instruction: "+Integer.toBinaryString(instruction)+ ", method: "+method);
        }
    }

    public int score(int algorithm){
        winningPatterns.forEach(pattern -> {

        });


        return -1;
    }

    private int scorecursive(int algorithm){
        return -1;
    }

    private int compareToSolutions(int moves){
//        int playerBits = pattern & player;
        return -1;
    }
}
