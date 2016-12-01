package moritz.current;

import java.util.*;

/**
 * Created by Moritz on 11/27/2016.
 * <p></p>
 */
public abstract class GeneticAlgorithm {
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
    private static final double mutationRate = 0.01;
    private static int populationSize;
    private static boolean DEBUG = false;
    private static int generationTracker = 0;
    private static int evalThreshold = 20;
    private static int targetInstructionLength;
    private static int max = 0;
    public static Map<Integer, Stack<Integer>> maxalgs = new HashMap<>();

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
        targetInstructionLength = targetLength;
        rand = new Random();
        for(double i = 0.0; i < populationSize; ){
            Stack<Integer> alg = Generator.getRPNInstructions(targetLength);
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
     * Each chromosome faces off against some number of other chromosomes in two rounds of tic-tac-toe (one on each side to eliminate
     * starting bias) and the result added to the competitors' fitness scores (weights).
     */
    public static void evaluatePopulation(){
        TeachTacToe ttt = new TeachTacToe();
        int matchScore;
        if(DEBUG){
            System.out.println("Evaluating chromosomes.....");
        }
        for(double i = 0.0; i < population.size(); i++){
            for(double j = i+1; j < evalThreshold && j < population.size(); j++){
                matchScore = 0;
                matchScore += ttt.teach(population.get(i), population.get(j));
                matchScore += ttt.teach(population.get(j), population.get(i));
                weights.put(i, weights.get(i)+matchScore);
            }
            if(weights.get(i) > max){
                max = weights.get(i);
                maxalgs.put(max, population.get(i));
            }
        }
        if(DEBUG){
            System.out.println("Chromosomes evaluated.\n");
        }
    }

    public static void evolvePopulation(){
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
            boolean crossover = false;
            for(int i = 0; i < limit; i++){
                if(!crossover){
                    if(rand.nextInt(new Double(crossoverRate*100).intValue()) < crossoverRate*100){
                        crossover = true;
                    }
                }
                else{
                    int temp = fit1.get(i);
                    fit1.set(i, fit2.get(i));
                    fit2.set(i, temp);
                }

            }

            //Mutate
            mutate(fit1);
            mutate(fit2);

            //Add to population
            newKey = refresh(fit1, newPopulation, newKey);
            newKey = refresh(fit2, newPopulation, newKey);
        }

        population = newPopulation;
        generationTracker += 1;
        weights.entrySet().forEach(e -> weights.put(e.getKey(), 0));
        if(DEBUG){
            System.out.println("Population evolved. Child generation #"+generationTracker+"\n");
        }
    }

    private static double refresh(Stack<Integer> fitalg, Map<Double, Stack<Integer>> newPop, double newKey){
        if(popContains(fitalg, newPop)){
            //If it's a fit algorithm but we already have it, mutate it heavily
            if(rand.nextBoolean()){
                fitalg = Generator.mutateInsanely(fitalg);
            }
            else{
                fitalg = Generator.getRPNInstructions(targetInstructionLength);
            }
            newPop.put(newKey, fitalg);
        }
        else{
            newPop.put(newKey, fitalg);
        }
        return newKey + 1;
    }

    private static boolean popContains(Stack<Integer> algorithm, Map<Double, Stack<Integer>> pop){
        boolean outcome = false;
        for(double i = 0.0; i < pop.size(); i++){
            if(algorithm.size() == pop.get(i).size()){
                outcome = true;
                for(int j = 0; j < algorithm.size(); j++){
                    if(algorithm.get(j) != pop.get(i).get(j)){
                        outcome = false;
                    }
                }
                if(outcome){
                    return true;
                }
            }
        }
        return false;
    }

    private static void mutate(Stack<Integer> algorithm){
        for(int i = 0; i < algorithm.size(); i++){
            if(rand.nextInt(1000) < new Double(mutationRate*1000).intValue()){
                if(rand.nextInt(10) < 1){
                    algorithm = Generator.mutateInsanely(algorithm);
                }
                else{
                    algorithm.set(i, Utility.getRandomMutation(algorithm.get(i)));
                }
            }
        }
    }

    public static double selectFitAlgorithm(double current){
        int totalWeight = weights.values().stream().reduce(0, Integer::sum);

        int roulette = rand.nextInt(totalWeight);
        for(Map.Entry<Double, Integer> e : weights.entrySet()){
            rouletteCounter += e.getValue();
            if(rouletteCounter >= roulette){
                rouletteCounter = 0;
                return e.getKey();
            }
        }
        try {
            throw new Exception("Inexplicably, the sum of all weights is strictly less than the sum of all weights.");
        } catch (Exception e) {
            System.out.println("Counter vs size: "+rouletteCounter+", "+totalWeight);
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

    public static Map<Integer, Stack<Integer>> getWeightedChromosomes() {
        Map<Integer, Stack<Integer>> outcome = new HashMap<>();
        weights.entrySet().stream().sorted((e1, e2) -> Integer.compare(e1.getValue(), e2.getValue()))
                                   .forEach(entry -> outcome.put(entry.getValue(), population.get(entry.getKey())));
        return outcome;
    }
}
