package moritz.current;

import java.util.*;

/**
 * Created by Moritz on 11/26/2016.
 * <p></p>
 */
public class Parser {
    private final boolean DEBUG = false;

    public Parser(){
    }

    public void setPlayerState(long pat){
        Utility.setPlayerState(pat);
    }

    public void setOpponentState(long pat){
        Utility.setOpponentState(pat);
    }

//    public void setWinPattern(int pat){
//        Utility.setWinPattern(pat);
//    }

    Stack<Integer> consumed = new Stack<>();
    public int score(Stack<Integer> instructions){
        int outcome = evaluateRPN(instructions);
        //restore instruction stack
        while(!consumed.isEmpty()){
            instructions.add(consumed.pop());
        }
        return outcome;
    }

    private int evaluateRPN(Stack<Integer> instructions){
        while(!instructions.isEmpty()){
            Integer bits = instructions.pop();
            consumed.add(bits);
//            System.out.println(StringGenerator.getBitMap().get(bits)+" ("+bits+"): next");
            //If not no-op
            if(bits < 0b11111){
                if(Utility.isData(bits)){
                    return new Long(dataLookup(bits)).intValue();
                }
                else if(Utility.isUnop(bits)){
                    int data = evaluateRPN(instructions);
                    return unopLookup(bits).invoke(data);
                }
                else{
                    int d2 = evaluateRPN(instructions);
                    int d1 = evaluateRPN(instructions);
                    if(binopLookup(bits) == null){
                        System.out.println(Utility.opcodeToLabel(bits)+" ("+bits+"): NULL");
                    }
                    return binopLookup(bits).invoke(d1, d2);
                }
            }
        }
        //If we've made it here, a garbage mutation has been introduced somewhere along the line. That's fine.
        if(DEBUG){
            try {
                throw new Exception("Invalid instruction stack");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    private BinopMethod binopLookup(int bits) {
        return Utility.getBinop(bits);
    }

    private UnopMethod unopLookup(int bits) {
        return Utility.getUnop(bits);
    }

    private long dataLookup(int bits) {
        return Utility.getData(bits);
    }

}
