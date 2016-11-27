package moritz.current;

import java.util.*;

/**
 * Created by Moritz on 11/27/2016.
 * <p></p>
 */
public class Generator {
    private Random rand;
    private int count;

    /*
    exp -> num | stm tail
    stm -> num exp binop | exp unop
    tail -> exp binop tail | unop tail | eps
    num -> bits bits bitnop bitunop | numData
     */
    public Generator(){
        rand = new Random();
    }

    public Stack<Integer> getRPNInstructions(int approxInstructionCount){
        count = approxInstructionCount;
        Stack<Integer> instructions = new Stack<>();
        exp(instructions);
        return instructions;
    }

    public void exp(Stack<Integer> il){
        if(rand.nextBoolean()){
            num(il);
        }
        else{
            stmt(il);
            tail(il);
        }
    }

    public void stmt(Stack<Integer> il){
        if(rand.nextBoolean()){
            num(il);
            exp(il);
            binop(il);
        }
        else{
            exp(il);
            unop(il);
        }
    }

    public void tail(Stack<Integer> il){
        if(count - il.size() > 3){
            if(rand.nextBoolean()){
                exp(il);
                binop(il);
                tail(il);
            }
            else{
                unop(il);
                tail(il);
            }
        }
    }

    public void num(Stack<Integer> il){
        if(rand.nextBoolean()){
            numdata(il);
        }
        else{
            bits(il);
            bits(il);
            bitnop(il);
            bitunop(il);
        }
    }

    public void binop(Stack<Integer> il){
        il.add(Utility.getRandomBinop());
    }

    public void unop(Stack<Integer> il){
        il.add(Utility.getRandomUnop());
    }

    public void numdata(Stack<Integer> il){
        il.add(Utility.getRandomNumData());
    }

    public void bits(Stack<Integer> il){
        il.add(Utility.getRandomBitData());
    }

    public void bitnop(Stack<Integer> il){
        il.add(Utility.getRandomBitnop());
    }

    public void bitunop(Stack<Integer> il){
        il.add(Utility.getRandomBitunop());
    }
}
