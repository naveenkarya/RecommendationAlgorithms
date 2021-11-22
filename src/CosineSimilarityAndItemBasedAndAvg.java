import java.util.List;
import java.util.Map;

public class CosineSimilarityAndItemBasedAndAvg extends RatingAlgorithm {

    private RatingAlgorithm algorithm1;
    private RatingAlgorithm algorithm2;
    private RatingAlgorithm algorithm3;

    public CosineSimilarityAndItemBasedAndAvg(List<List<Double>> trainingData, List<Double> avgUserRatingsForMovies) {
        super(trainingData, avgUserRatingsForMovies);
        this.algorithm1 = new UserBasedCosineSimilarity(trainingData, avgUserRatingsForMovies);
        this.algorithm2 = new ItemBasedFiltering(trainingData, avgUserRatingsForMovies);
        this.algorithm3 = new UserAndItemAverage(trainingData, avgUserRatingsForMovies);
    }

    @Override
    public double estimateUserRatingForAMovie(Map<Integer, Double> queryUserRatingMap, int queryMovieId) {
        double ratingFromAlgorithm1 = algorithm1.estimateUserRatingForAMovie(queryUserRatingMap, queryMovieId);
        double ratingFromAlgorithm2 = algorithm2.estimateUserRatingForAMovie(queryUserRatingMap, queryMovieId);
        double ratingFromAlgorithm3 = algorithm3.estimateUserRatingForAMovie(queryUserRatingMap, queryMovieId);
        double avgRating = ((3 * ratingFromAlgorithm1) + (2 * ratingFromAlgorithm2) + (3 * ratingFromAlgorithm3)) / 8;
        return avgRating;
    }
}
