package moritz;

/**
 * Created by Moritz on 11/25/2016.
 * <p></p>
 */
public class Driver {
    public static void main(String[] args){
        negamax();
    }

    public static void negamax(){
        Negamax nm = new Negamax();
        nm.getMove();
    }
}
