import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class RecommendIntro {
    public static void main(String[] args) throws IOException, TasteException {

        DataModel dataModel = new FileDataModel(new File("data/movies.csv"));

        UserSimilarity similarity = new EuclideanDistanceSimilarity(dataModel);

        UserNeighborhood neighborhood = new NearestNUserNeighborhood(2, similarity, dataModel);

        Recommender recommender = new GenericUserBasedRecommender(dataModel, neighborhood, similarity);   //create recommend engine

        List<RecommendedItem> recommendations = recommender.recommend(1, 10);                        //recommend 3 item cho user 1

        for (RecommendedItem recommendation : recommendations) {
            System.out.println(recommendation);
        }
    }
}
