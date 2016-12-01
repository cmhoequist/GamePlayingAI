package moritz.current;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Created by Moritz on 11/25/2016.
 * <p></p>
 */
public class Driver {
    public static void main(String[] args){
        Utility.initialize();
        geneticAlg();
//        testManual();
    }

    public static void geneticAlg(){
        int cohortSize = 1000;
        int generations = 5;
        GeneticAlgorithm.getPopulation(cohortSize, 20, 3);

        Stack<Integer> initialBest = new Stack<>();
        String str2 = "playermap oppmap ^ bitcount oppmap";
        String[] labels2 = str2.split(" ");
        for(String label : labels2){
            initialBest.add(Utility.labelToOpcode(label));
        }
        TeachTacToe ttt = new TeachTacToe(true);
        TC4 tc4 = new TC4(true);
        for(int i = 0; i < generations; i++){
            GeneticAlgorithm.evaluatePopulation();
            GeneticAlgorithm.evolvePopulation();
            Stack<Integer> newBest = GeneticAlgorithm.getBest();
//            ttt.teach(initialBest, newBest);
            tc4.teach(initialBest, newBest);
            initialBest = newBest;
        }

        GeneticAlgorithm.evaluatePopulation();
        System.out.println("Best 15--------------------");
        GeneticAlgorithm.getWeightedChromosomes().entrySet().stream()
                .sorted((e1, e2) -> Double.compare(e2.getKey(), e1.getKey()))
                .limit(15)
                .forEach(e -> {
                   System.out.print("K: "+e.getKey());
                    System.out.print(", ");
                    e.getValue().forEach(v -> System.out.print(Utility.opcodeToLabel(v)+" "));
                    System.out.println();
                });

        Map<Integer,Stack<Integer>> writeAlgs = GeneticAlgorithm.getWeightedChromosomes();
        Utility.writeToFile("src/main/resources/algs.txt",writeAlgs);
        System.out.println("Maxes 15------------------");
        GeneticAlgorithm.maxalgs.entrySet().stream()
                .sorted((e1, e2) -> Double.compare(e2.getKey(), e1.getKey()))
                .limit(15)
                .forEach(e -> {
                    System.out.print("K: "+e.getKey());
                    System.out.print(", ");
                    e.getValue().forEach(v -> System.out.print(Utility.opcodeToLabel(v)+" "));
                    System.out.println();
                });
    }

    public static void testManual(){
//        Generator sg = new Generator(); //Must initialize before parser (included in TTT)
        TeachTacToe ttt = new TeachTacToe();
        Stack<Integer> example1 = new Stack<>();
        Stack<Integer> example2 = new Stack<>();
        String str1 = "idealscore playermap wpattern oppmap playermap oppmap wpattern | bitcount maxlinebits playermap oppmap wpattern 10 wpattern wpattern 10 + - @ / playermap wpattern oppmap wpattern oppmap playermap wpattern wpattern playermap oppmap oppmap playermap oppmap wpattern oppmap oppmap playermap oppmap wpattern oppmap wpattern wpattern oppmap wpattern wpattern playermap playermap wpattern oppmap oppmap playermap wpattern wpattern wpattern playermap playermap playermap oppmap oppmap playermap oppmap playermap playermap oppmap oppmap oppmap wpattern wpattern oppmap wpattern wpattern wpattern playermap oppmap wpattern oppmap playermap oppmap oppmap wpattern playermap wpattern wpattern wpattern playermap playermap playermap playermap wpattern oppmap wpattern wpattern oppmap wpattern wpattern playermap oppmap oppmap wpattern oppmap playermap playermap idealscore playermap wpattern oppmap playermap oppmap wpattern | bitcount maxlinebits playermap oppmap wpattern 10 wpattern wpattern 10 + - @ / playermap wpattern oppmap wpattern oppmap playermap wpattern wpattern playermap oppmap oppmap playermap oppmap wpattern oppmap oppmap playermap oppmap wpattern oppmap wpattern wpattern oppmap wpattern wpattern playermap playermap wpattern oppmap oppmap playermap wpattern wpattern wpattern playermap playermap playermap oppmap oppmap playermap oppmap playermap playermap oppmap oppmap oppmap wpattern wpattern oppmap wpattern wpattern wpattern playermap oppmap wpattern oppmap playermap oppmap oppmap wpattern playermap wpattern wpattern wpattern playermap playermap playermap playermap wpattern oppmap wpattern wpattern oppmap wpattern wpattern playermap oppmap oppmap wpattern oppmap playermap playermap";
        String str2 = "playermap wpattern ^ bitcount oppmap";
        String[] labels1 = str1.split(" ");
        String[] labels2 = str2.split(" ");
        for(String label : labels1){
//            System.out.println("Symbol: "+label);
            example1.add(Utility.labelToOpcode(label));
        }
        for(String label : labels2){
            example2.add(Utility.labelToOpcode(label));
        }
        Parser exampleparser = new Parser();
        System.out.println("Example1: "+example1+", "+exampleparser.score(example1));
        System.out.println("Example2: "+example2+", "+exampleparser.score(example2));
        ttt.setDebug(true);
        int winner = ttt.teach(example1, example2);
        if(winner == 1){
            System.out.println("TIE");
        }
        else if(winner > 0){
            System.out.println("EXPECTED");
            example1.stream().forEach(e -> System.out.print(Utility.opcodeToLabel(e)+" "));
        }
        else{
            System.out.println("UPSET");
            example2.stream().forEach(e -> System.out.print(Utility.opcodeToLabel(e)+" "));
        }
//        winner.stream().forEach(e -> System.out.print(sg.getLabel(e)+" "));
    }


    public static void singleGenFilter(){
//        Generator sg = new Generator(); //Must initialize before parser (included in TTT)
        TeachTacToe ttt = new TeachTacToe();
        List<Stack<Integer>> algorithms = new ArrayList<>();
        boolean sentinel = true;
        while(sentinel){
            algorithms.clear();
            while(algorithms.size() < 2){
                Stack<Integer> alg = Generator.getRPNInstructions(8);
                if(alg.size() > 3){
                    algorithms.add(alg);
                    System.out.print("alg"+algorithms.size()+": ");
                    alg.stream().forEach(e -> System.out.print(Utility.opcodeToLabel(e)+" "));
                    System.out.println();
                }
            }

            int[] winners = new int[2];
            for(int i = 0; i < 2; i++){
//                Stack<Integer> winner = ttt.teach(algorithms.get(0+i), algorithms.get(1-i));
//                if(!winner.equals(algorithms.get(0))){
//                winner.stream().forEach(e -> System.out.print(sg.getLabel(e)+" "));
//                System.out.println("\nDNE");
//                algorithms.get(0).stream().forEach(e -> System.out.print(sg.getLabel(e)+" "));
//                    winners[i] = 0;
//                }
//                else{
//                System.out.println("Winner x");
//                    winners[i] = 1;
//                }
            }

            if(winners[0] == winners[1]){
                System.out.print("Winner is alg"+winners[0]+": ");
                algorithms.get(winners[0]).stream().forEach(e -> System.out.print(Utility.opcodeToLabel(e)+" "));
                sentinel = false;
            }
            else{
                System.out.println("Turn order dominant factor");
            }
        }


    }

    public static void debugInitial(){
//        StringGenerator sg = new StringGenerator(); //Must initialize before parser (included in TTT)
//        TeachTacToe ttt = new TeachTacToe();
//        Stack<Integer> example1 = new Stack<>();
//        example1.add(StringGenerator.getStringMap().get("playermap"));
//        example1.add(StringGenerator.getStringMap().get("idealscore"));
//        example1.add(StringGenerator.getStringMap().get("*"));
//        example1.add(StringGenerator.getStringMap().get("abs"));
//        example1.add(StringGenerator.getStringMap().get("wpattern"));
//        example1.add(StringGenerator.getStringMap().get("|"));
//        Stack<Integer> example2 = new Stack<>();
//        example2.add(StringGenerator.getStringMap().get("wpattern"));
//        example2.add(StringGenerator.getStringMap().get("bitcount"));
//        example2.add(StringGenerator.getStringMap().get("10"));
//        example2.add(StringGenerator.getStringMap().get("idealscore"));
//        example2.add(StringGenerator.getStringMap().get("+"));
//        example2.add(StringGenerator.getStringMap().get("+"));
//        Parser exampleparser = new Parser();
//        System.out.println("Example1: "+example1+", "+exampleparser.score(example1));
//        System.out.println("Example2: "+example2+", "+exampleparser.score(example2));
//        Stack<Integer> winner = ttt.teach(example1, example2);
//        winner.stream().forEach(e -> System.out.print(sg.getLabel(e)+" "));
    }
}
