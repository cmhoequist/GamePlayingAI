package moritz.core;

import java.util.*;

/**
 * Created by Moritz on 11/27/2016.
 * <p></p>
 */
public class GeneticAlgorithm {
    /*
    At the beginning of a run of a genetic algorithm a large population of random chromosomes is created.
    Each one, when decoded will represent a different solution to the problem at hand.
    Let's say there are N chromosomes in the initial population. Then, the following steps are repeated until a solution is found

    1. Test each chromosome to see how good it is at solving the problem at hand and assign a fitness score accordingly.
    The fitness score is a measure of how good that chromosome is at solving the problem to hand.
    2. Select two members from the current population. The chance of being selected is proportional to the chromosomes fitness.
    Roulette wheel selection is a commonly used method.
    3. Dependent on the crossover rate crossover the bits from each chosen chromosome at a randomly chosen point.
    4. Step through the chosen chromosomes bits and flip dependent on the mutation rate.
    Repeat step 2, 3, 4 until a new population of N members has been created.
     */

    private static Map<Double, Stack<Integer>> population = new HashMap<>();
    private static Map<Double, Integer> weights = new HashMap<>();
    private static int maxMatchScore = 4; //2 for a win, 1 for a tie, 0 for a loss. Match = 2 rounds.
    private static Random rand;
    private static int rouletteCounter = 0;
    private static final double crossoverRate = 0.5;
    private static final double mutationRate = 0.001;
    private static int populationSize;
    private static boolean DEBUG = true;
    private static int generationTracker = 0;

    /**
     * Returns a map of chromosomes. Key values are used for weight lookup.
     * @param size
     * @param targetLength
     * @param minLength
     * @return
     */
    public static void getPopulation(int size, int targetLength, int minLength){
        if(DEBUG){
            System.out.println("Generating population......\n");
        }
        populationSize = size;
        rand = new Random();
        StringGenerator sg = new StringGenerator(); //Must initialize before parser (included in TTT)
        for(double i = 0.0; i < populationSize; ){
            Stack<Integer> alg = sg.getRPNInstructions(targetLength);
            if(alg.size() > minLength){
                population.put(i, alg);
                weights.put(i, 0);
                i += 1;
                if(DEBUG){
                    System.out.println("Population size: "+population.size()+"/"+populationSize);
                }
            }
        }
        if(DEBUG){
            System.out.println("Population generated.\n");
        }
    }

    /**
     * Each chromosome faces off against every other chromosome in two rounds of tic-tac-toe (one on each side to eliminate
     * starting bias) and the result added to the competitors' fitness scores (weights).
     */
    public static void evaluatePopulation(){
        TeachTacToe ttt = new TeachTacToe();
        int matchScore;
        if(DEBUG){
            System.out.println("Evaluating chromosomes.....");
        }
        for(double i = 0.0; i < population.size(); i++){
            matchScore = 0;
            for(double j = i+1; j < population.size(); j++){
                matchScore += ttt.teach(population.get(i), population.get(j));
                matchScore += ttt.teach(population.get(j), population.get(i));
                weights.put(i, weights.get(i)+matchScore);
                weights.put(j, weights.get(j)+(maxMatchScore - matchScore));
            }
            if(DEBUG){
                System.out.println("Done evaluating chromosome #"+i);
            }
        }
        if(DEBUG){
            System.out.println("Chromosomes evaluated.\n");
        }
    }

    public static void evolvePopulation(StringGenerator seed){
        Map<Double, Stack<Integer>> newPopulation = new HashMap<>();
        if(DEBUG){
            System.out.println("Evolving population.....");
        }
        for(double newKey = 0.0; newPopulation.size() < populationSize; ){
            double key1 = selectFitAlgorithm(-1);
            double key2 = selectFitAlgorithm(key1);
            Stack<Integer> fit1 = population.get(key1);
            Stack<Integer> fit2 = population.get(key2);

            //Crossover
            int limit = Math.min(fit1.size(), fit2.size());
            for(int i = 0; i < limit; i++){
                if(rand.nextInt(10) < new Double(crossoverRate *10).intValue()){
                    int temp = fit1.get(i);
                    fit1.set(i, fit2.get(i));
                    fit2.set(i, temp);
                }
            }

            //Mutate
            for(int i = 0; i < fit1.size(); i++){
                if(rand.nextInt(1000) < new Double(mutationRate*1000).intValue()){
                    fit1.set(i, seed.getRandomMutation(rand.nextInt(3)));
                }
            }
            for(int i = 0; i < fit2.size(); i++){
                if(rand.nextInt(1000) < new Double(mutationRate*1000).intValue()){
                    fit2.set(i, seed.getRandomMutation(rand.nextInt(3)));
                }
            }

            newPopulation.put(newKey, fit1);
            newKey += 1;
            newPopulation.put(newKey, fit2);
            newKey += 1;
            if(DEBUG){
                System.out.println("Pair filtered through.");
            }
        }

        population = newPopulation;
        generationTracker += 1;
        if(DEBUG){
            System.out.println("Population evolved. Child generation #"+generationTracker+"\n");
        }
    }

    public static double selectFitAlgorithm(double current){
        int totalWeight = weights.values().stream().reduce(0, Integer::sum);

        int roulette = rand.nextInt(totalWeight);
        for(Map.Entry<Double, Integer> e : weights.entrySet()){
            rouletteCounter += e.getValue();
            if(rouletteCounter >= roulette && e.getKey() != current){   //Make sure we don't randomly select the same alg twice
                rouletteCounter = 0;
                return e.getKey();
            }
        }
        try {
            throw new Exception("Inexplicably, the sum of all weights is strictly less than the sum of all weights.");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static int getGeneration(){
        return generationTracker;
    }

    public static Stack<Integer> getBest(){
        double key = weights.entrySet().stream().max((e1, e2) -> Double.compare(e1.getValue(), e1.getKey())).map(e -> e.getKey()).get();
        return population.get(key);
    }

    public static Stack<Integer> getSampleChromosome(double index){
        return population.get(index);
    }
}
