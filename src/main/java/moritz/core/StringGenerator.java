package moritz.core;

import java.util.*;

/**
 * Created by Moritz on 11/26/2016.
 * <p></p>
 */
public class StringGenerator {
    private Random rand;
    private int count;

    private static List<Integer> data = new ArrayList<>();
    private static List<Integer> binoperators = new ArrayList<>();
    private static List<Integer> unoperators = new ArrayList<>();
    private static String[] binopStrings = {"&","^","|","+","-","*","/","@"};
    private static String[] unopStrings = {"bitcount","abs"};
    private static String[] dataStrings = {"maxlinebits","idealscore","playermap","oppmap","wpattern","2","10"};
    private static Map<Integer, String> bitMap = new HashMap<>();
    private static Map<String, Integer> stringMap = new HashMap<>();
    private static int terminateInstruction = 0b11111;

    private static int maxUnop;
    private static int maxData;
    private static int maxBinop = terminateInstruction - 1;

    {
        rand = new Random();
        int binLabel = terminateInstruction - 1;
        for(String label : binopStrings){
            bitMap.put(binLabel, label);
            stringMap.put(label, binLabel);
            binoperators.add(binLabel);
            binLabel -= 1;
        }
        maxUnop = binLabel;
        for(String label : unopStrings){
            bitMap.put(binLabel, label);
            stringMap.put(label, binLabel);
            unoperators.add(binLabel);
            binLabel -= 1;
        }
        maxData = binLabel;
        for(String label : dataStrings){
            bitMap.put(binLabel, label);
            stringMap.put(label, binLabel);
            data.add(binLabel);
            binLabel -= 1;
        }
    }

    /*
    Using RPN grammar:
    exp -> num | stm tail
    stm -> num exp binop | exp unop
    tail -> exp binop tail | unop tail | eps
     */
    public StringGenerator(){
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

    public void binop(Stack<Integer> il){
        il.add(getRandomBinop());
    }

    public void unop(Stack<Integer> il){
        il.add(getRandomUnop());
    }

    public void num(Stack<Integer> il){
        il.add(getRandomData());
    }

    //Helpers
    public int getRandomMutation(int selection){
        if(selection < 1){
            return getRandomData();
        }
        else if(selection < 2){
            return getRandomBinop();
        }
        return getRandomUnop();
    }

    private int getRandomData(){
        return data.get(rand.nextInt(data.size()));
    }

    private int getRandomBinop(){
        return binoperators.get(rand.nextInt(binoperators.size()));
    }

    private int getRandomUnop(){
        return unoperators.get(rand.nextInt(unoperators.size()));
    }

    public String getLabel(int opcode){
        return bitMap.get(opcode);
    }

    public static int getMaxUnop() {
        return maxUnop;
    }

    public static int getMaxData() {
        return maxData;
    }

    public static int getMaxBinop() {
        return maxBinop;
    }

    public static String[] getBinopStrings(){
        return binopStrings;
    }

    public static String[] getUnopStrings() {
        return unopStrings;
    }

    public static String[] getDataStrings() {
        return dataStrings;
    }

    public static Map<Integer, String> getBitMap(){
        return bitMap;
    }

    public static Map<String, Integer> getStringMap(){
        return stringMap;
    }

}
