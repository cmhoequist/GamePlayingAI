package justin;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Justin's Sandbox
 * Reference: https://github.com/tdunning/MiA
 */
public class Sandbox {

    public static void main(String[] args)
    {
        File modelFile = null;
        if (args.length > 0)
            modelFile = new File(args[0]);
        if(modelFile == null || !modelFile.exists())
            modelFile = new File("intro.csv");
        if(!modelFile.exists()) {
            System.err.println("Please, specify name of file, or put file 'input.csv' into current directory!");
            System.exit(1);
        }
        DataModel model = null;
        try {
            model = new FileDataModel(modelFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        UserSimilarity similarity = null;
        try {
            if (model != null) {
                similarity = new PearsonCorrelationSimilarity(model);
            }
        } catch (TasteException e) {
            e.printStackTrace();
        }
        UserNeighborhood neighborhood =
                null;
        try {
            assert model != null;
            neighborhood = new NearestNUserNeighborhood(2, similarity, model);
        } catch (TasteException e) {
            e.printStackTrace();
        }

        Recommender recommender = new GenericUserBasedRecommender(
                model, neighborhood, similarity);

        List<RecommendedItem> recommendations =
                null;
        try {
            recommendations = recommender.recommend(1, 1);
        } catch (TasteException e) {
            e.printStackTrace();
        }

        if (recommendations != null) {
            for (RecommendedItem recommendation : recommendations) {
                System.out.println(recommendation);
            }
        }


    }

}
