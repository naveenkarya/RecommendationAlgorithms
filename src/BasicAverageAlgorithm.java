import java.util.*;

public class BasicAverageAlgorithm extends RatingAlgorithm {

    public BasicAverageAlgorithm(List<List<Double>> trainingData, List<Double> avgUserRatingsForMovies) {
        super(trainingData, avgUserRatingsForMovies);
    }

    @Override
    public double estimateUserRatingForAMovie(Map<Integer, Double> queryUserRatingMap, int queryMovieId) {
        double avgUserRatingForQueryMovie = avgUserRatingsForMovies.get(queryMovieId - 1);
        if (avgUserRatingForQueryMovie == 0) {
            double queryUserActualAvgRating =
                    queryUserRatingMap.values().stream().mapToDouble(a -> a).average().getAsDouble();
            return queryUserActualAvgRating;
        }
        else return avgUserRatingForQueryMovie;
    }
}
