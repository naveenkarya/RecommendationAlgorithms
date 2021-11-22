import java.util.List;
import java.util.Map;

public class WeightedSlopeWithMovieAvg extends RatingAlgorithm {

    private RatingAlgorithm algorithm1;
    private RatingAlgorithm algorithm2;

    public WeightedSlopeWithMovieAvg(List<List<Double>> trainingData, List<Double> avgUserRatingsForMovies) {
        super(trainingData, avgUserRatingsForMovies);
        this.algorithm1 = new WeightedSlopeOneAlgorithm(trainingData, avgUserRatingsForMovies);
        this.algorithm2 = new UserAndItemAverage(trainingData, avgUserRatingsForMovies);
    }

    @Override
    public double estimateUserRatingForAMovie(Map<Integer, Double> queryUserRatingMap, int queryMovieId) {
        double ratingFromAlgorithm1 = algorithm1.estimateUserRatingForAMovie(queryUserRatingMap, queryMovieId);
        double ratingFromAlgorithm2 = algorithm2.estimateUserRatingForAMovie(queryUserRatingMap, queryMovieId);
        double avgRating = (ratingFromAlgorithm1 + ratingFromAlgorithm2) / 2;
        return avgRating;
    }
}
