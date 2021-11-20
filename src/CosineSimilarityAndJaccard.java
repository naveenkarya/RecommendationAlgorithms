import java.util.List;
import java.util.Map;

public class CosineSimilarityAndJaccard extends RatingAlgorithm {

    private RatingAlgorithm algorithm1;
    private RatingAlgorithm algorithm2;

    public CosineSimilarityAndJaccard(List<List<Double>> trainingData, List<Double> avgUserRatingsForMovies) {
        super(trainingData, avgUserRatingsForMovies);
        this.algorithm1 = new UserBasedCosineSimilarity(trainingData, avgUserRatingsForMovies);
        this.algorithm2 = new CustomRatingAlgorithm(trainingData, avgUserRatingsForMovies, CustomRatingAlgorithm.JACCARD_SIMILARITY, "", 0.0);
    }

    @Override
    public double estimateUserRatingForAMovie(Map<Integer, Double> queryUserRatingMap, int queryMovieId) {
        double ratingFromAlgorithm1 = algorithm1.estimateUserRatingForAMovie(queryUserRatingMap, queryMovieId);
        double ratingFromAlgorithm2 = algorithm2.estimateUserRatingForAMovie(queryUserRatingMap, queryMovieId);
        double avgRating = ((3 * ratingFromAlgorithm1) + (2 * ratingFromAlgorithm2)) / 5;
        return avgRating;
    }
}
