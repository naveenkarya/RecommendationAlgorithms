import java.util.List;
import java.util.Map;

public class BasicAverageAlgorithm2 extends RatingAlgorithm {

    public BasicAverageAlgorithm2(List<List<Double>> trainingData, List<Double> avgUserRatingsForMovies) {
        super(trainingData, avgUserRatingsForMovies);
    }

    @Override
    public double estimateUserRatingForAMovie(Map<Integer, Double> queryUserRatingMap, int queryMovieId) {
        double queryUserActualAvgRating =
                queryUserRatingMap.values().stream().mapToDouble(a -> a).average().getAsDouble();
        double avgUserRatingForQueryMovie = avgUserRatingsForMovies.get(queryMovieId - 1);
        if (avgUserRatingForQueryMovie == 0) {
            return queryUserActualAvgRating;
        }
        else return (avgUserRatingForQueryMovie + queryUserActualAvgRating)/2;
    }
}
