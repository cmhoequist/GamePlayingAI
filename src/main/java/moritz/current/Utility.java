package moritz.current;

import java.util.*;

/**
 * Created by Moritz on 11/27/2016.
 * <p></p>
 */
public abstract class Utility {
    private static Random rand;

    //Conversion mapping
    private static Map<Integer, String> bitMap = new HashMap<>();
    private static Map<String, Integer> stringMap = new HashMap<>();
    private static Map<Integer, Integer> categoryMap = new HashMap<>();

    //Sets of valid tokens
    private static List<Integer> numData = new ArrayList<>();
    private static List<Integer> bitData = new ArrayList<>();
    private static List<Integer> binoperators = new ArrayList<>();
    private static List<Integer> unoperators = new ArrayList<>();
    private static List<Integer> bitnoperators = new ArrayList<>();
    private static List<Integer> bitunoperators = new ArrayList<>();
    private static String[] binopStrings = {"+","-","*","/","@"};
    private static String[] unopStrings = {"abs"};
    private static String[] bitnopStrings = {"&","^","|"};
    private static String[] bitunopStrings = {"bitcount"};
    private static String[] numDataStrings = {"maxlinebits","idealscore","2","10"};
    private static String[] bitDataStrings = {"playermap","oppmap","wpattern"};

    //Highest instruction (starting point)
    private static int terminateInstruction = 0b11111;

    //Fast category checkers
    private static int maxUnop;
    private static int maxData;

    //Parser tables
    private static Map<Integer, BinopMethod> binopTable = new HashMap<>();
    private static Map<Integer, UnopMethod> unopTable = new HashMap<>();
    private static Map<Integer, Integer> dataTable = new HashMap<>();

    //Sample data for testing/debugging
    private static String[] dataStrings = {"maxlinebits","idealscore","2","10","playermap","oppmap","wpattern"};
    private static int[] data = {3, 24, 2, 10, 0b000011100, 0b001100001, 0b111000000};

    public static void initialize() {
        rand = new Random();
        int binLabel = terminateInstruction - 1;
        for(String label : binopStrings){
            binLabel = updateTables(binLabel, label, binoperators, 0);
        }
        for(String label : bitnopStrings){
            binLabel = updateTables(binLabel, label, bitnoperators, 1);
        }
        maxUnop = binLabel;
        for(String label : unopStrings){
            binLabel = updateTables(binLabel, label, unoperators, 2);
        }
        for(String label : bitunopStrings){
            binLabel = updateTables(binLabel, label, bitunoperators, 3);
        }
        maxData = binLabel;
        for(String label : numDataStrings){
            binLabel = updateTables(binLabel, label, numData, 4);
        }
        for(String label : bitDataStrings){
            binLabel = updateTables(binLabel, label, bitData, 5);
        }

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
        for(int i = 0; i < data.length; i++){
            dataTable.put(stringMap.get(dataStrings[i]), data[i]);
        }
    }

    private static int updateTables(int binLabel, String label, List<Integer> elementList, int category){
        bitMap.put(binLabel, label);
        stringMap.put(label, binLabel);
        categoryMap.put(binLabel, category);
        elementList.add(binLabel);
        return binLabel - 1;
    }

    //Data mutators
    public static void setPlayerState(int pat){
        dataTable.put(Utility.labelToOpcode("playermap"),pat);
    }

    public static void setOpponentState(int pat){
        dataTable.put(Utility.labelToOpcode("oppmap"),pat);
    }

    public static void setWinPattern(int pat){
        dataTable.put(Utility.labelToOpcode("wpattern"),pat);
    }

    //Deterministic getters
    public static int getData(int opcode){
        return dataTable.get(opcode);
    }

    public static BinopMethod getBinop(int opcode){
        return binopTable.get(opcode);
    }

    public static UnopMethod getUnop(int opcode){
        return unopTable.get(opcode);
    }

    //Random getters

    /**
     * Returns a random mutation (opcode) of the same category as the input gene.
     * @param opcode
     * @return
     */
    public static int getRandomMutation(int opcode){
        int selection = getCategory(opcode);
        if(selection==0){
            return Utility.getRandomBinop();
        }
        else if(selection==1){
             return Utility.getRandomBitnop();
        }
        else if(selection==2){
            return Utility.getRandomUnop();
        }
        else if(selection==3){
            return Utility.getRandomBitunop();
        }
        else if(selection==4){
            return Utility.getRandomNumData();
        }
        return Utility.getRandomBitData();
    }

    public static int getRandomNumData(){
        return numData.get(rand.nextInt(numData.size()));
    }

    public static int getRandomBinop(){
        return binoperators.get(rand.nextInt(binoperators.size()));
    }

    public static int getRandomUnop(){
        return unoperators.get(rand.nextInt(unoperators.size()));
    }

    public static int getRandomBitData(){
        return bitData.get(rand.nextInt(bitData.size()));
    }

    public static int getRandomBitnop(){
        return bitnoperators.get(rand.nextInt(bitnoperators.size()));
    }

    public static int getRandomBitunop(){
        return bitunoperators.get(rand.nextInt(bitunoperators.size()));
    }

    //Token key conversion
    public static String opcodeToLabel(int opcode){
        return bitMap.get(opcode);
    }

    public static int labelToOpcode(String label){
        return stringMap.get(label);
    }

    //Type checkers
    public static int getCategory(int opcode){
        return categoryMap.get(opcode);
    }

    public static boolean isData(int element){
        return element <= maxData;
    }

    public static boolean isUnop(int element){
        return element > maxData && element <= maxUnop;
    }

    public static boolean isBinop(int element){
        return element > maxUnop;
    }

    public static boolean isBitnop(int unknown){
        return bitnoperators.contains(unknown);
    }

    public static boolean isBitunop(int unknown){
        return bitunoperators.contains(unknown);
    }

    public static boolean isBitData(int unknown){
        return bitData.contains(unknown);
    }
}
