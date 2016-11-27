package moritz.current;

import java.util.*;

/**
 * Created by Moritz on 11/27/2016.
 * <p></p>
 */
public abstract class Generator {
    private static Random rand;
    private static int targetCount;

    /*
    exp -> num | stm tail
    stm -> num exp binop | exp unop
    tail -> exp binop tail | unop tail | eps
    num -> bits bits bitnop bitunop | numData
     */
    static public void initialize(){
        rand = new Random();
    }

    public static Stack<Integer> getRPNInstructions(int approxInstructionCount){
        targetCount = approxInstructionCount;
        Stack<Integer> instructions = new Stack<>();
        exp(instructions);
        return instructions;
    }

    public static void exp(Stack<Integer> il){
        if(rand.nextBoolean()){
            num(il);
        }
        else{
            stmt(il);
            tail(il);
        }
    }

    public static void stmt(Stack<Integer> il){
        if(rand.nextBoolean()){
            num(il);
            exp(il);
            binop(il);
        }
        else{
            exp(il);
            xunop(il);
        }
    }

    public static void tail(Stack<Integer> il){
        if(targetCount - il.size() > 3){
            if(rand.nextBoolean()){
                exp(il);
                binop(il);
                tail(il);
            }
            else{
                //Our only unop is abs. Do not want to repeat abs.
                xunop(il);
                tail(il);
            }
        }
    }

    public static void num(Stack<Integer> il){
        if(rand.nextBoolean()){
            numdata(il);
        }
        else{
            //Know we want to avoid duplication of bit data
            bits(il);
            xbits(il, il.peek());
            bitnop(il);
            bitunop(il);
        }
    }

    /**
     * Builds a new stack from a simple rule that contains an EXP (existing stack).
     * @param il
     */
    private static Stack<Integer> growStack(Stack<Integer> il){
        Stack<Integer> newStack = new Stack<>();
        //  stm -> num exp binop
        num(newStack);
        for(int i = 0; i < il.size(); i++){
            newStack.add(il.get(i));
        }
        binop(newStack);
        return newStack;
    }

    public static Stack<Integer> mutateInsanely(Stack<Integer> il){
        targetCount = 20;
        if(il.size() > 2*targetCount){
            for(int i = 0; i < il.size(); i++){
                il.set(i, Utility.getRandomMutation(il.get(i)));
            }
            return il;
        }
        return growStack(il);
    }

    public static void binop(Stack<Integer> il){
        il.add(Utility.getRandomBinop());
    }

//    public static void unop(Stack<Integer> il){
//        il.add(Utility.getRandomUnop());
//    }

    public static void xunop(Stack<Integer> il) {
        int opcode = Utility.getRandomXUnop(il.peek());
        if(opcode != 0){
            il.add(opcode);
        }
    }

    public static void numdata(Stack<Integer> il){
        il.add(Utility.getRandomNumData());
    }

    public static void bits(Stack<Integer> il){
        il.add(Utility.getRandomBitData());
    }

    public static void xbits(Stack<Integer> il, int dedup){
        il.add(Utility.getXORandomBitData(dedup));
    }

    public static void bitnop(Stack<Integer> il){
        il.add(Utility.getRandomBitnop());
    }

    public static void bitunop(Stack<Integer> il){
        il.add(Utility.getRandomBitunop());
    }

}
