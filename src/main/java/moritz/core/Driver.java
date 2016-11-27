package moritz.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by Moritz on 11/25/2016.
 * <p></p>
 */
public class Driver {
    public static void main(String[] args){
        fitnessFilter();
    }


    public static void fitnessFilter(){
        StringGenerator sg = new StringGenerator(); //Must initialize before parser (included in TTT)
        TeachTacToe ttt = new TeachTacToe();
        List<Stack<Integer>> algorithms = new ArrayList<>();
        boolean sentinel = true;
        while(sentinel){
            algorithms.clear();
            while(algorithms.size() < 2){
                Stack<Integer> alg = sg.getRPNInstructions(8);
                if(alg.size() > 3){
                    algorithms.add(alg);
                    System.out.print("alg"+algorithms.size()+": ");
                    alg.stream().forEach(e -> System.out.print(sg.getLabel(e)+" "));
                    System.out.println();
                }
            }

            int[] winners = new int[2];
            for(int i = 0; i < 2; i++){
                Stack<Integer> winner = ttt.teach(algorithms.get(0+i), algorithms.get(1-i));
                if(!winner.equals(algorithms.get(0))){
//                winner.stream().forEach(e -> System.out.print(sg.getLabel(e)+" "));
//                System.out.println("\nDNE");
//                algorithms.get(0).stream().forEach(e -> System.out.print(sg.getLabel(e)+" "));
                    winners[i] = 0;
                }
                else{
//                System.out.println("Winner x");
                    winners[i] = 1;
                }
            }

            if(winners[0] == winners[1]){
                System.out.print("Winner is alg"+winners[0]+": ");
                algorithms.get(winners[0]).stream().forEach(e -> System.out.print(sg.getLabel(e)+" "));
                sentinel = false;
            }
            else{
                System.out.println("Turn order dominant factor");
            }
        }


    }

    public static void debug(){
        StringGenerator sg = new StringGenerator(); //Must initialize before parser (included in TTT)
        TeachTacToe ttt = new TeachTacToe();
        Stack<Integer> example1 = new Stack<>();
        example1.add(StringGenerator.getStringMap().get("playermap"));
        example1.add(StringGenerator.getStringMap().get("idealscore"));
        example1.add(StringGenerator.getStringMap().get("*"));
        example1.add(StringGenerator.getStringMap().get("abs"));
        example1.add(StringGenerator.getStringMap().get("wpattern"));
        example1.add(StringGenerator.getStringMap().get("|"));
        Stack<Integer> example2 = new Stack<>();
        example2.add(StringGenerator.getStringMap().get("wpattern"));
        example2.add(StringGenerator.getStringMap().get("bitcount"));
        example2.add(StringGenerator.getStringMap().get("10"));
        example2.add(StringGenerator.getStringMap().get("idealscore"));
        example2.add(StringGenerator.getStringMap().get("+"));
        example2.add(StringGenerator.getStringMap().get("+"));
        ParseRPN exampleparser = new ParseRPN();
        System.out.println("Example1: "+example1+", "+exampleparser.score(example1));
        System.out.println("Example2: "+example2+", "+exampleparser.score(example2));
        Stack<Integer> winner = ttt.teach(example1, example2);
        winner.stream().forEach(e -> System.out.print(sg.getLabel(e)+" "));
    }
}
