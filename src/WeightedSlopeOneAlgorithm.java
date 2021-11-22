import java.util.*;

public class WeightedSlopeOneAlgorithm extends RatingAlgorithm {
    private double diffMatrix[][];
    private int countMatrix[][];

    public WeightedSlopeOneAlgorithm(List<List<Double>> trainingData, List<Double> avgUserRatingsForMovies) {
        super(trainingData, avgUserRatingsForMovies);
        int totalNumberOfMovies = trainingData.get(0).size();
        this.diffMatrix = new double[totalNumberOfMovies][totalNumberOfMovies];
        this.countMatrix = new int[totalNumberOfMovies][totalNumberOfMovies];
        for(int i = 0; i < totalNumberOfMovies; i++) {
            for(int j = 0; j < totalNumberOfMovies; j++) {
                if(i == j) diffMatrix[i][j] = 0;
                else if (diffMatrix[j][i] != 0){
                    diffMatrix[i][j] = -1.0 * diffMatrix[j][i];
                    countMatrix[i][j] = countMatrix[j][i];
                }
                else {
                    double sum = 0.0;
                    int count = 0;
                    for(int k = 0; k < trainingData.size(); k++) {
                        double iRating = trainingData.get(k).get(i);
                        double jRating = trainingData.get(k).get(j);
                        if(iRating != 0 && jRating != 0) {
                            sum = sum + iRating - jRating;
                            count++;
                        }
                    }
                    countMatrix[i][j] = count;
                    if(sum == 0) diffMatrix[i][j] = 0.0;
                    else diffMatrix[i][j] = sum/count;
                }
            }
        }
    }

    @Override
    public double estimateUserRatingForAMovie(Map<Integer, Double> queryUserRatingMap, int queryMovieId) {
        double queryUserActualAvgRating = queryUserRatingMap.values().stream().mapToDouble(a -> a).average().getAsDouble();
        double sum = 0.0;
        int count = 0;
        for(Map.Entry<Integer, Double> entry : queryUserRatingMap.entrySet()) {
            int movieId = entry.getKey();
            double movieRating = entry.getValue();
            sum = sum + ((diffMatrix[queryMovieId - 1][movieId - 1] + movieRating) * countMatrix[queryMovieId - 1][movieId - 1]);
            count = count + countMatrix[queryMovieId - 1][movieId - 1];
        }
        double rating = 0;
        if(sum >= 0 && count >= 5) rating = sum/count;
        if(rating != 0) {
            return rating;
        }
        else {
            double avgUserRatingForQueryMovie = avgUserRatingsForMovies.get(queryMovieId - 1);
            if(avgUserRatingForQueryMovie == 0) return queryUserActualAvgRating;
            else return (queryUserActualAvgRating + avgUserRatingForQueryMovie) / 2;
        }
    }

}
