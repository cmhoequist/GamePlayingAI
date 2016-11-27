package moritz.core;

import moritz.core.BinopMethod;
import moritz.core.StringGenerator;
import moritz.core.UnopMethod;

import java.util.*;

/**
 * Created by Moritz on 11/26/2016.
 * <p></p>
 */
public class ParseRPN {
    Map<Integer, BinopMethod> binopTable = new HashMap<>();
    Map<Integer, UnopMethod> unopTable = new HashMap<>();
    Map<Integer, Integer> dataTable = new HashMap<>();
    private static String[] binopStrings = {"&","^","|","+","-","*","/","@"};
    private static String[] unopStrings = {"bitcount","abs",};
    private static String[] dataStrings = {"maxlinebits","idealscore","playermap","oppmap","wpattern","2","10"};
    private static int[] sampleData = {3, 24, 0b000011100, 0b001100001, 0b111000000, 2, 10};
    private Map<String, Integer> stringMap = StringGenerator.getStringMap();
    private final boolean DEBUG = false;

    public ParseRPN(){
        binopTable.put(stringMap.get("&"), (playerBits, refBits) -> refBits & playerBits);
        binopTable.put(stringMap.get("^"), (playerBits, refBits) -> refBits ^ playerBits);
        binopTable.put(stringMap.get("|"), (playerBits, refBits) -> refBits | playerBits);
        binopTable.put(stringMap.get("+"), (playerBits, refBits) -> playerBits + refBits);
        binopTable.put(stringMap.get("-"), (playerBits, refBits) -> playerBits - refBits);
        binopTable.put(stringMap.get("*"), (playerBits, refBits) -> playerBits * refBits);
        binopTable.put(stringMap.get("/"), (playerBits, refBits) -> new Double(Math.pow(playerBits, refBits)).intValue());
        binopTable.put(stringMap.get("@"), (playerBits, refBits) -> playerBits / (refBits==0 ? 1 : refBits));   //avoid div by 0
        unopTable.put(stringMap.get("bitcount"), (playerBits) -> Integer.bitCount(playerBits));
        unopTable.put(stringMap.get("abs"), (playerBits) -> Math.abs(playerBits));
        for(int i = 0; i < sampleData.length; i++){
            dataTable.put(stringMap.get(dataStrings[i]),sampleData[i]);
        }
    }

    public void setPlayerState(int pat){
        dataTable.put(stringMap.get("playermap"),pat);
    }

    public void setOpponentState(int pat){
        dataTable.put(stringMap.get("oppmap"),pat);
    }

    public void setWinPattern(int pat){
        dataTable.put(stringMap.get("wpattern"),pat);
    }

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

                if(bits <= StringGenerator.getMaxData()){
                    return dataLookup(bits);
                }
                else if(bits <= StringGenerator.getMaxUnop()){
                    int data = evaluateRPN(instructions);
                    return unopLookup(bits).invoke(data);
                }
                else{
                    int d2 = evaluateRPN(instructions);
                    int d1 = evaluateRPN(instructions);
                    if(binopLookup(bits) == null){
                        System.out.println(StringGenerator.getBitMap().get(bits)+" ("+bits+"): NULL");
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
        return binopTable.get(bits);
    }

    private UnopMethod unopLookup(int bits) {
        return unopTable.get(bits);
    }

    private int dataLookup(int bits) {
        try{
            return dataTable.get(bits);
        }
        catch(NullPointerException e){
            System.out.println("Bits: "+Integer.toBinaryString(bits)+" ("+StringGenerator.getBitMap().get(bits)+")");
            System.out.println(dataTable.keySet());
        }
        return dataTable.get(bits);
    }

}
