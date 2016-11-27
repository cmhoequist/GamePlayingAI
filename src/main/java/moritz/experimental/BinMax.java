package moritz.experimental;

import java.util.Arrays;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Moritz on 11/26/2016.
 * <p></p>
 */
public class BinMax {
    private int[] patterns = {
            0b111000000, 0b000111000, 0b000000111, // rows
            0b100100100, 0b010010010, 0b001001001, // cols
            0b100010001, 0b001010100               // diagonals
    };
    private Set<Integer> winningPatterns = Arrays.stream(patterns).boxed().collect(Collectors.toSet());

    int xMoves = 0b000000000;
    int oMoves = 0b000000000;

    String currentPlayer = "x";
    int choice = -1;

    int depth = -1;


    public int getMove(){
        int move = -1;
        Scanner scanner = new Scanner(System.in);
        while(!isOver()){

            if(currentPlayer.equals("x")){
                System.out.println("Enter a cell index: ");
                move = scanner.nextInt();
            }
            else{
                minmax("o");
                move = choice;
            }

            System.out.println(currentPlayer+" moves to "+move);
            setMove(getBinaryIndex(8-move), currentPlayer);
            System.out.println("x: " + Integer.toBinaryString(xMoves));
            System.out.println("y: " + Integer.toBinaryString(oMoves));

            currentPlayer = getNextPlayer(currentPlayer);
        }
        return choice;
    }

    public int minmax(String current){
        depth += 1;

        int sumOfChildren = 0;
        int bestScore = currentPlayer.equals("x") ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        int nodeChoice = -1;

        int occupied = xMoves | oMoves;
        if(isOver()){
            depth -= 1;
            return score();
        }

        for(int i = 0; i < 9; i++){

            boolean test = true;
            if(depth==0){
                test = ((occupied >> i & 1) != 1);
            }
            if((occupied >> i & 1) != 1){
                int index = 8 - i;
                if(depth == 0){
                    System.out.println("test is " + test + " ("+index+","+current+")");
                }

                int thisBit = getBinaryIndex(i);
                setMove(thisBit, current);

                //Evaluate
                int childScore = minmax(getNextPlayer(current));
                sumOfChildren += childScore;
                if(current.equals("x")){
                    if(childScore > bestScore){
                        bestScore = childScore;
                        nodeChoice = index;
                    }
                }
                else{
                    if(childScore < bestScore){
                        bestScore = childScore;
                        nodeChoice = index;
                    }
                }

                unset(thisBit, current);

                if(depth==0){
                    System.out.println(childScore +" -> "+sumOfChildren+", "+index);
                }
            }
        }
        if(depth==0){
            System.out.println("choose "+nodeChoice+" with score: "+bestScore);
            System.out.println("total at this node: "+sumOfChildren);
        }
        depth -=1 ;
        choice = nodeChoice;
        return sumOfChildren;
    }

    private void unset(int thisBit, String current){
        if(current.equals("x")){
            xMoves ^= thisBit;
        }
        else{
            oMoves ^= thisBit;
        }
    }

    private void setMove(int thisBit, String current){
        if(current.equals("x")){
            int test = xMoves ^ thisBit;
//            System.out.println(Integer.toBinaryString(xMoves)+" vs " + Integer.toBinaryString(thisBit) + " = " + test);
            if(test < xMoves){
                System.out.println("filtering error x");
            }
            xMoves ^= thisBit;
        }
        else{
            int test = oMoves ^ thisBit;
//            System.out.println(Integer.toBinaryString(oMoves)+" vs " + Integer.toBinaryString(thisBit)+ " = " + test);
            if(test < oMoves){
                System.out.println("filtering error o");
            }
            oMoves ^= thisBit;
        }
    }

    private int getBinaryIndex(int index){
        return new Double(Math.pow(2, index)).intValue();
    }

    public static String getNextPlayer(String currentPlayer){
        return currentPlayer.equals("x") ? "o" : "x";
    }

    public int score(){
        for(int pat : winningPatterns){
            if((pat & xMoves)==pat){
                return 10;
            }
            else if((pat & oMoves)==pat){
                return -10;
            }
        }
        return 0;
    }

    private boolean isOver(){
        for(int pat : winningPatterns){
            if((pat & xMoves)==pat){
                return true;
            }
            else if((pat & oMoves)==pat){
                return true;
            }
        }
        if((xMoves | oMoves) == 0b111111111){
            return true;
        }
        return false;
    }
}
